package cn.zzb.grade.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * @author zzb
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value = "zhengzb_Grade")
public class Grade implements Serializable {
    @MppMultiId
    @TableField("zzb_sno")
    private String sno;

    @MppMultiId
    @TableField("zzb_offerid")
    private Integer offerId;

    @TableField("zzb_score")
    private Double score;
}
