package com.vienna.jaray.utils;

import com.vienna.jaray.common.Separator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jaray
 * @date 2020年09月12日 13:58
 * @description: IP相关工具类
 */
@Slf4j
public class IpUtils {
    private static final String UNKNOWN = "unknown";
    /**
     * 获取IP地址
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     *
     * @param request http请求对象
     * @return 请求ip
     */
	public static String getIpAddr(HttpServletRequest request) {
    	String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
        	log.error("IPUtils ERROR ", e);
        }
        
        //使用代理，则获取第一个IP地址
        int proxyIpLength = 15;
        if(StringUtils.isEmpty(ip) && ip.length() > proxyIpLength) {
			if(ip.indexOf(Separator.COMMA_SEPARATOR_EN.getSeparator()) > 0) {
				ip = ip.substring(0, ip.indexOf(","));
			}
		}
        
        return ip;
    }

}
