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
package me.zhengjie.modules.security.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * Online Users
 * @author Zheng Jie
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineUserDto {

    @ApiModelProperty(value = "Token No.")
    private String uid;

    @ApiModelProperty(value = "Username")
    private String userName;

    @ApiModelProperty(value = "Nickname")
    private String nickName;

    @ApiModelProperty(value = "Job")
    private String dept;

    @ApiModelProperty(value = "Browser")
    private String browser;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "Address")
    private String address;

    @ApiModelProperty(value = "token")
    private String key;

    @ApiModelProperty(value = "Login time")
    private Date loginTime;
}
