package cn.zzb.grade.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author zzb
 */
@Data
public class TeacherCourseView {
    @TableField("zzb_offerid")
    private Integer offerId;

    @TableField("zzb_tno")
    private String tno;

    @TableField("zzb_tname")
    private String tname;

    @TableField("zzb_cno")
    private String cno;

    @TableField("zzb_cname")
    private String cname;

    @TableField("zzb_term")
    private String term;

    @TableField("zzb_classname")
    private String className;
}
