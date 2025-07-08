package cn.zzb.grade.mapper;

import cn.zzb.grade.dto.request.UpdateCourseRequest;
import cn.zzb.grade.entity.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author zzb
 */
public interface CourseMapper extends BaseMapper<Course> {
    void updateByOldId(UpdateCourseRequest course);
}
