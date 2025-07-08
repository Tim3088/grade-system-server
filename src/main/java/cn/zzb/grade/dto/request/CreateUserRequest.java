package cn.zzb.grade.dto.request;

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
public class CreateUserRequest {
    private String userId;

    private String password;

    private String userName;

    private String role;
}
