package cn.zzb.grade.handler;

import cn.zzb.grade.constants.ExceptionEnum;
import cn.zzb.grade.exceptions.ApiException;
import cn.zzb.grade.exceptions.BaseException;
import cn.zzb.grade.models.AjaxResult;
import cn.zzb.grade.utils.HandlerUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * @author zzb
 */
@ControllerAdvice
@Slf4j
@Order(80)
@RequiredArgsConstructor
public class CustomExceptionHandler {
    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public AjaxResult<Object> handleAppException(BaseException e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        if (e instanceof ApiException) {
            return AjaxResult.fail(e.getErrorCode(), e.getErrorMsg());
        }
        return AjaxResult.fail();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseBody
    public AjaxResult<Object> handleNotFoundException(NoResourceFoundException e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        return AjaxResult.fail(ExceptionEnum.NOT_FOUND_ERROR);
    }
}
