package cn.zzb.grade.mapper;

import cn.zzb.grade.dto.request.UpdateUserRequest;
import cn.zzb.grade.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author zzb
 */
public interface UserMapper extends BaseMapper<User> {
    void updateByOldId(UpdateUserRequest user);
}
