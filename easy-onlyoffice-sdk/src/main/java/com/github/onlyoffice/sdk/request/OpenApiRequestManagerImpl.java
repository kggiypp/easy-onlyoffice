package com.github.onlyoffice.sdk.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onlyoffice.sdk.dto.LinkRequest;
import com.github.onlyoffice.sdk.utils.StreamUtil;
import com.onlyoffice.manager.request.DefaultRequestManager;
import com.onlyoffice.manager.security.JwtManager;
import com.onlyoffice.manager.settings.SettingsManager;
import com.onlyoffice.manager.url.UrlManager;
import com.onlyoffice.model.common.RequestEntity;
import com.onlyoffice.model.settings.HttpClientSettings;
import com.onlyoffice.model.settings.security.Security;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.onlyoffice.sdk.constants.OpenApiUrl.Path.BASE;
import static com.github.onlyoffice.sdk.constants.OpenApiUrl.Path.EDITOR_LINK;
import static com.github.onlyoffice.sdk.constants.OpenApiUrl.QUERY_PARAM.CALLBACK;
import static com.github.onlyoffice.sdk.constants.OpenApiUrl.QUERY_PARAM.SRC;
import static com.github.onlyoffice.sdk.constants.PropertyKey.EASY_ONLYOFFICE_SERVER_URL;
import static org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT;

/**
 * easy-onlyoffice扩展接口实现类
 * 
 * @author keguang
 */
public class OpenApiRequestManagerImpl extends DefaultRequestManager implements OpenApiRequestManager {

    private Method executeRequest;

    private Method createPostRequest;

    public OpenApiRequestManagerImpl(UrlManager urlManager, JwtManager jwtManager, SettingsManager settingsManager) {
        super(urlManager, jwtManager, settingsManager);
        initSuperMethodReference();
    }

    @SneakyThrows
    @Override
    public String editorLink(String srcUrl, String callbackUrl, Header... headers) {
        DefaultUriBuilderFactory builderFactory = new DefaultUriBuilderFactory();
        builderFactory.setEncodingMode(URI_COMPONENT);

        String url = getSettingsManager().getSetting(EASY_ONLYOFFICE_SERVER_URL) + BASE + EDITOR_LINK;
        UriBuilder builder = builderFactory.uriString(url);

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add(SRC, srcUrl);
        queryParam.add(CALLBACK, callbackUrl);

        builder.queryParams(queryParam);
        url = builder.build().toString();

        HttpGet httpGet = new HttpGet(url);
        addHeaders(httpGet, headers);

        return (String) executeRequest.invoke(this, httpGet, null, new DefaultCallback<>(new TypeReference<String>() {}));
    }

    @SneakyThrows
    @Override
    public String editorLink(LinkRequest linkRequest, Header... headers) {
        Security security = Security.builder()
                .key(getSettingsManager().getSecurityKey())
                .header(getSettingsManager().getSecurityHeader())
                .prefix(getSettingsManager().getSecurityPrefix())
                .build();

        String url = getSettingsManager().getSetting(EASY_ONLYOFFICE_SERVER_URL) + BASE + EDITOR_LINK;

        HttpPost httpPost = (HttpPost) createPostRequest.invoke(this, url, linkRequest, security);
        addHeaders(httpPost, headers);

        return (String) executeRequest.invoke(this, httpPost, null, new DefaultCallback<>(new TypeReference<String>() {}));
    }

    private void addHeaders(HttpUriRequest request, Header... headers) {
        Optional.ofNullable(headers).map(Stream::of).ifPresent(stream -> stream.forEach(request::addHeader));
    }

    @SneakyThrows
    private void initSuperMethodReference() {
        executeRequest = DefaultRequestManager.class.getDeclaredMethod("executeRequest", HttpUriRequest.class,
                HttpClientSettings.class, Callback.class);
        executeRequest.setAccessible(true);
        createPostRequest = DefaultRequestManager.class.getDeclaredMethod("createPostRequest", String.class,
                RequestEntity.class, Security.class);
        createPostRequest.setAccessible(true);
    }


    public static class DefaultCallback<Result> implements Callback<Result> {

        public static ObjectMapper objectMapper = new ObjectMapper();

        private TypeReference<Result> typeReference;

        public DefaultCallback(TypeReference<Result> typeReference) {
            this.typeReference = typeReference;
        }

        @Override
        public Result doWork(Object response) throws Exception {
            InputStream in = ((HttpEntity) response).getContent();
            if (typeReference.getType() == byte[].class) {
                return (Result) StreamUtil.readAllBytes(in);
            }
            String content = IOUtils.toString(in, StandardCharsets.UTF_8);
            if (typeReference.getType() == String.class) {
                // noinspection unchecked
                return (Result) content;
            }
            return objectMapper.readValue(content, typeReference);
        }

    }

}
