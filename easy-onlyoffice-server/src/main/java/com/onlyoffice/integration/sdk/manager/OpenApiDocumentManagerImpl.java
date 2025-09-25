package com.onlyoffice.integration.sdk.manager;

import com.onlyoffice.integration.utils.CacheFileMap;
import com.onlyoffice.manager.settings.SettingsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OpenApiDocumentManagerImpl extends DocumentMangerImpl {
    
    @Autowired
    @Qualifier("cacheFileMap")
    private CacheFileMap cacheFileMap;
    
    public OpenApiDocumentManagerImpl(SettingsManager settingsManager) {
        super(settingsManager);
    }

    @Override
    public String getDocumentKey(String internalFileName, boolean embedded) {
        return cacheFileMap.retrieve(internalFileName).getFileId();
    }
    
}
