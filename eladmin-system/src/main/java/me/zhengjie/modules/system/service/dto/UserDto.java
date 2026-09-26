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
package me.zhengjie.modules.system.service.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseDTO;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Getter
@Setter
public class UserDto extends BaseDTO implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "Role")
    private Set<RoleSmallDto> roles;

    @ApiModelProperty(value = "Job")
    private Set<JobSmallDto> jobs;

    @ApiModelProperty(value = "Department")
    private DeptSmallDto dept;

    @ApiModelProperty(value = "Department ID")
    private Long deptId;

    @ApiModelProperty(value = "Username")
    private String username;

    @ApiModelProperty(value = "Nickname")
    private String nickName;

    @ApiModelProperty(value = "Email")
    private String email;

    @ApiModelProperty(value = "Phone")
    private String phone;

    @ApiModelProperty(value = "Gender")
    private String gender;

    @ApiModelProperty(value = "Avatar")
    private String avatarName;

    @ApiModelProperty(value = "Avatar path")
    private String avatarPath;

    @ApiModelProperty(value = "Password")
    private String password;

    @ApiModelProperty(value = "Enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "Administrator")
    @JSONField(serialize = false)
    private Boolean isAdmin = false;

    @ApiModelProperty(value = "Password reset time")
    private Date pwdResetTime;
}
