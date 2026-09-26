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
package me.zhengjie.modules.quartz.utils;

import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import me.zhengjie.domain.vo.EmailVo;
import me.zhengjie.modules.quartz.domain.QuartzJob;
import me.zhengjie.modules.quartz.domain.QuartzLog;
import me.zhengjie.modules.quartz.repository.QuartzLogRepository;
import me.zhengjie.modules.quartz.service.QuartzJobService;
import me.zhengjie.service.EmailService;
import me.zhengjie.utils.RedisUtils;
import me.zhengjie.utils.SpringBeanHolder;
import me.zhengjie.utils.StringUtils;
import me.zhengjie.utils.ThrowableUtil;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.QuartzJobBean;
import java.util.*;
import java.util.concurrent.*;

/**
 * Refer to renren-fast,<a href="https://gitee.com/renrenio/renren-security">...</a>
 * @author /
 * @date 2019-01-07
 */
public class ExecutionJob extends QuartzJobBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    // For reference only, the thread pool parameters can be customized according to task execution
    private final ThreadPoolTaskExecutor executor = SpringBeanHolder.getBean("taskAsync");


    @Override
    public void executeInternal(JobExecutionContext context) {
        // Get tasks
        QuartzJob quartzJob = (QuartzJob) context.getMergedJobDataMap().get(QuartzJob.JOB_KEY);
        // Get spring bean
        QuartzLogRepository quartzLogRepository = SpringBeanHolder.getBean(QuartzLogRepository.class);
        QuartzJobService quartzJobService = SpringBeanHolder.getBean(QuartzJobService.class);
        RedisUtils redisUtils = SpringBeanHolder.getBean(RedisUtils.class);

        String uuid = quartzJob.getUuid();

        QuartzLog log = new QuartzLog();
        log.setJobName(quartzJob.getJobName());
        log.setBeanName(quartzJob.getBeanName());
        log.setMethodName(quartzJob.getMethodName());
        log.setParams(quartzJob.getParams());
        long startTime = System.currentTimeMillis();
        log.setCronExpression(quartzJob.getCronExpression());
        try {
            // Execute task
            QuartzRunnable task = new QuartzRunnable(quartzJob.getBeanName(), quartzJob.getMethodName(), quartzJob.getParams());
            Future<?> future = executor.submit(task);
            future.get();
            long times = System.currentTimeMillis() - startTime;
            log.setTime(times);
            if(StringUtils.isNotBlank(uuid)) {
                redisUtils.set(uuid, true);
            }
            // Task status
            log.setIsSuccess(true);
            logger.info("Task executed successfully, task name:{}, Execution time:{}Milliseconds", quartzJob.getJobName(), times);
            // Check whether sub-tasks exist
            if(StringUtils.isNotBlank(quartzJob.getSubTask())){
                String[] tasks = quartzJob.getSubTask().split("[,, ]");
                // Execute sub-task
                quartzJobService.executionSubJob(tasks);
            }
        } catch (Exception e) {
            if(StringUtils.isNotBlank(uuid)) {
                redisUtils.set(uuid, false);
            }
            logger.error("Task execution failed, task name:{}", quartzJob.getJobName());
            long times = System.currentTimeMillis() - startTime;
            log.setTime(times);
            // Task status 0: success 1: failure
            log.setIsSuccess(false);
            log.setExceptionDetail(ThrowableUtil.getStackTrace(e));
            // Pause the task if it fails
            if(quartzJob.getPauseAfterFailure() != null && quartzJob.getPauseAfterFailure()){
                //Update status
                quartzJob.setIsPause(false);
                quartzJobService.updateIsPause(quartzJob);
            }
            if(quartzJob.getEmail() != null){
                EmailService emailService = SpringBeanHolder.getBean(EmailService.class);
                // Email alarm
                if(StringUtils.isNoneBlank(quartzJob.getEmail())){
                    EmailVo emailVo = taskAlarm(quartzJob, ThrowableUtil.getStackTrace(e));
                    emailService.send(emailVo, emailService.find());
                }
            }
        } finally {
            quartzLogRepository.save(log);
        }
    }

    private EmailVo taskAlarm(QuartzJob quartzJob, String msg) {
        EmailVo emailVo = new EmailVo();
        emailVo.setSubject("Scheduled task ["+ quartzJob.getJobName() +"] failed, please handle it as soon as possible!");
        Map<String, Object> data = new HashMap<>(16);
        data.put("task", quartzJob);
        data.put("msg", msg);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("taskAlarm.ftl");
        emailVo.setContent(template.render(data));
        List<String> emails = Arrays.asList(quartzJob.getEmail().split("[,, ]"));
        emailVo.setTos(emails);
        return emailVo;
    }
}
