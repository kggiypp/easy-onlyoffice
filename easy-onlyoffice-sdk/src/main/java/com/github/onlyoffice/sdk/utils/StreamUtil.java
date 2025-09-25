package com.github.onlyoffice.sdk.utils;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 操作流工具类
 * 
 * @author keguang
 */
public class StreamUtil {

    /**
     * 该方法是为了java 8适配java 11的InputStream.readAllBytes()方法，
     * 因为官方样例代码是基于java 11编写的
     * @param in
     * @return
     */
    @SneakyThrows
    public static byte[] readAllBytes(InputStream in) {
        byte[] buff = new byte[1 << 10 << 3]; // 1024 * 8
        ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());
        int count;
        while ((count = in.read(buff)) != -1) {
            out.write(buff, 0, count);
        }
        return out.toByteArray();
    }
    
}
