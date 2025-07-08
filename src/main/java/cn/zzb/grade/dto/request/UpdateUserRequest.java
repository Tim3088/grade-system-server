package cn.zzb.grade.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zzb
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateUserRequest {
    private String oldUserId;

    @NotBlank(message = "新用户ID不能为空或空白")
    private String newUserId;

    private String password;

    private String userName;
}
