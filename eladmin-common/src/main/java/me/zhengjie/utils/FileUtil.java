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
package me.zhengjie.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * File Utility class extending the hutool toolkit
 *
 * @author Zheng Jie
 * @date 2018-12-27
 */
@Slf4j
public class FileUtil extends cn.hutool.core.io.FileUtil {

    /**
     * System temp directory
     * <br>
     * windows Contains the path separator, but Linux does not,
     * in windows \\==\ on the premise that,
     * For safety, uniformly assemble the path separator,
     * <pre>
     *       java.io.tmpdir
     *       windows : C:\Users/xxx\AppData\Local\Temp\
     *       linux: /temp
     * </pre>
     */
    public static final String SYS_TEM_DIR = System.getProperty("java.io.tmpdir") + File.separator;
    /**
     * Define the GB calculation constant
     */
    private static final int GB = 1024 * 1024 * 1024;
    /**
     * Define the MB calculation constant
     */
    private static final int MB = 1024 * 1024;
    /**
     * Define the KB calculation constant
     */
    private static final int KB = 1024;

    /**
     * Format decimals
     */
    private static final DecimalFormat DF = new DecimalFormat("0.00");

    public static final String IMAGE = "Image";
    public static final String TXT = "Docs";
    public static final String MUSIC = "Music";
    public static final String VIDEO = "Video";
    public static final String OTHER = "Other";


