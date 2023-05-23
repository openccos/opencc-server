package com.openccos.framework.core.session;

import com.openccos.framework.core.exception.UnauthenticatedException;
import com.openccos.framework.core.redis.RedisManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;

/**
 * 〈session工具类〉
 *
 * @author kevin
 * @since 1.0.0
 */
public class SessionUtil {
  private SessionUtil() {
  }

  public static Object getSession() {
    return RequestContextHolder.currentRequestAttributes().getAttribute(UserSession.USER_SESSION, 0);
  }

  public static String readToken(HttpServletRequest request) {
    String token = request.getHeader("Auth-Token");

    if (token == null) {
      token = request.getParameter("auth_token");
    }
    return token;
  }

  public static UserSession checkSession(HttpServletRequest request, RedisManager redisManager) {
    return checkSession(readToken(request), redisManager);
  }

  public static UserSession checkSession(String accessToken, RedisManager redisManager) {
    if (StringUtils.isBlank(accessToken)) {
      throw new UnauthenticatedException("not find Auth-Token in header");
    }

    UserSession us = redisManager.get(accessToken, UserSession.class);
    if (us == null) {
      throw new UnauthenticatedException("not find UserSession by token: " + accessToken);
    }

    return us;
  }

}
