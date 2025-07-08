package cn.zzb.grade.dto.response;

import cn.zzb.grade.entity.Course;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author zzb
 */
@Data
@Builder
public class GetCourseListResponse {
    private List<Course> courseList;
}
