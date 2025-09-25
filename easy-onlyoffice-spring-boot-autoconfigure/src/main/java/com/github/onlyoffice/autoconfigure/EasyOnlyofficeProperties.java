package com.github.onlyoffice.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

import static com.github.onlyoffice.sdk.constants.PropertyKey.EASY_ONLYOFFICE_SERVER_URL;

/**
 *  configuration properties for easy-onlyoffice
 * 
 * @author keguang
 */
@ConfigurationProperties("easy-onlyoffice")
@Data
public class EasyOnlyofficeProperties {

    /**
     * 扩展属性配置，以及适配easy-onlyoffice需要的配置
     * 例：easy-onlyoffice.properties.key=value
     */
    private final Properties properties = new Properties();
    
    /**
     * 是否开启easy-onlyoffice自动装配，默认开启
     */
    private Boolean enabled;
    
    private final Server server = new Server();
    
    /**
     * Server properties.
     */
    @Data
    public class Server {

        /**
         * easy-onlyoffice服务url
         */
        private String url;
        
        public void setUrl(String url) {
            this.url = url;
            properties.setProperty(EASY_ONLYOFFICE_SERVER_URL, url);
        }
        
    }
    
}
