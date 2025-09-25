package com.onlyoffice.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件信息
 * 
 * @author keguang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfo {

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 原始文件名
     */
    private String originalFileName;

    /**
     * 系统处理后的内部文件名
     */
    private String internalFileName;

    /**
     * 文件存放的绝对路径
     */
    private String filePath;
    
}
