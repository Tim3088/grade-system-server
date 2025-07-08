package cn.zzb.grade.dto.response;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author zzb
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDetail {
    @ExcelProperty("学号")
    private String sno;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("性别")
    private String sex;

    @ExcelProperty("年龄")
    private Integer age;

    @ExcelProperty("地区")
    private String region;

    @ExcelProperty("总学分")
    private String creditTotal;

    @ExcelProperty("班级")
    private String className;
}
