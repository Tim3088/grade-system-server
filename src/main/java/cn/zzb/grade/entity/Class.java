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
@TableName(value = "zhengzb_class")
public class Class {
    @TableId(value = "zzb_ClassID")
    @ExcelProperty("班级ID")
    private String classId;

    @TableField("zzb_ClassName")
    @ExcelProperty("班级")
    private String className;

    @TableField("zzb_MajorID")
    @ExcelProperty("专业ID")
    private String majorId;
}
