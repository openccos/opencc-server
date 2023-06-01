package com.openccos.framework.core.permiss;

import com.openccos.framework.core.cache.AbstractLongKeyCache;
import com.openccos.framework.core.db.DbEngine;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户权限集合缓存器
 * @author xkliu
 */
@Log4j2
public class PermCache extends AbstractLongKeyCache<PermChecker> {
    public static final String REALM_CACHE = "realmCache";
    private DbEngine dbEngine;

    private static final long ENTERPRISE_ADMIN_ID = 1;

    public PermCache(DbEngine dbEngine) {
        this.dbEngine = dbEngine;
    }

    @Override
    public String getIdent() {
        return REALM_CACHE;
    }

    @Override
    protected PermChecker load(Long key) {
        // admin
        if (key == ENTERPRISE_ADMIN_ID) {
            return PermChecker.build(new HashSet<>(Collections.singletonList("*")));
        }

        // TODO: 合并相同角色，自己到缓存里面获取
        Set<String> permissions = new HashSet<>();

        List<String> permList = dbEngine.queryStringList("SELECT menu_api_perm FROM sys_menu WHERE menu_id in\n" +
                "(SELECT rmp_menu_id from sys_role_menu_privilege WHERE rmp_role_id in\n" +
                "(SELECT role_id FROM iot_user.sys_role WHERE role_id IN" +
                "(SELECT ur_role_id FROM iot_user.sys_user_role WHERE ur_user_id=?)))", key);

        if (permList != null) {
            for (String perm: permList) {
                for (String item: perm.split(",")) {
                    permissions.add(item.trim());
                }
            }
        }

        return PermChecker.build(permissions);
    }

}
