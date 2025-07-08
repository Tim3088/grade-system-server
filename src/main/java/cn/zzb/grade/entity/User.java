package cn.zzb.grade.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author zzb
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value = "zhengzb_user")
public class User {
    @TableId(value = "zzb_userid")
    @ExcelProperty("用户ID")
    private String userId;

    @TableField("zzb_username")
    @ExcelProperty("姓名")
    private String userName;

    @TableField("zzb_password")
    @ExcelProperty("密码")
    private String password;

    @TableField("zzb_role")
    @ExcelProperty("角色")
    private String role;
}
