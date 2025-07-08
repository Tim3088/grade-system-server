package cn.zzb.grade.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author zzb
 */
@Data
public class AvailableCourseView {
    @TableField("zzb_OfferID")
    private Integer offerId;

    @TableField("zzb_ClassID")
    private Integer classId;

    @TableField("zzb_Cno")
    private String cno;

    @TableField("zzb_Cname")
    private String cname;

    @TableField("zzb_Hours")
    private Integer hours;

    @TableField("zzb_Type")
    private String type;

    @TableField("zzb_Credit")
    private Integer credit;

    @TableField("zzb_tname")
    private String tname;

    @TableField("zzb_term")
    private String term;
}
