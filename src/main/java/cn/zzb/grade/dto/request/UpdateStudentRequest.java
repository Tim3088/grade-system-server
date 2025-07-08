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
public class UpdateStudentRequest {
    private String oldSno;

    @NotBlank(message = "新学号不能为空")
    private String newSno;

    private String name;

    private String sex;

    private Integer age;

    private Integer regionId;

    private Integer classId;
}
