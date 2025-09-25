package com.onlyoffice.integration.sdk.service;

import com.onlyoffice.integration.dto.FileInfo;
import com.onlyoffice.integration.sdk.manager.UrlManager;
import com.onlyoffice.integration.utils.CacheFileMap;
import com.onlyoffice.manager.document.DocumentManager;
import com.onlyoffice.manager.security.JwtManager;
import com.onlyoffice.manager.settings.SettingsManager;
import com.onlyoffice.model.documenteditor.Config;
import com.onlyoffice.model.documenteditor.config.document.Type;
import com.onlyoffice.model.documenteditor.config.editorconfig.Customization;
import com.onlyoffice.model.documenteditor.config.editorconfig.Mode;
import com.onlyoffice.service.documenteditor.config.DefaultConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OpenApiConfigServiceImpl extends DefaultConfigService {
    
    @Autowired
    @Qualifier("cacheFileMap")
    private CacheFileMap cacheFileMap;
    
    public OpenApiConfigServiceImpl(@Qualifier("openApiDocumentManagerImpl") DocumentManager documentManager,
            @Qualifier("openApiUrlManagerImpl") UrlManager urlManager, JwtManager jwtManager,
            SettingsManager settingsManager) {
        
        super(documentManager, urlManager, jwtManager, settingsManager);
    }
    
    @Override
    public Config createConfig(String fileId, Mode mode, Type type) {
        FileInfo fileInfo = cacheFileMap.get(fileId);
        if (fileInfo == null) {
            throw new RuntimeException("系统内部该缓存文件已失效，请重新请求获取编辑链接");
        }
        return super.createConfig(fileInfo.getInternalFileName(), mode, type);
    }
    
    /**
     * 为不影响其他功能的使用，不在application中修改全局配置，
     * 只定制化openApi链路的配置
     * @param internalFileName
     * @return Customization
     */
    @Override
    public Customization getCustomization(String internalFileName) {
        Customization customization = super.getCustomization(internalFileName);
        customization.setAutosave(false);
        // 设置该参数可以使页面编辑器手动保存时请求回调地址，否则只会在关闭页面编辑器后才请求回调地址
        customization.setForcesave(true);
        customization.setChat(false);
        return customization;
    }
    
}
