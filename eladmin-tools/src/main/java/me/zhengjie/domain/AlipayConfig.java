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
package me.zhengjie.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Alipay configuration class
 * @author Zheng Jie
 * @date 2018-12-31
 */
@Data
@Entity
@Table(name = "tool_alipay_config")
public class AlipayConfig implements Serializable {

    @Id
    @Column(name = "config_id")
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "App ID")
    private String appId;

    @NotBlank
    @ApiModelProperty(value = "Merchant private key")
    private String privateKey;

    @NotBlank
    @ApiModelProperty(value = "Alipay public key")
    private String publicKey;

    @ApiModelProperty(value = "Signature method")
    private String signType="RSA2";

    @Column(name = "gateway_url")
    @ApiModelProperty(value = "Alipay open security URL", hidden = true)
    private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    @ApiModelProperty(value = "Encoding", hidden = true)
    private String charset= "utf-8";

    @NotBlank
    @ApiModelProperty(value = "Async notification URL")
    private String notifyUrl;

    @NotBlank
    @ApiModelProperty(value = "Page returned after the order is completed")
    private String returnUrl;

    @ApiModelProperty(value = "Type")
    private String format="JSON";

    @NotBlank
    @ApiModelProperty(value = "Merchant ID")
    private String sysServiceProviderId;

}
