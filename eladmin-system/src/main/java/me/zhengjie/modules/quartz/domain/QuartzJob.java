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
package me.zhengjie.modules.quartz.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseEntity;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Zheng Jie
 * @date 2019-01-07
 */
@Getter
@Setter
@Entity
@Table(name = "sys_quartz_job")
public class QuartzJob extends BaseEntity implements Serializable {

    public static final String JOB_KEY = "JOB_KEY";

    @Id
    @Column(name = "job_id")
    @NotNull(groups = {Update.class})
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    @ApiModelProperty(value = "Used as the unique identifier of sub-tasks", hidden = true)
    private String uuid;

    @ApiModelProperty(value = "Timer name")
    private String jobName;

    @NotBlank
    @ApiModelProperty(value = "Bean Name")
    private String beanName;

    @NotBlank
    @ApiModelProperty(value = "Method name")
    private String methodName;

    @ApiModelProperty(value = "Parameter")
    private String params;

    @NotBlank
    @ApiModelProperty(value = "cron Expression")
    private String cronExpression;

    @ApiModelProperty(value = "Status: paused or started")
    private Boolean isPause = false;

    @ApiModelProperty(value = "Owner")
    private String personInCharge;

    @ApiModelProperty(value = "Alarm email")
    private String email;

    @ApiModelProperty(value = "Sub-task")
    private String subTask;

    @ApiModelProperty(value = "Pause after failure")
    private Boolean pauseAfterFailure;

    @NotBlank
    @ApiModelProperty(value = "Remarks")
    private String description;
}