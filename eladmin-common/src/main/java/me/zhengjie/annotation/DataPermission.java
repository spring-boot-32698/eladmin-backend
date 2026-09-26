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
package me.zhengjie.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *   Used to decide whether to filter data permissions
 *   1, if not used @OneToOne For this kind of association, only fill in fieldName [Reference:DeptQueryCriteria.class]
 *   2, if used @OneToOne , both fieldName and joinName must be filled in, taking UserQueryCriteria.class as an example:
 *   should be @DataPermission(joinName = "dept", fieldName = "id")
 * </p>
 * @author Zheng Jie
 * @website <a href="https://eladmin.vip">...</a>
 * @date 2020-05-07
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPermission {

    /**
     * Entity The field name in
     */
    String fieldName() default "";

    /**
     * Entity The field name associated with the department in
     */
    String joinName() default "";
}
