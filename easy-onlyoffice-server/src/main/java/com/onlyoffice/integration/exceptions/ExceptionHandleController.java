package com.onlyoffice.integration.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

/**
 * 全局异常处理
 * 
 * @author keguang
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandleController {
    
    @Autowired
    private HttpServletResponse response;
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("出现异常，堆栈信息：", e);
        int status = response.getStatus();
        if (status == OK.value()) {
            status = INTERNAL_SERVER_ERROR.value();
        }
        String message = e.getMessage();
        if (!StringUtils.hasText(message)) {
            message = "系统内部错误，请联系开发";
        }
        return ResponseEntity.status(status).body(message);
    }
    
}
