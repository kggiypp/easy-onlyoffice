package com.github.onlyoffice.sdk.utils;

import java.util.stream.Stream;

/**
 * 枚举工具类
 * 
 * @author keguang
 */
public class EnumUtil {

    /**
     * 判断一个枚举是不是 被包含于 给定的多个枚举值里
     * @param one
     * @param multiple
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> boolean isOneOfTheMutiple(Enum<T> one, Enum<T>... multiple) {
        int oneBit = 1 << one.ordinal();
        
        int aggreateBit = Stream.of(multiple)
                .map(Enum::ordinal)
                .reduce((left, right) -> 1 << left | 1 << right)
                .orElse(0);
        
        return (oneBit & aggreateBit) != 0;
    }
    
}
