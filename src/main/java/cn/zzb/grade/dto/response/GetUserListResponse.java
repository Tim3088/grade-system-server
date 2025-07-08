package cn.zzb.grade.dto.response;

import cn.zzb.grade.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author zzb
 */
@Data
@Builder
public class GetUserListResponse {
    List <User> userList;
}
