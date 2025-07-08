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
@TableName(value = "zhengzb_teacher")
public class Teacher {
    @TableId(value = "zzb_Tno")
    @ExcelProperty("教师编号")
    private String tno;

    @TableField("zzb_Tname")
    @ExcelProperty("姓名")
    private String name;

    @TableField("zzb_Tsex")
    @ExcelProperty("性别")
    private String sex;

    @TableField("zzb_Tage")
    @ExcelProperty("年龄")
    private String age;

    @TableField("zzb_Title")
    @ExcelProperty("职称")
    private String title;

    @TableField("zzb_Phone")
    @ExcelProperty("联系电话")
    private String phone;
}
