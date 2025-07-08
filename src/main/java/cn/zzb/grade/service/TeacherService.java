package cn.zzb.grade.service;

import cn.zzb.grade.dto.request.UpdateTeacherRequest;
import cn.zzb.grade.entity.Teacher;

import java.util.List;

/**
 * @author zzb
 */
public interface TeacherService {
    List<Teacher> getTeacherList();

    void addTeacher(Teacher teacher);

    void deleteTeacher(String tno);

    void updateTeacher(UpdateTeacherRequest request);

    List<String> importTeachers(List<Teacher> list);

    void updateTeacherPhone(String userId, String phone);

    String getTeacherPhone(String userId);
}
