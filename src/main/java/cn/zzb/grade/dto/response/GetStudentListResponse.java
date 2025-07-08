package cn.zzb.grade.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author zzb
 */
@Data
@Builder
public class GetStudentListResponse {
    List<StudentDetail> studentList;
}
