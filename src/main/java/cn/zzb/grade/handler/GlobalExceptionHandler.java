package cn.zzb.grade.handler;

import cn.zzb.grade.models.AjaxResult;
import cn.zzb.grade.utils.HandlerUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 未被其余handler处理，则最终进入该handler处理，处理Exception子类
 *
 * @author zzb
 */
@ControllerAdvice
@Slf4j
@Order(1000)
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public AjaxResult<Object> handleGlobalException(Exception e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        return AjaxResult.fail();
    }
}
