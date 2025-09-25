package com.onlyoffice.integration.sdk.manager;

import com.onlyoffice.manager.settings.SettingsManager;
import org.springframework.stereotype.Component;

@Component
public class OpenApiUrlManagerImpl extends UrlManagerImpl {
    
    public OpenApiUrlManagerImpl(SettingsManager settingsManager) {
        super(settingsManager);
    }

    @Override
    public String getCreateUrl(String internalFileName) {
        return null;
    }
    
}
