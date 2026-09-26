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

import me.zhengjie.modules.security.service.dto.AuthorityDto;
import me.zhengjie.modules.system.domain.Role;
import me.zhengjie.modules.system.service.dto.RoleDto;
import me.zhengjie.modules.system.service.dto.RoleQueryCriteria;
import me.zhengjie.modules.system.service.dto.RoleSmallDto;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.utils.PageResult;
import org.springframework.data.domain.Pageable;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2018-12-03
 */
public interface RoleService {

    /**
     * Query all data
     * @return /
     */
    List<RoleDto> queryAll();

    /**
     * Query by ID
     * @param id /
     * @return /
     */
    RoleDto findById(long id);

    /**
     * Create
     * @param resources /
     */
    void create(Role resources);

    /**
     * Edit
     * @param resources /
     */
    void update(Role resources);

    /**
     * Delete
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * Query by user ID
     * @param userId User ID
     * @return /
     */
    List<RoleSmallDto> findByUsersId(Long userId);

    /**
     * Query the role level by role
     * @param roles /
     * @return /
     */
    Integer findByRoles(Set<Role> roles);

    /**
     * Edit bound menus
     * @param resources /
     * @param roleDTO /
     */
    void updateMenu(Role resources, RoleDto roleDTO);

    /**
     * Unbind menu
     * @param id /
     */
    void untiedMenu(Long id);

    /**
     * To be queried with conditions and pagination
     * @param criteria Condition
     * @param pageable Pagination parameters
     * @return /
     */
    PageResult<RoleDto> queryAll(RoleQueryCriteria criteria, Pageable pageable);

    /**
     * Query all
     * @param criteria Condition
     * @return /
     */
    List<RoleDto> queryAll(RoleQueryCriteria criteria);

    /**
     * Export data
     * @param queryAll Data to export
     * @param response /
     * @throws IOException /
     */
    void download(List<RoleDto> queryAll, HttpServletResponse response) throws IOException;

    /**
     * Get user permission info
     * @param user User info
     * @return Permission info
     */
    List<AuthorityDto> buildPermissions(UserDto user);

    /**
     * Validate whether it is associated with users
     * @param ids /
     */
    void verification(Set<Long> ids);

    /**
     * Query by menu ID
     * @param menuIds /
     * @return /
     */
    List<Role> findInMenuId(List<Long> menuIds);
}
