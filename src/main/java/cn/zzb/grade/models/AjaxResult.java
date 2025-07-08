package cn.zzb.grade.models;

import cn.zzb.grade.constants.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author zzb
 */
@Data
@AllArgsConstructor
public class AjaxResult<T> {
    public static final String SUCCESS_MSG = "success";
    private Integer code;
    private String msg;
    private T data;

    public static <N> AjaxResult<N> success() {
        return new AjaxResult<>(HttpStatus.OK.value(), SUCCESS_MSG, null);
    }

    public static <N> AjaxResult<N> success(N data) {
        return new AjaxResult<>(HttpStatus.OK.value(), SUCCESS_MSG, data);
    }

    public static <N> AjaxResult<N> success(String msg, N data) {
        return new AjaxResult<>(HttpStatus.OK.value(), msg, data);
    }

    public static <N> AjaxResult<N> fail(Integer code, String msg) {
        return new AjaxResult<>(code, msg, null);
    }

    public static <N> AjaxResult<N> fail(ExceptionEnum error) {
        return new AjaxResult<>(error.getErrorCode(), error.getErrorMsg(), null);
    }

    public static <N> AjaxResult<N> fail() {
        ExceptionEnum exception = ExceptionEnum.UNKNOWN_ERROR;
        return new AjaxResult<>(exception.getErrorCode(), exception.getErrorMsg(), null);
    }
}
