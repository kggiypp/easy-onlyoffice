package com.github.onlyoffice.autoconfigure;

import com.github.onlyoffice.sdk.callback.FileDataHandler;
import com.github.onlyoffice.sdk.callback.OpenApiCallbackService;
import com.github.onlyoffice.sdk.request.OpenApiRequestManagerImpl;
import com.onlyoffice.manager.request.RequestManager;
import com.onlyoffice.manager.security.DefaultJwtManager;
import com.onlyoffice.manager.security.JwtManager;
import com.onlyoffice.manager.settings.DefaultSettingsManager;
import com.onlyoffice.manager.settings.SettingsManager;
import com.onlyoffice.manager.url.DefaultUrlManager;
import com.onlyoffice.manager.url.UrlManager;
import com.onlyoffice.service.documenteditor.callback.CallbackService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * easy-onlyoffice配置类，默认会开启自动装配
 * 
 * @author keguang
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "easy-onlyoffice", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(EasyOnlyofficeProperties.class)
public class EasyOnlyofficeConfiguration {
    
    @Bean
    public SettingsManager settingsManager(EasyOnlyofficeProperties easyOnlyofficeProperties) {
        
        return new DefaultSettingsManager() {
            @Override
            public String getSetting(String name) {
                return easyOnlyofficeProperties.getProperties().getProperty(name);
            }
            @Override
            public void setSetting(String name, String value) {
                easyOnlyofficeProperties.getProperties().setProperty(name, value);
            }
        };
    }
    
    @Bean
    @ConditionalOnMissingBean
    public UrlManager urlManager(SettingsManager settingsManager) {
        return new DefaultUrlManager(settingsManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtManager jwtManager(SettingsManager settingsManager) {
        return new DefaultJwtManager(settingsManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestManager requestManager(UrlManager urlManager, JwtManager jwtManager,
            SettingsManager settingsManager) {
        
        return new OpenApiRequestManagerImpl(urlManager, jwtManager, settingsManager);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public CallbackService callbackService(JwtManager jwtManager, SettingsManager settingsManager,
            FileDataHandler fileDataHandler, RequestManager requestManager) {
        
        return new OpenApiCallbackService(jwtManager, settingsManager,fileDataHandler, requestManager);
    }
    
}
