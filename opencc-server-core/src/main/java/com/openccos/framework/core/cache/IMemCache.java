package com.openccos.framework.core.cache;

public interface IMemCache<K, V> {
  /**
   * 根据键值获取缓存对象
   * @param key 缓存唯一键值
   * @return 缓存对象
   */
  V get(K key);

  /**
   * 给redis发送广播消息，多服务同步删除相同key值的缓存对象
   * @param key 缓存键值
   */
  void publishRemove(K key);

  /**
   * 给redis发送广播消息，多服务同步清空相同类型的缓存对象
   */
  void publishClear();
}
