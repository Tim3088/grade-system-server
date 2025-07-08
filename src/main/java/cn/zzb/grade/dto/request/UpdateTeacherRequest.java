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
public class UpdateTeacherRequest {
    private String oldTno;

    @NotBlank(message = "新教师编号不能为空")
    private String newTno;

    private String name;

    private String sex;

    private String age;

    private String title;

    private String phone;
}
