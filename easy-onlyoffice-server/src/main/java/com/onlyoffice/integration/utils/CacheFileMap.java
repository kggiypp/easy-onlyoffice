package com.onlyoffice.integration.utils;

import com.onlyoffice.integration.dto.FileInfo;
import lombok.SneakyThrows;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * 文件信息缓存map
 * 
 * @author keguang
 */
public class CacheFileMap extends LruCacheMap<String, FileInfo> implements DisposableBean {

    private static final long serialVersionUID = 5896360269241664358L;
    
    private volatile boolean destroyed = false;
    
    public CacheFileMap(int maxSize) {
        super(maxSize);
        ShutdownHook.registerOne(this);
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<String, FileInfo> eldest) {
        boolean remove = super.removeEldestEntry(eldest);
        if (remove) {
            String filePath = eldest.getValue().getFilePath();
            deleteIfExists(Paths.get(filePath));
        }
        return remove;
    }

    /**
     * 根据内部文件名检索出缓存的文件信息，不用担心会检索出多个文件，
     * 因为系统生成的内部文件名也是唯一的（不重写改变文件夹路径的情况下）
     * @param internalFileName 内部文件名
     * @return
     */
    public FileInfo retrieve(String internalFileName) {
        return values().parallelStream()
                .filter(fileInfo -> fileInfo.getInternalFileName().equals(internalFileName))
                .findAny()
                .orElse(null);
    }

    @Override
    public void destroy() {
        if (destroyed) {
            return;
        }
        clearCacheFiles();
        ShutdownHook.unregisterOne(this);
        destroyed = true;
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
    }
    
    public void clearCacheFiles() {
        values().parallelStream()
                .map(FileInfo::getFilePath)
                .filter(StringUtils::hasText)
                .map(Paths::get)
                .forEach(this::deleteIfExists);
    }
    
    @SneakyThrows
    private void deleteIfExists(Path path) {
        Files.deleteIfExists(path);
    }
    
    private static class ShutdownHook {
        
        private static final String SHUTDOWN_HOOK_THREAD_NAME = "cacheFilesShutdownHook";
        
        private static Collection<CacheFileMap> collection = new HashSet<>();
        
        static {
            registerShutdownHook();
        }
        
        private static void registerShutdownHook() {
            Thread thread = new Thread(ShutdownHook::doDestroy);
            thread.setName(SHUTDOWN_HOOK_THREAD_NAME);
            Runtime.getRuntime().addShutdownHook(thread);
        }
        
        private static void registerOne(CacheFileMap one) {
            collection.add(one);
        }
        
        private static void unregisterOne(CacheFileMap one) {
            collection.remove(one);
        }
        
        private static void doDestroy() {
            try {
                collection.parallelStream()
                        .filter(one -> !one.destroyed)
                        .forEach(CacheFileMap::destroy);
            } catch (Throwable e) {
                // ignore and discard
            }
        }
        
    }
    
}
