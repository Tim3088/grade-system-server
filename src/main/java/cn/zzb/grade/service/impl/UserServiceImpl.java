package cn.zzb.grade.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import cn.zzb.grade.dto.request.CreateUserRequest;
import cn.zzb.grade.dto.request.UpdateUserRequest;
import cn.zzb.grade.entity.User;
import cn.zzb.grade.exceptions.ApiException;
import cn.zzb.grade.mapper.StudentMapper;
import cn.zzb.grade.mapper.UserMapper;
import cn.zzb.grade.service.StudentService;
import cn.zzb.grade.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.ArrayList;

import static cn.zzb.grade.constants.ExceptionEnum.*;

/**
 * @author zzb
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final StudentMapper studentMapper;
    private final StudentService studentService;

    @Override
    public List<String> importUsers(List<User> list) {
        if (list == null || list.isEmpty()) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 对 list 去重
        List<User> uniqueList = deduplicateUsers(list);
        try {
            // 获取所有用户的 userId
            List<User> existingUsers = userMapper.selectList(null);
            // 去掉uniqueList中已存在的用户
            Map<String, User> existingUserMap = existingUsers.stream()
                    .collect(Collectors.toMap(User::getUserId, user -> user));
            uniqueList = uniqueList.stream()
                    .filter(user -> !existingUserMap.containsKey(user.getUserId()))
                    .toList();
            for (User user : uniqueList) {
                user.setPassword(BCrypt.hashpw(user.getPassword()));
                userMapper.insert(user);
            }
        } catch (Exception e) {
            log.error("导入学生信息失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
        // 返回导入的用户ID列表
        return uniqueList.stream()
                .map(User::getUserId)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getUserList() {
        try {
            List<User> userList = userMapper.selectList(null);
            if (userList == null || userList.isEmpty()) {
                return new ArrayList<>();
            }
            // 去重
            return deduplicateUsers(userList);
        } catch (Exception e) {
            log.error("获取用户列表失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    @Override
    public void updateUser(UpdateUserRequest user) {
        if (user == null) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 检查用户是否存在
        User existingUser = userMapper.selectById(user.getOldUserId());
        if (existingUser == null) {
            throw new ApiException(USER_NOT_FOUND);
        }
        if (user.getNewUserId() != null) {
            User newUser = userMapper.selectById(user.getNewUserId());
            if (newUser != null && !newUser.getUserId().equals(existingUser.getUserId())) {
                throw new ApiException(USER_ALREADY_EXISTS);
            }
        }
        try {
            if (user.getUserName() != null) {
                existingUser.setUserName(user.getUserName());
            }
            if (user.getPassword() != null && !BCrypt.checkpw(user.getPassword(), existingUser.getPassword())) {
                existingUser.setPassword(BCrypt.hashpw(user.getPassword()));
            }
            if (Objects.equals(existingUser.getRole(), "student")) {
                studentMapper.updateByOldId(user);
            }
            // 更新用户信息(不包含密码)
            userMapper.updateByOldId(user);
            existingUser.setUserId(user.getNewUserId());
            // 更新密码
            userMapper.updateById(existingUser);
        }
        catch (Exception e) {
            log.error("更新用户信息失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    @Override
    public void deleteUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 检查用户是否存在
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            throw new ApiException(USER_NOT_FOUND);
        }
        if ("student".equals(existingUser.getRole())) {
            studentMapper.deleteById(userId);
        }
        try {
            // 删除用户
            userMapper.deleteById(userId);
        } catch (Exception e) {
            log.error("删除用户失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    @Override
    public void addUser(User user) {
        if (user == null || user.getUserId() == null || user.getPassword() == null) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 检查用户是否已存在
        User existingUser = userMapper.selectById(user.getUserId());
        if (existingUser != null) {
            throw new ApiException(USER_ALREADY_EXISTS);
        }
        try {
            // 加密密码
            user.setPassword(BCrypt.hashpw(user.getPassword()));
            // 插入新用户
            userMapper.insert(user);
        } catch (Exception e) {
            log.error("添加用户失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    @Override
    public void createUser(CreateUserRequest request) {
        // 检查用户是否已存在
        User existingUser = userMapper.selectById(request.getUserId());
        if (existingUser != null) {
            throw new ApiException(USER_ALREADY_EXISTS);
        }
        try {
            // 加密密码
            String hashedPassword = BCrypt.hashpw(request.getPassword());
            User user = User.builder()
                    .userId(request.getUserId())
                    .password(hashedPassword)
                    .userName(request.getUserName())
                    .role(request.getRole())
                    .build();
            userMapper.insert(user);
        } catch (Exception e) {
            log.error("创建用户失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    private List<User> deduplicateUsers(List<User> list) {
    return list.stream()
            .collect(Collectors.collectingAndThen(
                    Collectors.toMap(User::getUserId, user -> user, (existing, replacement) -> existing),
                    users -> new ArrayList<>(users.values())
            ));
    }

}
