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

import me.zhengjie.utils.PageResult;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.modules.system.service.dto.UserQueryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 */
public interface UserService {

    /**
     * Query by ID
     * @param id ID
     * @return /
     */
    UserDto findById(long id);

    /**
     * Add user
     * @param resources /
     */
    void create(User resources);

    /**
     * Edit user
     * @param resources /
     * @throws Exception /
     */
    void update(User resources) throws Exception;

    /**
     * Delete user
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * Query by username
     * @param userName /
     * @return /
     */
    UserDto findByName(String userName);

    /**
     * Query by username
     * @param userName /
     * @return /
     */
    UserDto getLoginData(String userName);

    /**
     * Change password
     * @param username Username
     * @param encryptPassword Password
     */
    void updatePass(String username, String encryptPassword);

    /**
     * Change avatar
     * @param file File
     * @return /
     */
    Map<String, String> updateAvatar(MultipartFile file);

    /**
     * Change email
     * @param username Username
     * @param email Email
     */
    void updateEmail(String username, String email);

    /**
     * Query all
     * @param criteria Condition
     * @param pageable Pagination parameters
     * @return /
     */
    PageResult<UserDto> queryAll(UserQueryCriteria criteria, Pageable pageable);

    /**
     * Query all without pagination
     * @param criteria Condition
     * @return /
     */
    List<UserDto> queryAll(UserQueryCriteria criteria);

    /**
     * Export data
     * @param queryAll Data to export
     * @param response /
     * @throws IOException /
     */
    void download(List<UserDto> queryAll, HttpServletResponse response) throws IOException;

    /**
     * Users edit their own profile
     * @param resources /
     */
    void updateCenter(User resources);

    /**
     * Reset password
     * @param ids User id
     * @param pwd Password
     */
    void resetPwd(Set<Long> ids, String pwd);
}
