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
package me.zhengjie.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.config.AmzS3Config;
import me.zhengjie.domain.S3Storage;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.repository.S3StorageRepository;
import me.zhengjie.service.S3StorageService;
import me.zhengjie.service.dto.S3StorageQueryCriteria;
import me.zhengjie.utils.*;
import org.apache.commons.io.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
* @description Service implementation
* @author Zheng Jie
* @date 2025-06-25
**/
@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageServiceImpl implements S3StorageService {

    private final S3Client s3Client;
    private final AmzS3Config amzS3Config;
    private final S3StorageRepository s3StorageRepository;

    @Override
    public S3Storage getById(Long id) {
        return s3StorageRepository.findById(id).orElse(null);
    }

    @Override
    public PageResult<S3Storage> queryAll(S3StorageQueryCriteria criteria, Pageable pageable){
        Page<S3Storage> page = s3StorageRepository.findAll((root, criteriaQuery, criteriaBuilder)
                -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page);
    }

    @Override
    public List<S3Storage> queryAll(S3StorageQueryCriteria criteria){
        return s3StorageRepository.findAll((root, criteriaQuery, criteriaBuilder)
                -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Long> ids) {
        // Check whether the bucket exists
        String bucketName = amzS3Config.getDefaultBucket();
        if (!bucketExists(bucketName)) {
            throw new BadRequestException("Bucket does not exist, please check the configuration or permissions.");
        }
        // Iterate the ID list and delete the corresponding files and database records
        for (Long id : ids) {
            String filePath = s3StorageRepository.selectFilePathById(id);
            if (filePath == null) {
                System.err.println("No record found with ID " + id + "  file record, it cannot be deleted.");
                continue;
            }
            try {
                // Create a DeleteObjectRequest specifying the bucket and file key
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(filePath)
                        .build();
                // Call the deleteObject method
                s3Client.deleteObject(deleteObjectRequest);
                // Delete database data
                s3StorageRepository.deleteById(id);
            } catch (S3Exception e) {
                // Handle AWS-specific exceptions
                log.error("Error deleting file from S3: {}", e.awsErrorDetails().errorMessage(), e);
            }
        }
    }

    @Override
    public S3Storage upload(MultipartFile file) {
        String bucketName = amzS3Config.getDefaultBucket();
        // Check whether the bucket exists
        if (!bucketExists(bucketName)) {
            log.warn("Bucket {} does not exist, trying to create...", bucketName);
            if (createBucket(bucketName)){
                log.info("Bucket {} Created successfully.", bucketName);
            } else {
                throw new BadRequestException("Bucket creation failed, please check the configuration or permissions.");
            }
        }
        // Get file name
        String originalName = file.getOriginalFilename();
        if (StringUtils.isBlank(originalName)) {
            throw new IllegalArgumentException("File name cannot be empty");
        }
        // Generate the storage path and file name
        String folder = DateUtil.format(new Date(), amzS3Config.getTimeformat());
        String fileName = IdUtil.simpleUUID() + "." + FileUtil.getExtensionName(originalName);
        String filePath = folder + "/" + fileName;
        // Build the upload request
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(amzS3Config.getDefaultBucket())
                .key(filePath)
                .build();
        // Create an S3Storage instance
        S3Storage s3Storage = new S3Storage();
        try {
            // Upload file to S3
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            // Set the S3Storage properties
            s3Storage.setFileMimeType(FileUtil.getMimeType(originalName));
            s3Storage.setFileName(originalName);
            s3Storage.setFileRealName(fileName);
            s3Storage.setFileSize(FileUtil.getSize(file.getSize()));
            s3Storage.setFileType(FileUtil.getExtensionName(originalName));
            s3Storage.setFilePath(filePath);
            // Save to database
            s3StorageRepository.save(s3Storage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Set address
        return s3Storage;
    }

    @Override
    public void download(List<S3Storage> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (S3Storage s3Storage : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("File name", s3Storage.getFileName());
            map.put("Actual stored name", s3Storage.getFileRealName());
            map.put("File size", s3Storage.getFileSize());
            map.put("File MIME type", s3Storage.getFileMimeType());
            map.put("File type", s3Storage.getFileType());
            map.put("File path", s3Storage.getFilePath());
            map.put("Creator", s3Storage.getCreateBy());
            map.put("Updater", s3Storage.getUpdateBy());
            map.put("Created at", s3Storage.getCreateTime());
            map.put("Updated at", s3Storage.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    public Map<String, String> privateDownload(Long id) {
        S3Storage storage = s3StorageRepository.findById(id).orElse(null);
        if (storage == null) {
            throw new BadRequestException("File does not exist or has been deleted");
        }
        // Create a GetObjectRequest specifying the bucket and file key
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(amzS3Config.getDefaultBucket())
                .key(storage.getFilePath())
                .build();
        String base64Data;
        // Use try-with-resources to ensure the stream is closed automatically
        // s3Client.getObject() Returns a ResponseInputStream, an input stream containing the S3 object data
        try (ResponseInputStream<GetObjectResponse> s3InputStream = s3Client.getObject(getObjectRequest)) {
            // Use IOUtils.toByteArray to convert the input stream directly to a byte array
            byte[] fileBytes = IOUtils.toByteArray(s3InputStream);
            // Use Java's built-in Base64 encoder to convert the byte array to a Base64 string
            base64Data = Base64.getEncoder().encodeToString(fileBytes);
        } catch (S3Exception e) {
            // Handle AWS-specific exceptions
            throw new BadRequestException("Error downloading file from S3: " + e.awsErrorDetails().errorMessage());
        } catch (IOException e) {
            // Handle generic IO exceptions (IOUtils.toByteArray May throw)
            throw new BadRequestException("Error reading the S3 input stream: " + e.getMessage());
        }
        // Construct the return data
        Map<String, String> responseData = new HashMap<>();
        // File name
        responseData.put("fileName", storage.getFileName());
        // File type
        responseData.put("fileMimeType", storage.getFileMimeType());
        // File content
        responseData.put("base64Data", base64Data);
        return responseData;
    }

    /**
     * Check whether the cloud storage bucket exists
     * @param bucketName Bucket name
     */
    @SuppressWarnings({"all"})
    private boolean bucketExists(String bucketName) {
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.headBucket(headBucketRequest);
            return true;
        } catch (S3Exception e) {
            // If the status code is 404 (Not Found), Indicates the bucket does not exist
            if (e.statusCode() == 404) {
                log.error("Bucket '{}' does not exist.", bucketName);
                return false;
            }
            // Other exceptions (e.g. 403 Forbidden) Indicates a problem, but it cannot be concluded that it does not exist
            throw new BadRequestException("Error checking bucket: " + e.awsErrorDetails().errorMessage());
        }
    }

    /**
     * Create cloud storage bucket
     * @param bucketName Bucket name
     */
    private boolean createBucket(String bucketName) {
        try {
            // Use S3Waiter to wait for bucket creation to complete
            S3Waiter s3Waiter = s3Client.waiter();
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .acl(BucketCannedACL.PRIVATE)
                    .build();
            s3Client.createBucket(bucketRequest);
            // Wait until bucket creation completes
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            // Use WaiterResponse to wait for the bucket to exist
            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
            waiterResponse.matched().response().ifPresent(response ->
                    log.info("Bucket '{}' Created successfully, status: {}", bucketName, response.sdkHttpResponse().statusCode())
            );
        } catch (BucketAlreadyOwnedByYouException e) {
            log.warn("Bucket '{}' is already owned by you, no need to create it again.", bucketName);
        } catch (S3Exception e) {
            throw new BadRequestException("Error creating bucket: " + e.awsErrorDetails().errorMessage());
        }
        return true;
    }
}