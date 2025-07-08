package cn.zzb.grade.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName(value = "zhengzb_student")
public class Student {
    @TableId(value = "zzb_Sno")
    @ExcelProperty("学号")
    private String sno;

    @TableField("zzb_Sname")
    @ExcelProperty("姓名")
    private String name;

    @TableField("zzb_Sex")
    @ExcelProperty("性别")
    private String sex;

    @TableField("zzb_Age")
    @ExcelProperty("年龄")
    private Integer age;

    @TableField("zzb_RegionID")
    @ExcelProperty("地区ID")
    private Integer regionId;

    @TableField("zzb_CreditTotal")
    @ExcelProperty("总学分")
    private String creditTotal;

    @TableField("zzb_ClassID")
    @ExcelProperty("班级ID")
    private Integer classId;
}
