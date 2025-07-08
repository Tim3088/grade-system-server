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
public class UpdateCourseRequest {
    private String oldCno;

    private String newCno;

    private String name;

    private String hours;

    private String type;

    private String credit;
}
