package cn.zzb.grade.mapper;

import cn.zzb.grade.dto.request.UpdateUserRequest;
import cn.zzb.grade.entity.Teacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author zzb
 */
public interface TeacherMapper extends BaseMapper<Teacher> {
    void updateByOldId(UpdateUserRequest build);
}
