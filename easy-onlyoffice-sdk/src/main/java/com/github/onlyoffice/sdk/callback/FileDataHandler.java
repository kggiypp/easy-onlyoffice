package com.github.onlyoffice.sdk.callback;

/**
 * 文件数据处理接口
 * 实现类需由客户端服务根据业务逻辑自行实现
 * 
 * @author keguang
 */
@FunctionalInterface
public interface FileDataHandler {

    /**
     * 处理文件数据
     * @param data 文件数据
     * @param fileId 文件id
     */
    void handData(byte[] data, String fileId);
    
}
