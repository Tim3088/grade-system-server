package cn.zzb.grade.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 统一错误码枚举
 *
 * @author zzb
 */
@Getter
public enum ExceptionEnum {
    INVALID_PARAMETER(200000, "参数错误"),
    DATABASE_ERROR(200001, "数据库异常"),
    JSON_PARSE_ERROR(200002, "json解析失败"),
    RESOURCE_NOT_FOUND(200003, "资源不存在"),
    NOT_LOGIN(200004, "当前未登录或登录过期, 请重新登录"),
    WRONG_USERNAME_OR_PASSWORD(200005, "用户名或密码错误"),
    NOT_FOUND_ERROR(200404, HttpStatus.NOT_FOUND.getReasonPhrase()),
    UNKNOWN_ERROR(200500, "未知错误, 请稍后重试"),
    JWT_ERROR(200401, "JWT验证失败"),
    ROLE_ERROR(200403, "权限不足, 无法访问该资源"),
    USER_NOT_FOUND(200404, "用户不存在"),
    USER_ALREADY_EXISTS(200405, "用户已存在"),
    STUDENT_ALREADY_EXISTS(200406, "学生已存在"),
    USER_ISNOT_STUDENT(200407, "用户不是学生身份"),
    TEACHER_ALREADY_EXISTS(200408, "教师已存在"),
    USER_ISNOT_TEACHER(200409, "用户不是教师身份"),
    COURSE_NOT_FOUND(200410, "课程不存在"),
    COURSE_ALREADY_EXISTS(200411, "课程已存在"),
    TEACHER_NOT_FOUND(200412, "教师不存在"),
    CLASS_NOT_FOUND(200413, "班级不存在"),
    COURSE_ALREADY_OPENED(200414, "课程已开设"),
    GRADE_ALREADY_EXISTS(200415, "成绩已存在"),
    GRADE_NOT_FOUND(200416, "成绩不存在"),
    TEACHER_PHONE_NOT_SET(200417, "教师手机号未设置"),
    NO_AVAILABLE_COURSES(200418, "当前没有可选课程"),
    COURSE_ALREADY_SELECTED(200419, "课程已被选中"),
    COURSE_NOT_SELECTED(200420, "课程未被选中"),
    ;

    private final Integer errorCode;
    private final String errorMsg;

    ExceptionEnum(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
