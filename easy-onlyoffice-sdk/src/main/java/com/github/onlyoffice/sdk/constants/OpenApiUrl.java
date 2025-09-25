package com.github.onlyoffice.sdk.constants;

/**
 * openApi接口url相关定义
 * 
 * @author keguang
 */
public interface OpenApiUrl {
    
    interface Path {

        String BASE = "/openApi";

        String EDITOR = "/editor";

        String EDITOR_LINK = "/editorLink";
        
    }
    
    interface QUERY_PARAM {
        
        String FILE_ID = "fileId";
        
        String SRC = "src";
        
        String CALLBACK = "callback";
        
    }
    
}
