package cn.zzb.grade.controller;

import cn.zzb.grade.constants.ExceptionEnum;
import cn.zzb.grade.dto.request.CreateUserRequest;
import cn.zzb.grade.dto.request.LoginRequest;
import cn.zzb.grade.dto.request.UpdatePasswordRequest;
import cn.zzb.grade.dto.request.UpdateUserRequest;
import cn.zzb.grade.dto.response.LoginResponse;
import cn.zzb.grade.entity.User;
import cn.zzb.grade.exceptions.ApiException;
import cn.zzb.grade.mapper.UserMapper;
import cn.zzb.grade.models.AjaxResult;
import cn.zzb.grade.service.LoginService;
import cn.zzb.grade.service.UserService;
import cn.zzb.grade.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * @author zzb
 */
@Slf4j
@RequestMapping("/api/user")
@RestController
@Tag(name = "用户", description = "用户相关接口")
public class UserController {
    @Resource
    private LoginService loginService;
    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public AjaxResult<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = loginService.login(request);
        return AjaxResult.success(response);
    }

    @PutMapping("/profile")
    @Operation(summary = "修改密码")
    public AjaxResult<Void> updatePassword(@RequestHeader("Authorization") String token,
                                           @RequestBody @Valid UpdatePasswordRequest request) {
        User currentUser = JwtUtil.getUserFromToken(token);

        try {
            User user = userMapper.selectById(currentUser.getUserId());
            userService.updateUser(UpdateUserRequest.builder()
                            .oldUserId(user.getUserId())
                            .newUserId(user.getUserId())
                            .password(request.getNewPassword())
                            .userName(user.getUserName())
                            .build());
            return AjaxResult.success();
        } catch (ApiException e) {
            log.error("修改密码失败: {}", e.getMessage());
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    @GetMapping("/name")
    @Operation(summary = "获取当前用户姓名")
    public AjaxResult<String> getCurrentUserName(@RequestHeader("Authorization") String token) {
        User currentUser = JwtUtil.getUserFromToken(token);
        User user = userMapper.selectById(currentUser.getUserId());
        if (user.getUserName() == null) {
            throw new ApiException(ExceptionEnum.USER_NOT_FOUND);
        }
        return AjaxResult.success(user.getUserName());
    }

    @PostMapping("/new")
    @Operation(summary = "创建新用户")
    public AjaxResult<Void> createUser(@RequestBody @Valid CreateUserRequest request) {
        userService.createUser(request);
        return AjaxResult.success();
    }
}
