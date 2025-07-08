package cn.zzb.grade.mapper;

import cn.zzb.grade.entity.AvailableCourseView;
import cn.zzb.grade.entity.CourseOffer;
import cn.zzb.grade.entity.TeacherCourseView;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author zzb
 */
public interface CourseOfferMapper extends BaseMapper<CourseOffer> {
    List<CourseOffer> selectListByTeacherId(String userId);

    List<TeacherCourseView> selectTeacherCourseView();

    List<AvailableCourseView> selectAvailableCourses();
}
