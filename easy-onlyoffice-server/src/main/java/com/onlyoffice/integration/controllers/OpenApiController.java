package com.onlyoffice.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onlyoffice.sdk.dto.LinkRequest;
import com.github.onlyoffice.sdk.utils.StreamUtil;
import com.onlyoffice.integration.documentserver.storage.FileStoragePathBuilder;
import com.onlyoffice.integration.documentserver.storage.OpenApiFileStorage;
import com.onlyoffice.integration.sdk.manager.DocumentManager;
import com.onlyoffice.integration.sdk.manager.UrlManager;
import com.onlyoffice.model.documenteditor.Callback;
import com.onlyoffice.model.documenteditor.Config;
import com.onlyoffice.model.documenteditor.callback.Status;
import com.onlyoffice.model.documenteditor.config.EditorConfig;
import com.onlyoffice.service.documenteditor.config.ConfigService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.github.onlyoffice.sdk.constants.OpenApiUrl.Path.BASE;
import static com.github.onlyoffice.sdk.constants.OpenApiUrl.Path.EDITOR;
import static com.github.onlyoffice.sdk.constants.OpenApiUrl.Path.EDITOR_LINK;
import static com.github.onlyoffice.sdk.constants.OpenApiUrl.QUERY_PARAM.CALLBACK;
import static com.github.onlyoffice.sdk.constants.OpenApiUrl.QUERY_PARAM.FILE_ID;
import static com.github.onlyoffice.sdk.constants.OpenApiUrl.QUERY_PARAM.SRC;
import static com.onlyoffice.model.documenteditor.config.document.Type.DESKTOP;
import static com.onlyoffice.model.documenteditor.config.editorconfig.Mode.EDIT;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT;

/**
 * 二次开发对外开放接口
 * 
 * @author keguang
 */
@SuppressWarnings("SpringMVCViewInspection")
@CrossOrigin("*")
@Controller
@RequestMapping(BASE)
public class OpenApiController {

    @Autowired
    private DocumentManager documentManager;

    @Autowired
    private FileStoragePathBuilder storagePathBuilder;

    @Autowired
    @Qualifier("openApiUrlManagerImpl")
    private UrlManager urlManager;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    @Qualifier("openApiConfigServiceImpl")
    private ConfigService configService;

    @Autowired
    private OpenApiFileStorage fileStorage;
    
    @Autowired
    private HttpServletResponse servletResponse;
    
    /**
     * 该方法根据指定的内部文件id生成一个可在线编辑的html页面编辑器，并且会回调指定的地址，
     * 回调的时机等详情可查看
     * <a href="https://api.onlyoffice.com/zh-CN/docs/docs-api/get-started/how-it-works/saving-file/">官方文档</a>
     * 回调的入参json格式可参考{@link Callback}
     * 
     * <p>另外需注意html编辑页面首次被打开时就会请求一次回调地址，Callback会传入{@link Status#EDITING}的标记状态，
     * 客户端处理后响应体json需为 {"error":"0"} 的格式，否则编辑页面无法正常打开
     * 
     * @param fileId 文件id
     * @param callbackUrl 回调地址
     * @param model
     * @return html页面编辑器
     */
    @GetMapping(EDITOR)
    public String editor(@RequestParam(FILE_ID) String fileId, @RequestParam(CALLBACK) String callbackUrl,
            final Model model) {

        Config config = configService.createConfig(fileId, EDIT, DESKTOP);

        EditorConfig editorConfig = config.getEditorConfig();
        editorConfig.setCallbackUrl(callbackUrl);
        editorConfig.setLang(Locale.CHINESE.toLanguageTag());
        
        model.addAttribute("model", config);
        // create the document service api URL and add it to the model
        model.addAttribute("docserviceApiUrl", urlManager.getDocumentServerApiUrl());
        // get an image and add it to the model
        model.addAttribute("dataInsertImage",  getInsertImage());
        
        return "editor.html";
    }

    /**
     * 为指定的资源文件生成一个可在线编辑的链接地址
     * @param srcUrl 可直接访问的文件资源路径
     * @param callbackUrl 回调地址
     * @return 可在线编辑的一个链接地址
     */
    @GetMapping(EDITOR_LINK)
    @ResponseBody
    @SneakyThrows
    public String editorLink(@RequestParam(SRC) String srcUrl, @RequestParam(CALLBACK) String callbackUrl) {
        URL url = new URL(srcUrl);
        URLConnection con = url.openConnection();
        
        String filename = extractFilename(srcUrl, con);
        byte[] data = StreamUtil.readAllBytes(con.getInputStream());
        
        return editorLink(new LinkRequest(data, callbackUrl, filename));
    }

    /**
     * 为给定的文件数据生成一个可在线编辑的链接地址
     * @param linkRequest 请求参数
     * @return 可在线编辑的一个链接地址
     */
    @PostMapping(EDITOR_LINK)
    @ResponseBody
    public String editorLink(@RequestBody LinkRequest linkRequest) {
        String error = validateFile(linkRequest);
        if (error != null) {
            servletResponse.setStatus(BAD_REQUEST.value());
            return error;
        }
        
        String fileId = fileStorage.updateFile(linkRequest.getFilename(), linkRequest.getData());
        
        DefaultUriBuilderFactory builderFactory = new DefaultUriBuilderFactory();
        builderFactory.setEncodingMode(URI_COMPONENT);
        
        String editorUrl = fileStorage.getServerUrl(false) + BASE + EDITOR;
        UriBuilder builder = builderFactory.uriString(editorUrl);

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add(FILE_ID, fileId);
        queryParam.add(CALLBACK, linkRequest.getCallback());
        builder.queryParams(queryParam);
        
        return builder.build().toString();
    }

    @SneakyThrows
    private String getInsertImage() {  // get an image that will be inserted into the document
        Map<String, Object> dataInsertImage = new HashMap<>();
        dataInsertImage.put("fileType", "svg");
        dataInsertImage.put("url", storagePathBuilder.getServerUrl(true) + "/css/img/logo.svg");
        
        return objectMapper.writeValueAsString(dataInsertImage)
                .substring(1, objectMapper.writeValueAsString(dataInsertImage).length() - 1);
    }

    private String validateFile(LinkRequest linkRequest) {
        if (documentManager.getMaxFileSize() < linkRequest.getData().length) {
            return "超过了最大文件限制" + (documentManager.getMaxFileSize() >> 20) + "MB";
        }
        if (documentManager.getDocumentType(linkRequest.getFilename()) == null) {
            return "不支持该文件类型";
        }
        return null;
    }

    @SneakyThrows
    private String extractFilename(String srcUrl, URLConnection con) {
        String fileName = Optional.ofNullable(con.getHeaderField(CONTENT_DISPOSITION))
                .map(ContentDisposition::parse)
                .map(ContentDisposition::getFilename)
                .orElse(null);
        
        if (fileName == null) {
            fileName = fileStorage.getFileName(srcUrl);
        }
        
        Assert.hasText(fileName, "filename不能为空");
        return URLDecoder.decode(fileName, StandardCharsets.UTF_8.name());
    }
    
}
