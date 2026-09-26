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
package me.zhengjie.modules.system.service;

import me.zhengjie.modules.system.domain.Dept;
import me.zhengjie.modules.system.service.dto.DeptDto;
import me.zhengjie.modules.system.service.dto.DeptQueryCriteria;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
* @author Zheng Jie
* @date 2019-03-25
*/
public interface DeptService {

    /**
     * Query all data
     * @param criteria Condition
     * @param isQuery /
     * @throws Exception /
     * @return /
     */
    List<DeptDto> queryAll(DeptQueryCriteria criteria, Boolean isQuery) throws Exception;

    /**
     * Query by ID
     * @param id /
     * @return /
     */
    DeptDto findById(Long id);

    /**
     * Create
     * @param resources /
     */
    void create(Dept resources);

    /**
     * Edit
     * @param resources /
     */
    void update(Dept resources);

    /**
     * Delete
     * @param deptDtos /
     *
     */
    void delete(Set<DeptDto> deptDtos);

    /**
     * Query by PID
     * @param pid /
     * @return /
     */
    List<Dept> findByPid(long pid);

    /**
     * Query by role ID
     * @param id /
     * @return /
     */
    Set<Dept> findByRoleId(Long id);

    /**
     * Export data
     * @param queryAll Data to export
     * @param response /
     * @throws IOException /
     */
    void download(List<DeptDto> queryAll, HttpServletResponse response) throws IOException;

    /**
     * Get the departments to delete
     * @param deptList /
     * @param deptDtos /
     * @return /
     */
    Set<DeptDto> getDeleteDepts(List<Dept> deptList, Set<DeptDto> deptDtos);

    /**
     * Get sibling and parent data by ID
     * @param deptDto /
     * @param depts /
     * @return /
     */
    List<DeptDto> getSuperior(DeptDto deptDto, List<Dept> depts);

    /**
     * Build tree data
     * @param deptDtos /
     * @return /
     */
    Object buildTree(List<DeptDto> deptDtos);

    /**
     * Get
     * @param deptList /
     * @return /
     */
    List<Long> getDeptChildren(List<Dept> deptList);

    /**
     * Validate whether it is associated with roles or users
     * @param deptDtos /
     */
    void verification(Set<DeptDto> deptDtos);
}