package com.openccos.framework.core.cache;

/**
 * 键类型为Long的缓存对象
 * @author xkliu
 * @param <V> 缓存对象的类型
 */
public abstract class AbstractLongKeyCache<V> extends AbstractCache<Long, V> {
  @Override
  protected Long parseKey(String key) {
    return Long.parseLong(key);
  }
}
