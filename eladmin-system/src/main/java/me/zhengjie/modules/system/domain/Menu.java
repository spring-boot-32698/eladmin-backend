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
package me.zhengjie.modules.system.domain;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseEntity;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2018-12-17
 */
@Entity
@Getter
@Setter
@Table(name = "sys_menu")
public class Menu extends BaseEntity implements Serializable {

    @Id
    @Column(name = "menu_id")
    @NotNull(groups = {Update.class})
    @ApiModelProperty(value = "ID", hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JSONField(serialize = false)
    @ManyToMany(mappedBy = "menus")
    @ApiModelProperty(value = "Menu role")
    private Set<Role> roles;

    @ApiModelProperty(value = "Menu title")
    private String title;

    @Column(name = "name")
    @ApiModelProperty(value = "Menu component name")
    private String componentName;

    @ApiModelProperty(value = "Sort")
    private Integer menuSort = 999;

    @ApiModelProperty(value = "Component path")
    private String component;

    @ApiModelProperty(value = "Route path")
    private String path;

    @ApiModelProperty(value = "Menu type: directory, menu, button")
    private Integer type;

    @ApiModelProperty(value = "Permission identifier")
    private String permission;

    @ApiModelProperty(value = "Menu icon")
    private String icon;

    @Column(columnDefinition = "bit(1) default 0")
    @ApiModelProperty(value = "Cache")
    private Boolean cache;

    @Column(columnDefinition = "bit(1) default 0")
    @ApiModelProperty(value = "Hidden")
    private Boolean hidden;

    @ApiModelProperty(value = "Parent menu")
    private Long pid;

    @ApiModelProperty(value = "Number of child nodes", hidden = true)
    private Integer subCount = 0;

    @ApiModelProperty(value = "External link menu")
    private Boolean iFrame;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Menu menu = (Menu) o;
        return Objects.equals(id, menu.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
