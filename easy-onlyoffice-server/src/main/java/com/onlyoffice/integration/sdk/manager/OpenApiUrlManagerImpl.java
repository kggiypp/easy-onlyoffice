package com.onlyoffice.integration.sdk.manager;

import com.onlyoffice.integration.documentserver.storage.FileStoragePathBuilder;
import com.onlyoffice.manager.document.DocumentManager;
import com.onlyoffice.manager.settings.SettingsManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class OpenApiUrlManagerImpl extends UrlManagerImpl implements InitializingBean {

    @Autowired
    @Qualifier("openApiFileStorage")
    private FileStoragePathBuilder storagePathBuilderOverride;

    @Autowired
    @Qualifier("openApiDocumentManagerImpl")
    private DocumentManager documentManagerOverride;

    public OpenApiUrlManagerImpl(SettingsManager settingsManager) {
        super(settingsManager);
    }

    @Override
    public String getCreateUrl(String internalFileName) {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Field storagePathBuilderField = UrlManagerImpl.class.getDeclaredField("storagePathBuilder");
        storagePathBuilderField.setAccessible(true);
        storagePathBuilderField.set(this, storagePathBuilderOverride);

        Field documentManagerField = UrlManagerImpl.class.getDeclaredField("documentManager");
        documentManagerField.setAccessible(true);
        documentManagerField.set(this, documentManagerOverride);
    }

}
