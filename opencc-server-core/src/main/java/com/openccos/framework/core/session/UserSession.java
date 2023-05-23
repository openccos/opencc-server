package com.openccos.framework.core.session;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 用户会话缓存，前端调用API时传输 Auto_Token 参数来查找用户会话
 * @author xkliu
 */
@Getter @Setter
public class UserSession implements Serializable {
    private static final long serialVersionUID = 3854315462714888716L;
    public static final String USER_SESSION = "_CC_USER_SESSION";
    public static final String USER_TOKEN = "_CC_TOKEN";
    public static final String BODY_MAP = "_CC_BODY_MAP";
    public static final String AUTH_TOKEN = USER_TOKEN;

    // 用户ID
    private long userId;
    // 当前组织ID
    private long companyId;
    // 站点ID
    private long siteId;
    // 终端类型
    private byte terminalType;
}
