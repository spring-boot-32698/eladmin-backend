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
package me.zhengjie.modules.security.service;

import cn.hutool.core.util.RandomUtil;
import me.zhengjie.modules.security.config.LoginProperties;
import me.zhengjie.modules.security.service.dto.JwtUserDto;
import me.zhengjie.utils.RedisUtils;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * @author Zheng Jie
 * @description User cache management
 * @date 2022-05-26
 **/
@Component
public class UserCacheManager {

    @Resource
    private RedisUtils redisUtils;
    @Value("${login.user-cache.idle-time}")
    private long idleTime;

    /**
     * Return user cache
     * @param userName Username
     * @return JwtUserDto
     */
    public JwtUserDto getUserCache(String userName) {
        // Convert to lowercase
        userName = StringUtils.lowerCase(userName);
        if (StringUtils.isNotEmpty(userName)) {
            // Get data
            return redisUtils.get(LoginProperties.cacheKey + userName, JwtUserDto.class);
        }
        return null;
    }

    /**
     *  Add cache to Redis
     * @param userName Username
     */
    @Async
    public void addUserCache(String userName, JwtUserDto user) {
        // Convert to lowercase
        userName = StringUtils.lowerCase(userName);
        if (StringUtils.isNotEmpty(userName)) {
            // Add data, Avoid data expiring at the same time
            long time = idleTime + RandomUtil.randomInt(900, 1800);
            redisUtils.set(LoginProperties.cacheKey + userName, user, time);
        }
    }

    /**
     * Clear user cache info
     * When user info changes
     * @param userName Username
     */
    @Async
    public void cleanUserCache(String userName) {
        // Convert to lowercase
        userName = StringUtils.lowerCase(userName);
        if (StringUtils.isNotEmpty(userName)) {
            // Clear data
            redisUtils.del(LoginProperties.cacheKey + userName);
        }
    }
}