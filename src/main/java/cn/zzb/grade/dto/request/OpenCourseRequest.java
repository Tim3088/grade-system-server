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
public class OpenCourseRequest {
    @NotBlank(message = "开课号不能为空")
    private Integer offerId;

    @NotBlank(message = "课程号不能为空")
    private String cno;

    @NotBlank(message = "教师号不能为空")
    private String tno;

    @NotBlank(message = "班级号不能为空")
    private String classId;

    @NotBlank(message = "学期不能为空")
    private String term;
}
