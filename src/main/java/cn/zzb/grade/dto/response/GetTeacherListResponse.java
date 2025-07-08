package cn.zzb.grade.dto.response;

import cn.zzb.grade.entity.Teacher;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author zzb
 */
@Data
@Builder
public class GetTeacherListResponse {
    private List<Teacher> teacherList;
}
