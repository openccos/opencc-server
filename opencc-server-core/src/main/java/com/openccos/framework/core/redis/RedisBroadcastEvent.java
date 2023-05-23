package com.openccos.framework.core.redis;

import lombok.Data;

/**
 * redis 广播消息
 */
@Data
public class RedisBroadcastEvent {
  public static final int CODE_REMOVE = 1;
  public static final int CODE_CLEAR = 2;
  /** 缓存更新 */
  public static final int CODE_CACHE_UPDATE = 3;

  private int action;
  private String field;
  private String key;
  private long id;
}
