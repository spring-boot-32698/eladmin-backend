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

import me.zhengjie.modules.maint.domain.ServerDeploy;
import me.zhengjie.modules.maint.service.dto.ServerDeployDto;
import me.zhengjie.modules.maint.service.dto.ServerDeployQueryCriteria;
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
public interface ServerDeployService {

    /**
     * Paged query
     * @param criteria Condition
     * @param pageable Pagination parameters
     * @return /
     */
    PageResult<ServerDeployDto> queryAll(ServerDeployQueryCriteria criteria, Pageable pageable);

    /**
     * Query all data
     * @param criteria Condition
     * @return /
     */
    List<ServerDeployDto> queryAll(ServerDeployQueryCriteria criteria);

    /**
     * Query by ID
     * @param id /
     * @return /
     */
    ServerDeployDto findById(Long id);

    /**
     * Create
     * @param resources /
     */
    void create(ServerDeploy resources);

    /**
     * Edit
     * @param resources /
     */
    void update(ServerDeploy resources);

    /**
     * Delete
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * Query by IP
     * @param ip /
     * @return /
     */
    ServerDeployDto findByIp(String ip);

	/**
	 * Test server login
	 * @param resources /
	 * @return /
	 */
	Boolean testConnect(ServerDeploy resources);

    /**
     * Export data
     * @param queryAll /
     * @param response /
     * @throws IOException /
     */
    void download(List<ServerDeployDto> queryAll, HttpServletResponse response) throws IOException;
}
