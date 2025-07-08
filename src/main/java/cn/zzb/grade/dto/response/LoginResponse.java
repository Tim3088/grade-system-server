package cn.zzb.grade.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @author zzb
 */
@Data
@Builder
public class LoginResponse {
    @Schema(description = "用户类型(student,admin)")
    private String userType;
    @Schema(description = "jwt令牌")
    private String token;
}
