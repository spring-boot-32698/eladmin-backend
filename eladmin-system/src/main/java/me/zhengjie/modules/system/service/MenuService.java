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

import me.zhengjie.modules.system.domain.Menu;
import me.zhengjie.modules.system.domain.vo.MenuVo;
import me.zhengjie.modules.system.service.dto.MenuDto;
import me.zhengjie.modules.system.service.dto.MenuQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2018-12-17
 */
public interface MenuService {

    /**
     * Query all data
     * @param criteria Condition
     * @param isQuery /
     * @throws Exception /
     * @return /
     */
    List<MenuDto> queryAll(MenuQueryCriteria criteria, Boolean isQuery) throws Exception;

    /**
     * Query by ID
     * @param id /
     * @return /
     */
    MenuDto findById(long id);

    /**
     * Create
     * @param resources /
     */
    void create(Menu resources);

    /**
     * Edit
     * @param resources /
     */
    void update(Menu resources);

    /**
     * Get all child nodes, including itself ID
     * @param menuList /
     * @param menuSet /
     * @return /
     */
    Set<Menu> getChildMenus(List<Menu> menuList, Set<Menu> menuSet);

    /**
     * Build menu tree
     * @param menuDtos Raw data
     * @return /
     */
    List<MenuDto> buildTree(List<MenuDto> menuDtos);

    /**
     * Build menu tree
     * @param menuDtos /
     * @return /
     */
    List<MenuVo> buildMenus(List<MenuDto> menuDtos);

    /**
     * Query by ID
     * @param id /
     * @return /
     */
    Menu findOne(Long id);

    /**
     * Delete
     * @param menuSet /
     */
    void delete(Set<Menu> menuSet);

    /**
     * Export
     * @param queryAll Data to export
     * @param response /
     * @throws IOException /
     */
    void download(List<MenuDto> queryAll, HttpServletResponse response) throws IOException;

    /**
     * Lazy-load menu data
     * @param pid /
     * @return /
     */
    List<MenuDto> getMenus(Long pid);

    /**
     * Get sibling and parent data by ID
     * @param menuDto /
     * @param objects /
     * @return /
     */
    List<MenuDto> getSuperior(MenuDto menuDto, List<Menu> objects);

    /**
     * Get menus for the current user
     * @param currentUserId /
     * @return /
     */
    List<MenuDto> findByUser(Long currentUserId);
}
