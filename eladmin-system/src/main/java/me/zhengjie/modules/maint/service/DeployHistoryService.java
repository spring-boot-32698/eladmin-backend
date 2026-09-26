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

import me.zhengjie.modules.maint.domain.DeployHistory;
import me.zhengjie.modules.maint.service.dto.DeployHistoryDto;
import me.zhengjie.modules.maint.service.dto.DeployHistoryQueryCriteria;
import me.zhengjie.utils.PageResult;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author zhanghouying
 */
public interface DeployHistoryService {

    /**
     * Paged query
     * @param criteria Condition
     * @param pageable Pagination parameters
     * @return /
     */
    PageResult<DeployHistoryDto> queryAll(DeployHistoryQueryCriteria criteria, Pageable pageable);

    /**
     * Query all
     * @param criteria Condition
     * @return /
     */
    List<DeployHistoryDto> queryAll(DeployHistoryQueryCriteria criteria);

    /**
     * Query by ID
     * @param id /
     * @return /
     */
    DeployHistoryDto findById(String id);

    /**
     * Create
     * @param resources /
     */
    void create(DeployHistory resources);

    /**
     * Delete
     * @param ids /
     */
    void delete(Set<String> ids);

    /**
     * Export data
     * @param queryAll /
     * @param response /
     * @throws IOException /
     */
    void download(List<DeployHistoryDto> queryAll, HttpServletResponse response) throws IOException;
}
