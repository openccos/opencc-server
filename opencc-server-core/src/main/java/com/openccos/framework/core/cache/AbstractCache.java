package com.openccos.framework.core.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.Scheduler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 封装caffeine的抽象Cache类，实现内存缓存，并支持用redis广播进行数据缓存同步
 * @author xkilu
 * @param <K> 缓存的key类
 * @param <V> 缓存的值类
 */
public abstract class AbstractCache<K, V> implements IMemCache<K, V> {
  private LoadingCache<K, V> cache;

//  private RedisManager redisManager;

  protected void install(ScheduledExecutorService executorService) {
    this.cache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .scheduler(Scheduler.forScheduledExecutorService(executorService))
            .removalListener(this::onRemove)
            .build(this::onLoad);
  }

  /** 注册标识，用于同步数据事件通知 */
  public String getIdent() {
    return this.getClass().getSimpleName();
  }

  /**
   * 通过指定的key获取缓存对象值
   * @param key 缓存key值
   * @return 缓存对象值
   */
  protected abstract V load(K key);

  /**
   * 解析String类型的key为K类型，redis广播同步时采用的string类型key
   * @param key String类型的key，一般时redis广播传递的值
   * @return K类型key值
   */
  protected abstract K parseKey(String key);
  protected void onRemove(K key, V value, RemovalCause cause) {}
  protected void onAfterLoad(K key, V value) {}

  private V onLoad(K key) {
    V result = load(key);

    onAfterLoad(key, result);

    return result;
  }

  protected final void put(K key, V value) {
    cache.put(key, value);
  }

  @Override
  public final V get(K key) {
    return cache.get(key);
  }

  @Override
  public final void publishRemove(K key) {
//    RedisBroadcastEvent message = new RedisBroadcastEvent();
//    message.setField(makeKey(key));
//    message.setKey(getIdent());
//    message.setAction(RedisBroadcastEvent.CODE_REMOVE);
//    redisManager.publish(message);
    remove(key);
  }

  protected String makeKey(K key) {
    return key.toString();
  }

  @Override
  public final void publishClear(){
//    RedisBroadcastEvent message = new RedisBroadcastEvent();
//    message.setKey(getIdent());
//    message.setAction(RedisBroadcastEvent.CODE_CLEAR);
//    redisManager.publish(message);
    clear();
  }

  // TODO: 测试是否调用了 onRemove
  void remove(K key) {
    cache.invalidate(key);
  }

  // TODO: 测试是否调用了 onRemove
  void clear(){
    cache.cleanUp();
  }
}
