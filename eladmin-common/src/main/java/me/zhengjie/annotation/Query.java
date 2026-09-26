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
 * @author Zheng Jie
 * @date 2019-6-4 13:52:30
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    // Dong ZhaoYang 2017/8/7 Property names of the base object
    String propName() default "";
    // Dong ZhaoYang 2017/8/7 Query type
    Type type() default Type.EQUAL;

    /**
     * Property name for join queries, e.g. in the User class dept
     */
    String joinName() default "";

    /**
     * Left join by default
     */
    Join join() default Join.LEFT;

    /**
     * Multi-field fuzzy search, only String fields are supported, separate multiple with commas, e.g.@Query(blurry = "email,username")
     */
    String blurry() default "";

    enum Type {
        // jie 2019/6/4 Equal
        EQUAL
        // Dong ZhaoYang 2017/8/7 Greater than or equal to
        , GREATER_THAN
        // Dong ZhaoYang 2017/8/7 Less than or equal to
        , LESS_THAN
        // Dong ZhaoYang 2017/8/7 fuzzy query in
        , INNER_LIKE
        // Dong ZhaoYang 2017/8/7 Left fuzzy query
        , LEFT_LIKE
        // Dong ZhaoYang 2017/8/7 Right fuzzy query
        , RIGHT_LIKE
        // Dong ZhaoYang 2017/8/7 Less than
        , LESS_THAN_NQ
        // jie 2019/6/4 Contains
        , IN
        // Not contains
        , NOT_IN
        // Not equal to
        ,NOT_EQUAL
        // between
        ,BETWEEN
        // Not empty
        ,NOT_NULL
        // is empty
        ,IS_NULL,
        // Aborn Jiang 2022/06/01, Corresponding SQL: SELECT * FROM table WHERE FIND_IN_SET('querytag', table.tags);
        FIND_IN_SET
    }

    /**
     * @author Zheng Jie
     * Suitable for simple join queries; for complex ones customize this annotation or use SQL queries
     */
    enum Join {
        /** jie 2019-6-4 13:18:30 */
        LEFT, RIGHT, INNER
    }

}

