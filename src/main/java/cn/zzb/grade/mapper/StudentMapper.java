package cn.zzb.grade.mapper;

import cn.zzb.grade.dto.request.UpdateUserRequest;
import cn.zzb.grade.entity.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author zzb
 */
public interface StudentMapper extends BaseMapper<Student> {
    void updateByOldId(UpdateUserRequest user);

    void insertStudent(@Param("sno") String sno,
                       @Param("sname") String sname,
                       @Param("sex") String sex,
                       @Param("age") Integer age,
                       @Param("regionId") Integer regionId,
                       @Param("classId") Integer classId);

    void updateStudent(@Param("sno") String sno,
                       @Param("sname") String sname,
                       @Param("sex") String sex,
                       @Param("age") Integer age,
                       @Param("regionId") Integer regionId,
                       @Param("classId") Integer classId);
}
