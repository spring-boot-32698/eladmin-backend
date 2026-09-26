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
package me.zhengjie.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Zheng Jie
 * @website https://eladmin.vip
 * @description
 * @date 2020-06-10
 **/
@Getter
@AllArgsConstructor
public enum RequestMethodEnum {

    /**
     * Search @AnonymousGetMapping
     */
    GET("GET"),

    /**
     * Search @AnonymousPostMapping
     */
    POST("POST"),

    /**
     * Search @AnonymousPutMapping
     */
    PUT("PUT"),

    /**
     * Search @AnonymousPatchMapping
     */
    PATCH("PATCH"),

    /**
     * Search @AnonymousDeleteMapping
     */
    DELETE("DELETE"),

    /**
     * Otherwise all Request endpoints are allowed
     */
    ALL("All");

    /**
     * Request Type
     */
    private final String type;

    public static RequestMethodEnum find(String type) {
        for (RequestMethodEnum value : RequestMethodEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return ALL;
    }
}
