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
package me.zhengjie.modules.maint.service;

import me.zhengjie.modules.maint.domain.Deploy;
import me.zhengjie.modules.maint.domain.DeployHistory;
import me.zhengjie.modules.maint.service.dto.DeployDto;
import me.zhengjie.modules.maint.service.dto.DeployQueryCriteria;
import me.zhengjie.utils.PageResult;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
* @author zhanghouying
* @date 2019-08-24
*/
public interface DeployService {

    /**
     * Paged query
     * @param criteria Condition
     * @param pageable Pagination parameters
     * @return /
     */
    PageResult<DeployDto> queryAll(DeployQueryCriteria criteria, Pageable pageable);

    /**
     * Query all data
     * @param criteria Condition
     * @return /
     */
    List<DeployDto> queryAll(DeployQueryCriteria criteria);

    /**
     * Query by ID
     * @param id /
     * @return /
     */
    DeployDto findById(Long id);

    /**
     * Create
     * @param resources /
     */
    void create(Deploy resources);


    /**
     * Edit
     * @param resources /
     */
    void update(Deploy resources);

    /**
     * Delete
     * @param ids /
     */
    void delete(Set<Long> ids);

	/**
	 * Deploy files to the server
	 * @param fileSavePath File path
	 * @param appId App ID
     */
	void deploy(String fileSavePath, Long appId);

    /**
     * Query deployment status
     * @param resources /
     * @return /
     */
    String serverStatus(Deploy resources);
    /**
     * Start service
     * @param resources /
     * @return /
     */
    String startServer(Deploy resources);
    /**
     * Stop service
     * @param resources /
     * @return /
     */
    String stopServer(Deploy resources);

    /**
     * Stop service
     * @param resources /
     * @return /
     */
    String serverReduction(DeployHistory resources);

    /**
     * Export data
     * @param queryAll /
     * @param response /
     * @throws IOException /
     */
    void download(List<DeployDto> queryAll, HttpServletResponse response) throws IOException;
}
