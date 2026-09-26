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
package me.zhengjie.modules.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Jwt Parameter config
 *
 * @author Zheng Jie
 * @date 2019-11-28
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class SecurityProperties {

    /**
     * Request Headers :  Authorization
     */
    private String header;

    /**
     * Token prefix, leave a trailing space Bearer
     */
    private String tokenStartWith;

    /**
     * The token must be Base64-encoded with at least 88 bits
     */
    private String base64Secret;

    /**
     * Token expiration time, unit: milliseconds
     */
    private Long tokenValidityInSeconds;

    /**
     * Online user key, used to query the online user data in Redis
     */
    private String onlineKey;

    /**
     * Verification code key
     */
    private String codeKey;

    /**
     * token Renewal check
     */
    private Long detect;

    /**
     * Renewal time
     */
    private Long renew;

    public String getTokenStartWith() {
        return tokenStartWith + " ";
    }
}
