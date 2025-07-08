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
@TableName(value = "zhengzb_region")
public class Region {
    @TableId(value = "zzb_regionId")
    @ExcelProperty("地区ID")
    private String regionId;

    @TableField("zzb_regionName")
    @ExcelProperty("地区")
    private String regionName;
}
