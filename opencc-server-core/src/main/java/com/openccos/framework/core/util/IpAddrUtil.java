package com.openccos.framework.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * 〈IP〉
 *
 * @author kevin
 * @since 1.0.0
 */
public class IpAddrUtil {

    private IpAddrUtil() {
    }

    public static boolean isIpAddress(String ipAddr) {
        if (StringUtils.isEmpty(ipAddr)) {
            return false;
        } else {
            String regTxt =
                    "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
            return regMatch(ipAddr, regTxt);
        }
    }

    private static boolean regMatch(String source, String regTxt) {
        Pattern pattern = Pattern.compile(regTxt);
        Matcher matcher = pattern.matcher(source);
        return matcher.matches();
    }

    public static long ip2Long(String ipAddr) {
        long[] ip = new long[4];
        int position1 = ipAddr.indexOf(".");
        int position2 = ipAddr.indexOf(".", position1 + 1);
        int position3 = ipAddr.indexOf(".", position2 + 1);
        ip[0] = Long.parseLong(ipAddr.substring(0, position1));
        ip[1] = Long.parseLong(ipAddr.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(ipAddr.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(ipAddr.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    public static String long2IP(long ipAddr) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(ipAddr >>> 24));
        sb.append(".");
        sb.append(String.valueOf((ipAddr & 16777215L) >>> 16));
        sb.append(".");
        sb.append(String.valueOf((ipAddr & 65535L) >>> 8));
        sb.append(".");
        sb.append(String.valueOf(ipAddr & 255L));
        return sb.toString();
    }

    public static long getLongIp(HttpServletRequest request) {
        try {
            String ipAddr = getIpAddr(request);
            return StringUtils.isBlank(ipAddr) ? 0L : ip2Long(ipAddr);
        } catch (Exception var2) {
            return 0L;
        }
    }

    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "";
        } else {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }

            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }

            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }

            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }

            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }

            return ip;
        }
    }
}