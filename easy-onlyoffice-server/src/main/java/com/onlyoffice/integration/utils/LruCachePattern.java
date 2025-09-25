package com.onlyoffice.integration.utils;

import org.springframework.lang.NonNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

/**
 * 缓存最近使用到的正则Pattern对象使其可复用，减少每次重新编译正则串的性能开销
 * 
 * @author keguang
 */
public class LruCachePattern {

    /**
     * 指定缓存的数量大小
     */
    private static final int LIMIT_SIZE = 20;

    /**
     * 缓存容器，基于LinkedHashMap特性实现LRU策略
     */
    private static volatile Map<String, Pattern> cachedMap = new LruCacheMap<>(LIMIT_SIZE);

    /**
     * 某一时刻正在被访问的正则串，避免同一正则串被重复编译浪费资源
     */
    private static volatile Set<String> currentlyInAccessKeySet = new HashSet<>();

    /**
     * 锁
     */
    private static ReentrantLock lock = new ReentrantLock();
    
    /**
     * 获取编译过的缓存Pattern，如果不存在则编译
     * @param regex 正则表达式
     * @return 编译后的Pattern
     */
    public static Pattern getCachedPattern(@NonNull String regex) {
        return getCachedPattern(regex, 0);
    }
    
    /**
     * 获取编译过的缓存Pattern，如果不存在则编译
     * @param regex 正则表达式
     * @param flags 匹配模式，参考Pattern内部定义的flags常量
     * @return 编译后的Pattern
     */
    public static Pattern getCachedPattern(@NonNull String regex, int flags) {
        try {
            while (!currentlyInAccessKeySet.add(regex)) {
                Thread.yield();
            }
            return cachedMap.computeIfAbsent(regex, k -> {
                Pattern pattern = Pattern.compile(regex, flags);
                lock.lock();
                return pattern;
            });
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
            currentlyInAccessKeySet.remove(regex);
        }
    }
    
}
