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
 * <p>
 * The Redis key corresponding to the captcha business scenario: key
 * </p>
 * @author Zheng Jie
 * @date 2020-05-02
 */
@Getter
@AllArgsConstructor
public enum CodeEnum {

    /* Reset email by phone number */
    PHONE_RESET_EMAIL_CODE("phone_reset_email_code_", "Reset email by phone number"),

    /* Reset email via the old email */
    EMAIL_RESET_EMAIL_CODE("email_reset_email_code_", "Reset email via the old email"),

    /* Reset password by phone number */
    PHONE_RESET_PWD_CODE("phone_reset_pwd_code_", "Reset password by phone number"),

    /* Reset password by email */
    EMAIL_RESET_PWD_CODE("email_reset_pwd_code_", "Reset password by email");

    private final String key;
    private final String description;
}
