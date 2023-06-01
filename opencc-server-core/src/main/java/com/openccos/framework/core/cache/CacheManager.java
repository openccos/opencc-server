package com.openccos.framework.core.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 内存缓存管理器，管理lazy加载的缓存，长时间不使用会失效
 */
public class CacheManager {
  private final Map<String, AbstractCache> cacheMap = new HashMap<>();
  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

  public AbstractCache getCache(String ident) {
    return cacheMap.get(ident);
  }

  // 初始化cache
  public boolean install(AbstractCache cache) {
    if (cacheMap.putIfAbsent(cache.getIdent(), cache) == null) {
      cache.install(executorService);
      return true;
    }

    return false;
  }

  /**
   * 删除指定键值的缓存对象
   * @param ident 缓存类型值
   * @param key 缓存键值
   */
  public void remove(String ident, String key) {
    AbstractCache cache = getCache(ident);
    if (cache != null) {
      cache.remove(cache.parseKey(key));
    }
  }

  /**
   * 清除缓存对象
   * @param ident 缓存对象标识
   */
  public void clear(String ident) {
    AbstractCache cache = getCache(ident);
    if (cache != null) {
      cache.clear();
    }
  }

  /**
   * 清除指定用户权限本地缓存
   */
  public <K> boolean removePermits(String ident, K key) {
    AbstractCache<K, ?> cache = getCache(ident);

    if (cache != null) {
      cache.publishRemove(key);
      return true;
    }

    return false;
  }

  /**
   * 清除所有用户权限本地缓存
   */
  public boolean clearPermits(String ident) {
    AbstractCache cache = getCache(ident);

    if (cache != null) {
      cache.publishClear();
      return true;
    }

    return false;
  }
}
