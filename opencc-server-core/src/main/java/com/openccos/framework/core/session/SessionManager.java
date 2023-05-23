package com.openccos.framework.core.session;

import com.openccos.framework.core.db.jdbc.IdGenerator;
import com.openccos.framework.core.redis.RedisBroadcastEvent;
import com.openccos.framework.core.redis.RedisManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Random;

public class SessionManager {
  private final RedisManager redisManager;
  private final IdGenerator idGenerator;

  public SessionManager(RedisManager redisManager, IdGenerator idGenerator) {
    this.redisManager = redisManager;
    this.idGenerator = idGenerator;
  }

  // 登录，产生session
  public String login(UserSession userSession) {
    if (userSession != null) {
      String sid = RedisManager.PREFIX_SESSION + Long.toHexString(idGenerator.nextId()) + "_" + Integer.toHexString(new Random().nextInt());

      redisManager.set(sid, userSession, RedisManager.SESSION_EXPIRE_SEC);

      return sid;
    }

    return null;
  }

  public void logout() {
    String sid = (String)RequestContextHolder.currentRequestAttributes().getAttribute(UserSession.USER_TOKEN, RequestAttributes.SCOPE_REQUEST);
    if (StringUtils.isNoneBlank(sid)) {
      redisManager.del(sid);
    }
  }

  // token延时
  public boolean flush(String token) {
    if (token != null) {
      // 更新Token redis TTL
      return redisManager.expire(token, RedisManager.SESSION_EXPIRE_SEC);
    }

    return false;
  }

  public UserSession find(String token) {
    if (token != null) {
      // 更新Token redis TTL
      return redisManager.get(token, UserSession.class);
    }

    return null;
  }

  /**
   * 清除指定用户权限本地缓存
   * @param userId 用户ID
   */
  public void removePermits(long userId) {
    RedisBroadcastEvent event = new RedisBroadcastEvent();
    event.setAction(RedisBroadcastEvent.CODE_REMOVE);
    event.setKey("ReamlCache");
    event.setField(userId + "");
    redisManager.publish(event);
  }

  /**
   * 清除所有用户权限本地缓存
   */
  public void clearPermits() {
    RedisBroadcastEvent event = new RedisBroadcastEvent();
    event.setAction(RedisBroadcastEvent.CODE_CLEAR);
    event.setKey("ReamlCache");
    redisManager.publish(event);
  }
}
