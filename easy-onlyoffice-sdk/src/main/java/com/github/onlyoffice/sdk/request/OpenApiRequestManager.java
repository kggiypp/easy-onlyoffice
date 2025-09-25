package com.github.onlyoffice.sdk.request;

import com.github.onlyoffice.sdk.dto.LinkRequest;
import com.onlyoffice.manager.request.RequestManager;

/**
 * easy-onlyoffice扩展接口
 * 
 * @author keguang
 */
public interface OpenApiRequestManager extends RequestManager {

    /**
     * 为指定的资源文件生成一个可在线编辑的链接地址
     * @param srcUrl 可直接访问的文件资源路径
     * @param callbackUrl 回调地址
     * @return 可在线编辑的一个链接地址
     */
    String editorLink(String srcUrl, String callbackUrl);

    /**
     * 为给定的文件数据生成一个可在线编辑的链接地址
     * @param linkRequest 请求参数
     * @return 可在线编辑的一个链接地址
     */
    String editorLink(LinkRequest linkRequest);
    
}
