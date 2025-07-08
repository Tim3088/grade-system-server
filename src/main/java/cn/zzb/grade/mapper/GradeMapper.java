package cn.zzb.grade.mapper;

import cn.zzb.grade.entity.Grade;
import cn.zzb.grade.entity.SelectedCourseView;
import cn.zzb.grade.entity.StudentGradeView;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;

import java.util.List;

/**
 * @author zzb
 */
public interface GradeMapper extends MppBaseMapper<Grade> {
    List<StudentGradeView> selectStudentGradeView();

    List<SelectedCourseView> selectSelectedCourses();

}
