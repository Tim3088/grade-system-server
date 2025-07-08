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
@TableName(value = "zhengzb_Course")
public class Course {
    @TableId(value = "zzb_Cno")
    @ExcelProperty("课程编号")
    private String cno;

    @TableField("zzb_Cname")
    @ExcelProperty("课程名称")
    private String name;

    @TableField("zzb_Hours")
    @ExcelProperty("学时")
    private String hours;

    @TableField("zzb_Type")
    @ExcelProperty("课程类型")
    private String type;

    @TableField("zzb_Credit")
    @ExcelProperty("学分")
    private String credit;
}
