package cn.zzb.grade.service;

import cn.zzb.grade.entity.CourseOffer;
import cn.zzb.grade.entity.TeacherCourseView;

import java.util.List;

/**
 * @author zzb
 */
public interface CourseOfferService {
    List<TeacherCourseView> getCourseListByTeacher(String userId);

}
