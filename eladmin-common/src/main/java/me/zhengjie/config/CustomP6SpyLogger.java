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
package me.zhengjie.config;

import cn.hutool.core.util.StrUtil;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Zheng Jie
 * @description Custom p6spy SQL output format
 * @date 2024-12-26
 **/
@Slf4j
public class CustomP6SpyLogger implements MessageFormattingStrategy {

    // Reset color
    private static final String RESET = "\u001B[0m";
    // Red
    private static final String RED = "\u001B[31m";
    // Green
    private static final String GREEN = "\u001B[32m";

    /**
     * Format sql
     * @param connectionId Connect id
     * @param now Current time
     * @param elapsed Execution duration
     * @param category sql Category
     * @param prepared Precompile sql
     * @param sql Execute sql
     * @param url Database connection url
     * @return Formatted sql
     */
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        // Remove line breaks and extra spaces
        if(StrUtil.isNotBlank(sql)){
            sql = sql.replaceAll("\\s+", " ").trim();
        }

        // Format and add color
        return String.format(
                "%s[Time: %dms]%s - %s%s%s;",
                GREEN, elapsed, RESET, RED, sql, RESET
        );
    }
}

