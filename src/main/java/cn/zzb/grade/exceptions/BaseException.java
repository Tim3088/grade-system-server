package cn.zzb.grade.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zzb
 */
@Setter
@Getter
public class BaseException extends RuntimeException {
    private Integer errorCode;
    private String errorMsg;

    private BaseException() {
    }

    public BaseException(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        if (StringUtils.isNotBlank(errorMsg)) {
            return errorMsg;
        }
        return getMessage();
    }
}