    /**
     * MultipartFile Convert File
     */
    public static File toFile(MultipartFile multipartFile) {
        // Get file name
        String fileName = multipartFile.getOriginalFilename();
        // Get file suffix
        String prefix = "." + getExtensionName(fileName);
        File file = null;
        try {
            // Use a uuid as the file name to prevent duplicate temp files
            file = new File(SYS_TEM_DIR + IdUtil.simpleUUID() + prefix);
            // MultipartFile to File
            multipartFile.transferTo(file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return file;
    }

    /**
     * Get the file extension, without .
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (!filename.isEmpty())) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * Java File operation: get the file name without extension
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (!filename.isEmpty())) {
            int dot = filename.lastIndexOf('.');
            if (dot > -1) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * File size conversion
     */
    public static String getSize(long size) {
        String resultSize;
        if (size / GB >= 1) {
            //If the current Byte value is greater than or equal to 1GB
            resultSize = DF.format(size / (float) GB) + "GB";
        } else if (size / MB >= 1) {
            //If the current Byte value is greater than or equal to 1MB
            resultSize = DF.format(size / (float) MB) + "MB";
        } else if (size / KB >= 1) {
            //If the current Byte value is greater than or equal to 1KB
            resultSize = DF.format(size / (float) KB) + "KB";
        } else {
            resultSize = size + "B";
        }
        return resultSize;
    }

    /**
     * inputStream Convert File
     */
    static File inputStreamToFile(InputStream ins, String name){
        File file = new File(SYS_TEM_DIR + name);
        if (file.exists()) {
            return file;
        }
        OutputStream os = null;
        try {
            os = Files.newOutputStream(file.toPath());
            int bytesRead;
            int len = 8192;
            byte[] buffer = new byte[len];
            while ((bytesRead = ins.read(buffer, 0, len)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            CloseUtil.close(os);
            CloseUtil.close(ins);
        }
        return file;
    }

    /**
     * Parse the file name into the file's upload path
     */
    public static File upload(MultipartFile file, String filePath) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmssS");
        // Filter illegal file names
        String name = getFileNameNoEx(verifyFilename(file.getOriginalFilename()));
        String suffix = getExtensionName(file.getOriginalFilename());
        String nowStr = "-" + format.format(date);
        try {
            String fileName = name + nowStr + "." + suffix;
            String path = filePath + fileName;
            // getCanonicalFile Can correctly parse various paths
            File dest = new File(path).getCanonicalFile();
            // Check whether the directory exists
            if (!dest.getParentFile().exists()) {
                if (!dest.getParentFile().mkdirs()) {
                    System.out.println("was not successful.");
                }
            }
            // File write
            file.transferTo(dest);
            return dest;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Prevent CSV/XLSX injection (CWE-1236)
     * <p>
     * When a cell value starts with {@code = + - @ \t \r} at the start, spreadsheet software may interpret it as a formula.
     * This method prepends a single quote to such values and escapes existing single quotes in the value to prevent formula injection attacks.
     * </p>
     *
     * @param value Original cell value
     * @return Sanitized cell value
     * @see <a href="https://owasp.org/www-community/attacks/CSV_Injection">OWASP CSV Injection</a>
     */
    public static String sanitizeCellValue(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        char first = value.charAt(0);
        if (first == '=' || first == '+' || first == '-' || first == '@'
                || first == '\t' || first == '\r') {
            // Escape existing single quotes in the value so attackers cannot use them to break out
            return "'" + value.replace("'", "''");
        }
        return value;
    }

    /**
     * Export excel
     */
    public static void downloadExcel(List<Map<String, Object>> list, HttpServletResponse response) throws IOException {
        String tempPath = SYS_TEM_DIR + IdUtil.fastSimpleUUID() + ".xlsx";
        File file = new File(tempPath);
        BigExcelWriter writer = ExcelUtil.getBigWriter(file);
        // Process data to prevent CSV/XLSX injection (CWE-1236)
        List<Map<String, Object>> sanitizedList = list.stream().map(map -> {
            Map<String, Object> sanitizedMap = new LinkedHashMap<>();
            map.forEach((key, value) -> {
                if (value instanceof String) {
                    sanitizedMap.put(key, sanitizeCellValue((String) value));
                } else {
                    sanitizedMap.put(key, value);
                }
            });
            return sanitizedMap;
        }).collect(Collectors.toList());
        // Write the content in one go, use the default style, force the title output
        writer.write(sanitizedList, true);
        SXSSFSheet sheet = (SXSSFSheet)writer.getSheet();
        //The above needs a cast to SXSSFSheet, otherwise there is no trackAllColumnsForAutoSizing method
        sheet.trackAllColumnsForAutoSizing();
        //Auto-fit column width
        writer.autoSizeColumnAll();
        //response is the HttpServletResponse object
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        //test.xls is the file name shown in the download dialog; it cannot be Chinese, encode it yourself if needed
        response.setHeader("Content-Disposition", "attachment;filename=file.xlsx");
        ServletOutputStream out = response.getOutputStream();
        // Delete temp files after termination
        file.deleteOnExit();
        writer.flush(out, true);
        //Remember to close the output Servlet stream here
        IoUtil.close(out);
    }

    public static String getFileType(String type) {
        String documents = "txt doc pdf ppt pps xlsx xls docx";
        String music = "mp3 wav wma mpa ram ra aac aif m4a";
        String video = "avi mpg mpe mpeg asf wmv mov qt rm mp4 flv m4v webm ogv ogg";
        String image = "bmp dib pcp dif wmf gif jpg tif eps psd cdr iff tga pcd mpt png jpeg";
        if (image.contains(type)) {
            return IMAGE;
        } else if (documents.contains(type)) {
            return TXT;
        } else if (music.contains(type)) {
            return MUSIC;
        } else if (video.contains(type)) {
            return VIDEO;
        } else {
            return OTHER;
        }
    }

    public static void checkSize(long maxSize, long size) {
        // 1M
        int len = 1024 * 1024;
        if (size > (maxSize * len)) {
            throw new BadRequestException("File exceeds the allowed size:" + maxSize + "MB");
        }
    }

    /**
     * Check whether two files are the same
     */
    public static boolean check(File file1, File file2) {
        String img1Md5 = getMd5(file1);
        String img2Md5 = getMd5(file2);
        if(img1Md5 != null){
            return img1Md5.equals(img2Md5);
        }
        return false;
    }

    /**
     * Check whether two files are the same
     */
    public static boolean check(String file1Md5, String file2Md5) {
        return file1Md5.equals(file2Md5);
    }

    private static byte[] getByte(File file) {
        // Get file length
        byte[] b = new byte[(int) file.length()];
        InputStream in = null;
        try {
            in = Files.newInputStream(file.toPath());
            try {
                System.out.println(in.read(b));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            CloseUtil.close(in);
        }
        return b;
    }

    private static String getMd5(byte[] bytes) {
        // 16-base characters
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(bytes);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            // Shift and output the string
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Download file
     *
     * @param request  /
     * @param response /
     * @param file     /
     */
    public static void downloadFile(HttpServletRequest request, HttpServletResponse response, File file, boolean deleteOnExit) {
        response.setCharacterEncoding(request.getCharacterEncoding());
        response.setContentType("application/octet-stream");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            IOUtils.copy(fis, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    if (deleteOnExit) {
                        file.deleteOnExit();
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Validate and filter illegal file names
     * @param fileName File name
     * @return File name
     */
    public static String verifyFilename(String fileName) {
        // Filter out special characters
        fileName = fileName.replaceAll("[\\\\/:*?\"<>|~\\s]", "");

        // Remove spaces and dots at the start and end of the file name
        fileName = fileName.trim().replaceAll("^[. ]+|[. ]+$", "");

        // File names longer than 255 (on Mac and Linux) or 260 (on Windows) characters are not allowed
        int maxFileNameLength = 255;
        if (System.getProperty("os.name").startsWith("Windows")) {
            maxFileNameLength = 260;
        }
        if (fileName.length() > maxFileNameLength) {
            fileName = fileName.substring(0, maxFileNameLength);
        }

        // Filter out control characters
        fileName = fileName.replaceAll("[\\p{Cntrl}]", "");

        // Filter out ".." Path
        fileName = fileName.replaceAll("\\.{2,}", "");

        // Remove the leading part of the file name ".."
        fileName = fileName.replaceAll("^\\.+/", "");

        // Keep the last one in the file name "." characters, filter out others "."
        fileName = fileName.replaceAll("^(.*)(\\.[^.]*)$", "$1").replaceAll("\\.", "") +
                fileName.replaceAll("^(.*)(\\.[^.]*)$", "$2");

        return fileName;
    }

    public static String getMd5(File file) {
        return getMd5(getByte(file));
    }
}
