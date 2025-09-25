package com.onlyoffice.integration.documentserver.storage;

import com.onlyoffice.integration.dto.FileInfo;
import com.onlyoffice.integration.utils.CacheFileMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

@Component
public class OpenApiFileStorage extends LocalFileStorage {
    
    @Autowired
    @Qualifier("cacheFileMap")
    private CacheFileMap cacheFileMap;

    @Override
    public String updateFile(String fileName, byte[] bytes) {
        return cacheFileMap.computeIfAbsent(DigestUtils.md5DigestAsHex(bytes), fileId -> {
            String internalFileName = super.updateFile(fileName, bytes);
            String filePath = getFileLocation(internalFileName);
            return new FileInfo(fileId, fileName, internalFileName, filePath);
        }).getFileId();
    }
    
    @Override
    @Autowired
    public void configure(@Value("${files.storage}") String address) {
        super.configure(StringUtils.hasText(address) ? address : null);
    }
    
}
