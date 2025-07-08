package cn.zzb.grade.exceptions;

import cn.zzb.grade.constants.ExceptionEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * @author zzb
 */
@Setter
@Getter
public class ApiException extends BaseException {
  private Integer statusCode;

  /**
   * 业务异常构造函数
   *
   * @param statusCode Http状态码
   * @param errorCode  错误码
   * @param errorMsg   错误信息
   */
  public ApiException(Integer statusCode, Integer errorCode, String errorMsg) {
    super(errorCode, errorMsg);
    this.statusCode = statusCode;
  }

  /**
   * 包装错误枚举为业务异常
   *
   * @param e 错误枚举
   */
  public ApiException(ExceptionEnum e) {
    super(e.getErrorCode(), e.getErrorMsg());
    this.statusCode = HttpStatus.OK.value();
  }

  public boolean isException(ExceptionEnum e) {
    return e.getErrorCode().equals(this.getErrorCode());
  }
}
