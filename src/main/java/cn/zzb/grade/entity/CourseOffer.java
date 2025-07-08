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
@TableName(value = "zhengzb_CourseOffer")
public class CourseOffer {
    @TableId(value = "zzb_OfferID")
    @ExcelProperty("开设ID")
    private Integer offerId;

    @TableField("zzb_Cno")
    @ExcelProperty("课程ID")
    private String cno;

    @TableField("zzb_Tno")
    @ExcelProperty("教师ID")
    private String tno;

    @TableField("zzb_ClassID")
    @ExcelProperty("班级ID")
    private String classId;

    @TableField("zzb_Term")
    @ExcelProperty("学期")
    private String term;
}
