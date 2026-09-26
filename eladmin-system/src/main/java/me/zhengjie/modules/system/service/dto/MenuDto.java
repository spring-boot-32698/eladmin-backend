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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseDTO;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author Zheng Jie
 * @date 2018-12-17
 */
@Getter
@Setter
public class MenuDto extends BaseDTO implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "Child node")
    private List<MenuDto> children;

    @ApiModelProperty(value = "Type")
    private Integer type;

    @ApiModelProperty(value = "Permission")
    private String permission;

    @ApiModelProperty(value = "Menu title")
    private String title;

    @ApiModelProperty(value = "Sort")
    private Integer menuSort;

    @ApiModelProperty(value = "Path")
    private String path;

    @ApiModelProperty(value = "Component")
    private String component;

    @ApiModelProperty(value = "PID")
    private Long pid;

    @ApiModelProperty(value = "Number of child nodes")
    private Integer subCount;

    @ApiModelProperty(value = "Whether it is Iframe")
    @JsonProperty("iFrame")
    private Boolean iFrame;

    @ApiModelProperty(value = "Cached")
    private Boolean cache;

    @ApiModelProperty(value = "Hidden")
    private Boolean hidden;

    @ApiModelProperty(value = "Component name")
    private String componentName;

    @ApiModelProperty(value = "Icon")
    private String icon;

    @ApiModelProperty(value = "Whether child nodes exist")
    public Boolean getHasChildren() {
        return subCount > 0;
    }

    @ApiModelProperty(value = "Whether it is a leaf node")
    public Boolean getLeaf() {
        return subCount <= 0;
    }

    @ApiModelProperty(value = "Title")
    public String getLabel() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MenuDto menuDto = (MenuDto) o;
        return Objects.equals(id, menuDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
