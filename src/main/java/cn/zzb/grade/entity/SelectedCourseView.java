package cn.zzb.grade.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author zzb
 */
@Data
public class SelectedCourseView {
    @TableField("zzb_OfferID")
    private Integer offerId;

    @TableField("zzb_Sno")
    private String sno;

    @TableField("zzb_Cno")
    private String cno;

    @TableField("zzb_Cname")
    private String cname;

    @TableField("zzb_Term")
    private String term;

    @TableField("zzb_Tname")
    private String tname;

    @TableField("zzb_Credit")
    private Integer credit;

    @TableField("zzb_score")
    private Double score;
}
