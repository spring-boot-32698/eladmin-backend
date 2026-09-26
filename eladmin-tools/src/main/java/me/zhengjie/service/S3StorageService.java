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

import me.zhengjie.domain.S3Storage;
import me.zhengjie.service.dto.S3StorageQueryCriteria;
import me.zhengjie.utils.PageResult;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @description Service interface
* @author Zheng Jie
* @date 2025-06-25
**/
public interface S3StorageService {

    /**
    * Paged data query
    * @param criteria Condition
    * @param pageable Pagination parameters
    * @return PageResult
    */
    PageResult<S3Storage> queryAll(S3StorageQueryCriteria criteria, Pageable pageable);

    /**
    * Query all data without pagination
    * @param criteria Condition parameter
    * @return List<S3StorageDto>
    */
    List<S3Storage> queryAll(S3StorageQueryCriteria criteria);

    /**
    * Multi-select delete
    * @param ids /
    */
    void deleteAll(List<Long> ids);

    /**
    * Export data
    * @param all Data to export
    * @param response /
    * @throws IOException /
    */
    void download(List<S3Storage> all, HttpServletResponse response) throws IOException;

    /**
     * Private download, for reference only, there are many other ways
     * @param id File ID
     */
    Map<String, String> privateDownload(Long id);

    /**
     * Upload file
     * @param file Uploaded file
     * @return S3Storage Object containing file storage info
     */
    S3Storage upload(MultipartFile file);

    /**
     * Get file info by ID
     * @param id File ID
     * @return S3Storage Object containing file storage info
     */
    S3Storage getById(Long id);
}