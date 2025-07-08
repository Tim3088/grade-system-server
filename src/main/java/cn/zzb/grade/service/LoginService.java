package cn.zzb.grade.service;

import cn.zzb.grade.dto.request.LoginRequest;
import cn.zzb.grade.dto.response.LoginResponse;
import jakarta.validation.Valid;

/**
 * @author zzb
 */
public interface LoginService {
    LoginResponse login(@Valid LoginRequest request);

}
