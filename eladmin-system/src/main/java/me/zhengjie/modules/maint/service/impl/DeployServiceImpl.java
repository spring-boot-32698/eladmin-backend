/*
 *  Copyright 2019-2025 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.maint.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.maint.domain.App;
import me.zhengjie.modules.maint.domain.Deploy;
import me.zhengjie.modules.maint.domain.DeployHistory;
import me.zhengjie.modules.maint.domain.ServerDeploy;
import me.zhengjie.modules.maint.repository.DeployRepository;
import me.zhengjie.modules.maint.service.DeployHistoryService;
import me.zhengjie.modules.maint.service.DeployService;
import me.zhengjie.modules.maint.service.ServerDeployService;
import me.zhengjie.modules.maint.service.dto.AppDto;
import me.zhengjie.modules.maint.service.dto.DeployDto;
import me.zhengjie.modules.maint.service.dto.DeployQueryCriteria;
import me.zhengjie.modules.maint.service.dto.ServerDeployDto;
import me.zhengjie.modules.maint.service.mapstruct.DeployMapper;
import me.zhengjie.modules.maint.util.ExecuteShellUtil;
import me.zhengjie.modules.maint.util.ScpClientUtil;
import me.zhengjie.modules.maint.websocket.MsgType;
import me.zhengjie.modules.maint.websocket.SocketMsg;
import me.zhengjie.modules.maint.websocket.WebSocketServer;
import me.zhengjie.utils.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author zhanghouying
 * @date 2019-08-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeployServiceImpl implements DeployService {

	private final String FILE_SEPARATOR = "/";
	private final DeployRepository deployRepository;
	private final DeployMapper deployMapper;
	private final ServerDeployService serverDeployService;
	private final DeployHistoryService deployHistoryService;
	/**
	 * Loop count
	 */
	private final Integer count = 30;


	@Override
	public PageResult<DeployDto> queryAll(DeployQueryCriteria criteria, Pageable pageable) {
		Page<Deploy> page = deployRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
		return PageUtil.toPage(page.map(deployMapper::toDto));
	}

	@Override
	public List<DeployDto> queryAll(DeployQueryCriteria criteria) {
		return deployMapper.toDto(deployRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
	}

	@Override
	public DeployDto findById(Long id) {
		Deploy deploy = deployRepository.findById(id).orElseGet(Deploy::new);
		ValidationUtil.isNull(deploy.getId(), "Deploy", "id", id);
		return deployMapper.toDto(deploy);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void create(Deploy resources) {
		deployRepository.save(resources);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(Deploy resources) {
		Deploy deploy = deployRepository.findById(resources.getId()).orElseGet(Deploy::new);
		ValidationUtil.isNull(deploy.getId(), "Deploy", "id", resources.getId());
		deploy.copy(resources);
		deployRepository.save(deploy);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Set<Long> ids) {
		for (Long id : ids) {
			deployRepository.deleteById(id);
		}
	}

	@Override
	public void deploy(String fileSavePath, Long id) {
		deployApp(fileSavePath, id);
	}

	/**
	 * @param fileSavePath Local path
	 * @param id ID
	 */
	private void deployApp(String fileSavePath, Long id) {

		DeployDto deploy = findById(id);
		if (deploy == null) {
			sendMsg("Deployment info does not exist", MsgType.ERROR);
			throw new BadRequestException("Deployment info does not exist");
		}
		AppDto app = deploy.getApp();
		if (app == null) {
			sendMsg("Application info for the package does not exist", MsgType.ERROR);
			throw new BadRequestException("Application info for the package does not exist");
		}
		int port = app.getPort();
		//This is the server deployment path
		String uploadPath = app.getUploadPath();
		StringBuilder sb = new StringBuilder();
		String msg;
		Set<ServerDeployDto> deploys = deploy.getDeploys();
		for (ServerDeployDto deployDTO : deploys) {
			String ip = deployDTO.getIp();
			ExecuteShellUtil executeShellUtil = getExecuteShellUtil(ip);
			//Check whether this is the first deployment
			boolean flag = checkFile(executeShellUtil, app);
			//Step 1: confirm that this directory exists on the server
			executeShellUtil.execute("mkdir -p " + app.getUploadPath());
			executeShellUtil.execute("mkdir -p " + app.getBackupPath());
			executeShellUtil.execute("mkdir -p " + app.getDeployPath());
			//Upload file
			msg = String.format("Log in to the server:%s", ip);
			ScpClientUtil scpClientUtil = getScpClientUtil(ip);
			log.info(msg);
			sendMsg(msg, MsgType.INFO);
			msg = String.format("Upload file to server:%s<br>Directory:%s, please wait...", ip, uploadPath);
			sendMsg(msg, MsgType.INFO);
			scpClientUtil.putFile(fileSavePath, uploadPath);
			if (flag) {
				sendMsg("Stop the original application", MsgType.INFO);
				//Stop application
				stopApp(port, executeShellUtil);
				sendMsg("Back up the original application", MsgType.INFO);
				//Back up application
				backupApp(executeShellUtil, ip, app.getDeployPath()+FILE_SEPARATOR, app.getName(), app.getBackupPath()+FILE_SEPARATOR, id);
			}
			sendMsg("Deploy application", MsgType.INFO);
			//Deployment file,and start the application
			String deployScript = app.getDeployScript();
			executeShellUtil.execute(deployScript);
			sleep(3);
			sendMsg("The application is being deployed, please wait for the result or check the deployment status manually later", MsgType.INFO);
			int i  = 0;
			boolean result = false;
			// Since starting the application takes time, poll the status in a loop; if it exceeds 30 attempts, treat it as a failed start
			while (i++ < count){
				result = checkIsRunningStatus(port, executeShellUtil);
				if(result){
					break;
				}
				// Sleep 6 seconds
				sleep(6);
			}
			sb.append("Server:").append(deployDTO.getName()).append("<br>App:").append(app.getName());
			sendResultMsg(result, sb);
			executeShellUtil.close();
		}
	}

	private void sleep(int second) {
		try {
			Thread.sleep(second * 1000);
		} catch (InterruptedException e) {
			log.error(e.getMessage(),e);
		}
	}

	private void backupApp(ExecuteShellUtil executeShellUtil, String ip, String fileSavePath, String appName, String backupPath, Long id) {
		String deployDate = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
		StringBuilder sb = new StringBuilder();
		backupPath += appName + FILE_SEPARATOR + deployDate + "\n";
		sb.append("mkdir -p ").append(backupPath);
		sb.append("mv -f ").append(fileSavePath);
		sb.append(appName).append(" ").append(backupPath);
		log.info("Application backup script:" + sb.toString());
		executeShellUtil.execute(sb.toString());
		//Save the restore info to the database
		DeployHistory deployHistory = new DeployHistory();
		deployHistory.setAppName(appName);
		deployHistory.setDeployUser(SecurityUtils.getCurrentUsername());
		deployHistory.setIp(ip);
		deployHistory.setDeployId(id);
		deployHistoryService.create(deployHistory);
	}

	/**
	 * Stop App
	 *
	 * @param port Port
	 * @param executeShellUtil /
	 */
	private void stopApp(int port, ExecuteShellUtil executeShellUtil) {
		//Send stop command
		executeShellUtil.execute(String.format("lsof -i :%d|grep -v \"PID\"|awk '{print \"kill -9\",$2}'|sh", port));

	}

	/**
	 * Whether the program on the specified port is running
	 *
	 * @param port Port
	 * @param executeShellUtil /
	 * @return true Running; false means stopped
	 */
	private boolean checkIsRunningStatus(int port, ExecuteShellUtil executeShellUtil) {
		String result = executeShellUtil.executeForResult(String.format("fuser -n tcp %d", port));
		return result.indexOf("/tcp:")>0;
	}

	private void sendMsg(String msg, MsgType msgType) {
		try {
			WebSocketServer.sendInfo(new SocketMsg(msg, msgType), "deploy");
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
	}

	@Override
	public String serverStatus(Deploy resources) {
		Set<ServerDeploy> serverDeploys = resources.getDeploys();
		App app = resources.getApp();
		for (ServerDeploy serverDeploy : serverDeploys) {
			StringBuilder sb = new StringBuilder();
			ExecuteShellUtil executeShellUtil = getExecuteShellUtil(serverDeploy.getIp());
			sb.append("Server:").append(serverDeploy.getName()).append("<br>App:").append(app.getName());
			boolean result = checkIsRunningStatus(app.getPort(), executeShellUtil);
			if (result) {
				sb.append("<br>Running");
				sendMsg(sb.toString(), MsgType.INFO);
			} else {
				sb.append("<br>Stopped!");
				sendMsg(sb.toString(), MsgType.ERROR);
			}
			log.info(sb.toString());
			executeShellUtil.close();
		}
		return "Execution finished";
	}

	private boolean checkFile(ExecuteShellUtil executeShellUtil, AppDto app) {
		String deployPath = app.getDeployPath();
		String appName = app.getName();
		// Use a safe way to execute commands, avoiding direct string concatenation,https://github.com/elunez/eladmin/issues/873
		String[] command = {"find", deployPath, "-name", appName};
		String result = executeShellUtil.executeForResult(Arrays.toString(command));
		return result.contains(appName);
	}

	/**
	 * Start service
	 * @param resources /
	 * @return /
	 */
	@Override
	public String startServer(Deploy resources) {
		Set<ServerDeploy> deploys = resources.getDeploys();
		App app = resources.getApp();
		for (ServerDeploy deploy : deploys) {
			StringBuilder sb = new StringBuilder();
			ExecuteShellUtil executeShellUtil = getExecuteShellUtil(deploy.getIp());
			//To prevent duplicate starts, stop the application first
			stopApp(app.getPort(), executeShellUtil);
			sb.append("Server:").append(deploy.getName()).append("<br>App:").append(app.getName());
			sendMsg("Send start command", MsgType.INFO);
			executeShellUtil.execute(app.getStartScript());
			sleep(3);
			sendMsg("The application is starting, please wait for the result or check the running status manually later", MsgType.INFO);
			int i  = 0;
			boolean result = false;
			// Since starting the application takes time, poll the status in a loop; if it exceeds 30 attempts, treat it as a failed start
			while (i++ < count){
				result = checkIsRunningStatus(app.getPort(), executeShellUtil);
				if(result){
					break;
				}
				// Sleep 6 seconds
				sleep(6);
			}
			sendResultMsg(result, sb);
			log.info(sb.toString());
			executeShellUtil.close();
		}
		return "Execution finished";
	}

	/**
	 * Stop service
	 * @param resources /
	 * @return /
	 */
	@Override
	public String stopServer(Deploy resources) {
		Set<ServerDeploy> deploys = resources.getDeploys();
		App app = resources.getApp();
		for (ServerDeploy deploy : deploys) {
			StringBuilder sb = new StringBuilder();
			ExecuteShellUtil executeShellUtil = getExecuteShellUtil(deploy.getIp());
			sb.append("Server:").append(deploy.getName()).append("<br>App:").append(app.getName());
			sendMsg("Send stop command", MsgType.INFO);
			//Stop application
			stopApp(app.getPort(), executeShellUtil);
			sleep(1);
			boolean result = checkIsRunningStatus(app.getPort(), executeShellUtil);
			if (result) {
				sb.append("<br>Close failed!");
				sendMsg(sb.toString(), MsgType.ERROR);
			} else {
				sb.append("<br>Closed successfully!");
				sendMsg(sb.toString(), MsgType.INFO);
			}
			log.info(sb.toString());
			executeShellUtil.close();
		}
		return "Execution finished";
	}

	@Override
	public String serverReduction(DeployHistory resources) {
		Long deployId = resources.getDeployId();
		Deploy deployInfo = deployRepository.findById(deployId).orElseGet(Deploy::new);
		String deployDate = DateUtil.format(resources.getDeployDate(), DatePattern.PURE_DATETIME_PATTERN);
		App app = deployInfo.getApp();
		if (app == null) {
			sendMsg("App info does not exist:" + resources.getAppName(), MsgType.ERROR);
			throw new BadRequestException("App info does not exist:" + resources.getAppName());
		}
		String backupPath = app.getBackupPath()+FILE_SEPARATOR;
		backupPath += resources.getAppName() + FILE_SEPARATOR + deployDate;
		//This is the server deployment path
		String deployPath = app.getDeployPath();
		String ip = resources.getIp();
		ExecuteShellUtil executeShellUtil = getExecuteShellUtil(ip);
		String msg;

		msg = String.format("Log in to the server:%s", ip);
		log.info(msg);
		sendMsg(msg, MsgType.INFO);
		sendMsg("Stop the original application", MsgType.INFO);
		//Stop application
		stopApp(app.getPort(), executeShellUtil);
		//Delete the original application
		sendMsg("Delete app", MsgType.INFO);
		executeShellUtil.execute("rm -rf " + deployPath + FILE_SEPARATOR + resources.getAppName());
		//Restore application
		sendMsg("Restore application", MsgType.INFO);
		executeShellUtil.execute("cp -r " + backupPath + "/. " + deployPath);
		sendMsg("Start application", MsgType.INFO);
		executeShellUtil.execute(app.getStartScript());
		sendMsg("The application is starting, please wait for the result or check the startup status manually later", MsgType.INFO);
		int i  = 0;
		boolean result = false;
		// Since starting the application takes time, poll the status in a loop; if it exceeds 30 attempts, treat it as a failed start
		while (i++ < count){
			result = checkIsRunningStatus(app.getPort(), executeShellUtil);
			if(result){
				break;
			}
			// Sleep 6 seconds
			sleep(6);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Server:").append(ip).append("<br>App:").append(resources.getAppName());
		sendResultMsg(result, sb);
		executeShellUtil.close();
		return "";
	}

	private ExecuteShellUtil getExecuteShellUtil(String ip) {
		ServerDeployDto serverDeployDTO = serverDeployService.findByIp(ip);
		if (serverDeployDTO == null) {
			sendMsg("IP The corresponding server info does not exist:" + ip, MsgType.ERROR);
			throw new BadRequestException("IP The corresponding server info does not exist:" + ip);
		}
		return new ExecuteShellUtil(ip, serverDeployDTO.getAccount(), serverDeployDTO.getPassword(),serverDeployDTO.getPort());
	}

	private ScpClientUtil getScpClientUtil(String ip) {
		ServerDeployDto serverDeployDTO = serverDeployService.findByIp(ip);
		if (serverDeployDTO == null) {
			sendMsg("IP The corresponding server info does not exist:" + ip, MsgType.ERROR);
			throw new BadRequestException("IP The corresponding server info does not exist:" + ip);
		}
		return ScpClientUtil.getInstance(ip, serverDeployDTO.getPort(), serverDeployDTO.getAccount(), serverDeployDTO.getPassword());
	}

	private void sendResultMsg(boolean result, StringBuilder sb) {
		if (result) {
			sb.append("<br>Started successfully!");
			sendMsg(sb.toString(), MsgType.INFO);
		} else {
			sb.append("<br>Start failed!");
			sendMsg(sb.toString(), MsgType.ERROR);
		}
	}

	@Override
	public void download(List<DeployDto> queryAll, HttpServletResponse response) throws IOException {
		List<Map<String, Object>> list = new ArrayList<>();
		for (DeployDto deployDto : queryAll) {
			Map<String,Object> map = new LinkedHashMap<>();
			map.put("App name", deployDto.getApp().getName());
			map.put("Server", deployDto.getServers());
			map.put("Deployment date", deployDto.getCreateTime());
			list.add(map);
		}
		FileUtil.downloadExcel(list, response);
	}
}
