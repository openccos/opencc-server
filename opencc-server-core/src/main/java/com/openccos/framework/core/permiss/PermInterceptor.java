package com.openccos.framework.core.permiss;

import com.openccos.framework.core.R;
import com.openccos.framework.core.annotation.CcPerm;
import com.openccos.framework.core.db.DbEngine;
import com.openccos.framework.core.exception.UnauthenticatedException;
import com.openccos.framework.core.redis.RedisManager;
import com.openccos.framework.core.session.SessionUtil;
import com.openccos.framework.core.session.UserSession;
import com.openccos.framework.core.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

/**
 * @author kevin
 */
@Slf4j
public class PermInterceptor implements HandlerInterceptor {
    private RedisManager redisManager;
    private PermCache permCache;

    public PermInterceptor(RedisManager redisManager, DbEngine dbEngine) {
        this.redisManager = redisManager;
        this.permCache = new PermCache(dbEngine);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception  {
        HandlerMethod handlerMethod = null;

        // 如果请求的不是方法 则直接跳过当前拦截器
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        handlerMethod = (HandlerMethod) handler;

        // 获取当前请求的方法
        Method method = handlerMethod.getMethod();

        // 获取方法上注解
        CcPerm ccPerm = method.getAnnotation(CcPerm.class);

        // 如果方法没有注解,则去类上去获取 如果方法有则使用方法的注解,方法的注解优先级高于类的注解
        if (ccPerm == null) {
            ccPerm = handlerMethod.getBeanType().getAnnotation(CcPerm.class);
        }

        // 如果没有Perm 注解，默认为通用会话权限
        String authorityCode = CcPerm.SESSION;
        if (ccPerm != null) {
            authorityCode = ccPerm.value();
        }

        // 如果不包含权限编码,则表示这个接口登录就可以访问
        if (!CcPerm.NONE.equals(authorityCode)) {
            String token = SessionUtil.readToken(request);
            if (StringUtils.isBlank(token)) {
                throw new UnauthenticatedException("not find Auth-Token in header");
            }

            UserSession userSession = SessionUtil.checkSession(token, redisManager);

            // 用户还没有登录
            if (userSession == null) {
                noPermission(request, response);
                return false;
            }

            request.setAttribute(UserSession.USER_TOKEN, token);
            request.setAttribute(UserSession.USER_SESSION, userSession);

            // 如果标记了权限注解，则判断权限
            if (checkPermission(authorityCode, userSession)) {
                // 更新Token redis TTL
                redisManager.expire(token, RedisManager.SESSION_EXPIRE_SEC);
                return true;
            } else {
//                    throw new ForbiddenException("user not permission: " + permissionValue);
                noPermission(request, response);
                return false;
            }
        }

        return true;
    }

    /**
     *  权限检查
     */
    private boolean checkPermission(String permissionValue, UserSession us) {
        if (StringUtils.isBlank(permissionValue)) {
            return true;
        }

        // 从本地缓存或数据库中获取该用户的权限信息
        PermChecker permissionSet = permCache.get(us.getUserId());

//    if (MapUtils.isEmpty(permissionSet)) {
//      throw new ForbiddenException("empty permission");
//    }

        PermCheckItem permChecker = permissionSet.get(permissionValue);

        if (permChecker != null) {
            return true;
        }

        while (true) {
            permissionValue = getParentPermValue(permissionValue);

            if (permissionValue != null) {
                permChecker = permissionSet.get(permissionValue);
                if (permChecker != null && permChecker.isPrefixMath()) {
                    return true;
                }
            } else {
                break;
            }
        }

        return false;
    }


    private static String getParentPermValue(String permissionValue) {
        if (permissionValue.length() > 0) {

            int pos = permissionValue.lastIndexOf(':');
            if (pos > 0) {
                return permissionValue.substring(0, pos);
            }

            return "";
        }

        return null;
    }


    /**
     * 没有权限的处理方式
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    public void noPermission(HttpServletRequest request, HttpServletResponse response) {
        if (isAjax(request)) {
            try {
                OutputStream out = response.getOutputStream();

                out.write(JsonUtil.encodeBytes(R.error(401, "您没有访问权限")));
                out.flush();
                out.close();

            } catch (IOException e) {
                log.error("error", e);
            }
        } else {
            try {
                response.sendRedirect("error/noPermission");
            } catch (IOException e) {
                log.error("error", e);
            }

        }
    }

    /**
     * 判断请求是否为ajax请求
     *
     * @param request HttpServletRequest
     * @return Boolean
     */
    public boolean isAjax(HttpServletRequest request) {
        return request.getHeader("x-requested-with") != null
                && "XMLHttpRequest".equalsIgnoreCase(request.getHeader("x-requested-with"));
    }

}
