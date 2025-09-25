package com.onlyoffice.integration.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 该类是基于LinkedHashMap实现的LRU策略的一个缓存map，
 * 注意该类<strong>非线程安全的</strong>，
 * 不能保证多线程大量并发读写场景下容器元素一定不超过{@code maxSize}阈值，
 * 如要严格控制容器元素不超过阈值，需要调用者额外考虑并发读写同步逻辑。
 * 
 * <p> 可通过{@code accessOrder}参数控制通过{@link #get(Object)}访问元素后，
 * 是否更新该元素的新旧顺序将其变更为最新元素。
 * 
 * @author keguang
 */
public class LruCacheMap<K, V> extends LinkedHashMap<K, V> {
    
    private static final long serialVersionUID = 7859085100524129829L;
    
    private final int maxSize;
    
    public LruCacheMap(int maxSize) {
        this(maxSize, 0.75f, true);
    }
    
    public LruCacheMap(int maxSize, float loadFactor, boolean accessOrder) {
        super(maxSize << 1, loadFactor, accessOrder);
        this.maxSize = maxSize;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > this.maxSize;
    }
    
}
