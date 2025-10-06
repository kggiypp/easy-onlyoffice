package com.github.onlyoffice.sdk.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onlyoffice.model.common.RequestEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求文件在线编辑链接参数对象
 * 
 * @author keguang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkRequest implements RequestEntity {

    /**
     * 文件数据
     */
    private byte[] data;

    /**
     * 回调地址
     */
    private String callback;

    /**
     * 文件名（包括扩展名后缀）
     */
    private String filename;

    @Override
    @JsonIgnore
    public String getKey() {
        return null;
    }

    @Override
    public String getToken() {
        return null;
    }

    @Override
    public void setToken(String token) {}
    
}
