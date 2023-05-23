package com.openccos.framework.core.util;

import java.util.function.Function;


/**
 * 〈工具类〉
 *
 * @author kevin
 * @since 1.0.0
 */
public class CommonUtil {
    public static Function<String, String> formatUrl = u -> {
        if (!u.startsWith("/")) {
            u = "/" + u;
        }
        int i = 0, j = u.indexOf("{");
        if (j != -1) {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append(u, i, j).append("*");
                i = u.indexOf("}", j) + 1;
                j = u.indexOf("{", i);
            } while (j != -1);
            sb.append(u.substring(i));
            return sb.toString();
        }
        return u;
    };

}