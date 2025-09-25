package com.github.onlyoffice.sdk.callback;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.onlyoffice.sdk.request.OpenApiRequestManagerImpl.DefaultCallback;
import com.onlyoffice.manager.request.RequestManager;
import com.onlyoffice.manager.security.JwtManager;
import com.onlyoffice.manager.settings.SettingsManager;
import com.onlyoffice.model.documenteditor.Callback;
import com.onlyoffice.service.documenteditor.callback.DefaultCallbackService;

public class OpenApiCallbackService extends DefaultCallbackService {
    
    private FileDataHandler fileDataHandler;
    
    private RequestManager requestManager;
    
    public OpenApiCallbackService(JwtManager jwtManager, SettingsManager settingsManager,
            FileDataHandler fileDataHandler, RequestManager requestManager) {
        
        super(jwtManager, settingsManager);
        
        this.fileDataHandler = fileDataHandler;
        this.requestManager = requestManager;
    }
    
    @Override
    public void handlerForcesave(Callback callback, String fileId) throws Exception {
        handlerSave(callback, fileId);
    }

    @Override
    public void handlerSave(Callback callback, String fileId) throws Exception {
        String downloadUrl = callback.getUrl();
        
        byte[] data = requestManager.executeGetRequest(downloadUrl, new DefaultCallback<>(new TypeReference<byte[]>() {}));
        
        fileDataHandler.handData(data, fileId);
    }
    
}
