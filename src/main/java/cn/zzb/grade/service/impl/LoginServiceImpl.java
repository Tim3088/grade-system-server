package cn.zzb.grade.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import cn.zzb.grade.constants.ExceptionEnum;
import cn.zzb.grade.dto.request.LoginRequest;
import cn.zzb.grade.dto.response.LoginResponse;
import cn.zzb.grade.entity.User;
import cn.zzb.grade.exceptions.ApiException;
import cn.zzb.grade.mapper.UserMapper;
import cn.zzb.grade.service.LoginService;
import cn.zzb.grade.utils.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author zzb
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserMapper userMapper;

    @Override
    public LoginResponse login(LoginRequest request) throws ApiException {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, request.getUserId()));
        if (Objects.isNull(user)) {
            throw new ApiException(ExceptionEnum.USER_NOT_FOUND);
        }
        if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            return LoginResponse.builder()
                    .userType(user.getRole())
                    .token(JwtUtil.enCodeToken(user))
                    .build();
        }
        throw new ApiException(ExceptionEnum.WRONG_USERNAME_OR_PASSWORD);
    }
}
