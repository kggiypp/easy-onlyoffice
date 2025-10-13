package com.onlyoffice.integration.sdk.manager;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * 配置管理，重写该类是为了遵循spring加载多个重名application.properties配置文件时默认的
 * {@linkplain org.springframework.boot.context.config.ConfigFileApplicationListener#DEFAULT_SEARCH_LOCATIONS 覆盖规则}。
 * 
 * @author keguang
 */
@Component
@Primary
public class OpenApiSettingsManagerImpl extends SettingsManagerImpl implements EnvironmentAware, InitializingBean {

    private static final String SETTINGS_PREFIX = "docservice";

    private static Properties properties = new Properties();
    
    private Environment environment;
    
    @Override
    public String getSetting(String name) {
        return properties.getProperty(name);
    }

    @Override
    public void setSetting(String name, String value) {
        properties.put(name, value);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, String> map = Binder.get(environment)
                .bind(SETTINGS_PREFIX, Bindable.mapOf(String.class, String.class))
                .orElseGet(Collections::emptyMap);
        properties.putAll(map);
    }
    
}
