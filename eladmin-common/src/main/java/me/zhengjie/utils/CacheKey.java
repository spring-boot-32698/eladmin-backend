/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.zhengjie.utils;

/**
 * @author liaojinlong
 * @date 2020/6/11 15:49
 * @description Key set for the cache
 */
public interface CacheKey {

    /**
     * User
     */
    String USER_ID = "user::id:";

    /**
     * Data
     */
    String DATA_USER = "data::user:";

    /**
     * Menu
     */
    String MENU_ID = "menu::id:";
    String MENU_USER = "menu::user:";

    /**
     * Role authorization
     */
    String ROLE_AUTH = "role::auth:";
    String ROLE_USER = "role::user:";

    /**
     * Role info
     */
    String ROLE_ID = "role::id:";

    /**
     * Department
     */
    String DEPT_ID = "dept::id:";

    /**
     * Job
     */
    String JOB_ID = "job::id:";

    /**
     * Data Dictionary
     */
    String DICT_NAME = "dict::name:";
}
