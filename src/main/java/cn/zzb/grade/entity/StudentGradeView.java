package cn.zzb.grade.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author zzb
 */
@Data
public class StudentGradeView {
    @TableField("zzb_Sno")
    private String sno;

    @TableField("zzb_Sname")
    private String sname;

    @TableField("zzb_Cname")
    private String cname;

    @TableField("zzb_Term")
    private String term;

    @TableField("zzb_Score")
    private Double score;

    @TableField("zzb_Tname")
    private String tname;

    @TableField("zzb_ClassName")
    private String className;

    @TableField("zzb_Credit")
    private Integer credit;
}
