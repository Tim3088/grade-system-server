package cn.zzb.grade.service;

import cn.zzb.grade.dto.request.OpenCourseRequest;
import cn.zzb.grade.dto.request.UpdateCourseRequest;
import cn.zzb.grade.entity.Course;
import cn.zzb.grade.entity.CourseOffer;

import java.util.List;

/**
 * @author zzb
 */
public interface CourseService {
    List<String> importCourses(List<Course> list);

    List<Course> getCourseList();

    void updateCourse(UpdateCourseRequest request);

    void deleteCourse(String cno);

    void addCourse(Course course);

    void openCourse(OpenCourseRequest courseOffer);
}
