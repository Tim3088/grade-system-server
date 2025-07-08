package cn.zzb.grade.handler;

import cn.zzb.grade.constants.ExceptionEnum;
import cn.zzb.grade.models.AjaxResult;
import cn.zzb.grade.utils.HandlerUtils;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 处理参数校验相关异常
 *
 * @author zzb
 */
@Order(10)
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ValidateExceptionHandler {
    /**
     * 参数校验错误拦截处理
     *
     * @param e 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public AjaxResult<Object> validationBodyException(MethodArgumentNotValidException e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        return AjaxResult.fail(ExceptionEnum.INVALID_PARAMETER);
    }

    /**
     * SQL执行失败错误（重复主键）拦截处理
     *
     * @param e 错误信息集合
     * @return 错误信息
     */

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseBody
    public AjaxResult<Object> validationBodyException(SQLIntegrityConstraintViolationException e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        return AjaxResult.fail(ExceptionEnum.DATABASE_ERROR);
    }

    /**
     * Json解析失败错误（字段填写错误或漏填必选值）拦截处理
     *
     * @param e 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(JsonMappingException.class)
    @ResponseBody
    public AjaxResult<Object> validationBodyException(JsonMappingException e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        return AjaxResult.fail(ExceptionEnum.INVALID_PARAMETER);
    }

    /**
     * Json格式错误拦截处理
     *
     * @param e 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public AjaxResult<Object> validationBodyException(HttpMessageNotReadableException e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        return AjaxResult.fail(ExceptionEnum.INVALID_PARAMETER);
    }
}
