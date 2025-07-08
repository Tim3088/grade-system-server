package cn.zzb.grade.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String userId;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;
}
