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
package me.zhengjie.service;

import me.zhengjie.domain.GenConfig;
import me.zhengjie.domain.ColumnInfo;
import me.zhengjie.domain.vo.TableInfo;
import me.zhengjie.utils.PageResult;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Zheng Jie
 * @date 2019-01-02
 */
public interface GeneratorService {

    /**
     * Query database metadata
     * @param name Table name
     * @param startEnd Pagination parameters
     * @return /
     */
    PageResult<TableInfo> getTables(String name, int[] startEnd);

    /**
     * Get the metadata of the data table
     * @param name Table name
     * @return /
     */
    List<ColumnInfo> getColumns(String name);

    /**
     * Sync table data
     * @param columnInfos /
     * @param columnInfoList /
     */
    void sync(List<ColumnInfo> columnInfos, List<ColumnInfo> columnInfoList);

    /**
     * Keep data
     * @param columnInfos /
     */
    void save(List<ColumnInfo> columnInfos);

    /**
     * Get all table
     * @return /
     */
    Object getTables();

    /**
     * Code Generation
     * @param genConfig Configuration
     * @param columns Field info
     */
    void generator(GenConfig genConfig, List<ColumnInfo> columns);

    /**
     * Preview
     * @param genConfig Configuration
     * @param columns Field info
     * @return /
     */
    ResponseEntity<Object> preview(GenConfig genConfig, List<ColumnInfo> columns);

    /**
     * Package and download
     * @param genConfig Configuration
     * @param columns Field info
     * @param request /
     * @param response /
     */
    void download(GenConfig genConfig, List<ColumnInfo> columns, HttpServletRequest request, HttpServletResponse response);

    /**
     * Query the table column data of the database
     * @param table /
     * @return /
     */
    List<ColumnInfo> query(String table);
}
