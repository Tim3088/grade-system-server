package cn.zzb.grade.service;

import cn.zzb.grade.dto.request.UpdateStudentRequest;
import cn.zzb.grade.dto.response.StudentDetail;
import cn.zzb.grade.entity.*;

import java.util.List;

/**
 * @author zzb
 */
public interface StudentService {
    List<StudentDetail> getStudentList();

    List<String> importStudents(List<Student> list);

    void addStudent(Student student);

    void deleteStudent(String sno);

    void updateStudent(UpdateStudentRequest request);

    List<AvailableCourseView> getAvailableCourses(String userId);

    void selectCourse(String userId, Integer offerId);

    List<SelectedCourseView> getSelectedCourses(String userId);

    void dropCourse(String userId, Integer offerId);

    List<StudentGradeView> getStudentGrades(String userId);

    String getStudentClass(String userId);

}
