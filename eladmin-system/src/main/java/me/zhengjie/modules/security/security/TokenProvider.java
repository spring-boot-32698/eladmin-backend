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
package me.zhengjie.modules.security.security;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.security.config.SecurityProperties;
import me.zhengjie.modules.security.service.dto.JwtUserDto;
import me.zhengjie.utils.RedisUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author /
 */
@Slf4j
@Component
public class TokenProvider implements InitializingBean {

    private Key signingKey;
    private JwtParser jwtParser;
    private final RedisUtils redisUtils;
    private final SecurityProperties properties;
    public static final String AUTHORITIES_UUID_KEY = "uid";
    public static final String AUTHORITIES_UID_KEY = "userId";

    public TokenProvider(SecurityProperties properties, RedisUtils redisUtils) {
        this.properties = properties;
        this.redisUtils = redisUtils;
    }

    @Override
    public void afterPropertiesSet() {
        // Decode the Base64 key and create the signing key
        byte[] keyBytes = Decoders.BASE64.decode(properties.getBase64Secret());
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        // Initialize JwtParser
        jwtParser = Jwts.parserBuilder()
                .setSigningKey(signingKey) // Use the pre-generated signing key
                .build();
    }

    /**
     * Create a token that never expires,
     * Token  time validity is maintained in Redis
     *
     * @param user /
     * @return /
     */
    public String createToken(JwtUserDto user) {
        // Set parameters
        Map<String, Object> claims = new HashMap<>(6);
        // Set user ID
        claims.put(AUTHORITIES_UID_KEY, user.getUser().getId());
        // Set a UUID to ensure each token is different
        claims.put(AUTHORITIES_UUID_KEY, IdUtil.simpleUUID());
        // Call directly Jwts.builder() Create a new instance
        return Jwts.builder()
                // Set custom Claims
                .setClaims(claims)
                // Set theme
                .setSubject(user.getUsername())
                // Sign using the pre-generated signing key and algorithm
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Get authentication info from the token
     *
     * @param token /
     * @return /
     */
    Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        User principal = new User(claims.getSubject(), "******", new ArrayList<>());
        return new UsernamePasswordAuthenticationToken(principal, token, new ArrayList<>());
    }

    public Claims getClaims(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * @param token To be checked token
     */
    public void checkRenewal(String token) {
        // Check whether to renew token,Calculate the token expiration time
        String loginKey = loginKey(token);
        long time = redisUtils.getExpire(loginKey) * 1000;
        Date expireDate = DateUtil.offset(new Date(), DateField.MILLISECOND, (int) time);
        // Compute the difference between the current time and the expiration time
        long differ = expireDate.getTime() - System.currentTimeMillis();
        // If within the renewal check window, renew
        if (differ <= properties.getDetect()) {
            long renew = time + properties.getRenew();
            redisUtils.expire(loginKey, renew, TimeUnit.MILLISECONDS);
        }
    }

    public String getToken(HttpServletRequest request) {
        final String requestHeader = request.getHeader(properties.getHeader());
        if (requestHeader != null && requestHeader.startsWith(properties.getTokenStartWith())) {
            return requestHeader.substring(7);
        }
        return null;
    }

    /**
     * Get the login user RedisKey
     * @param token /
     * @return key
     */
    public String loginKey(String token) {
        Claims claims = getClaims(token);
        return properties.getOnlineKey() + claims.getSubject() + ":" + getId(token);
    }

    /**
     * Get the login user TokenKey
     * @param token /
     * @return /
     */
    public String getId(String token) {
        Claims claims = getClaims(token);
        return claims.get(AUTHORITIES_UUID_KEY).toString();
    }
}
