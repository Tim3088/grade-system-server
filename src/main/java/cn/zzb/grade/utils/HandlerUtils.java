package cn.zzb.grade.utils;

import jakarta.servlet.http.HttpServletRequest;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

/**
 * @author zzb
 */
@Slf4j
public class HandlerUtils {
    /**
     * 错误日志
     *
     * @param e       错误异常
     * @param request Http请求对象
     */
    public static void logException(Exception e, HttpServletRequest request) {
        log.error("[{}] | {} | {} | request={}", Instant.now(), getRemoteAddr(request), request.getRequestURI(), JSON.toJSONString(request.getParameterMap()), e);
    }

    private static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        return ip == null ? request.getRemoteAddr() : ip;
    }
}
