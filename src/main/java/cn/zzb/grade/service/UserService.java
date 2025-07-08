package cn.zzb.grade.service;

import cn.zzb.grade.dto.request.CreateUserRequest;
import cn.zzb.grade.dto.request.UpdateUserRequest;
import cn.zzb.grade.entity.Student;
import cn.zzb.grade.entity.User;
import jakarta.validation.Valid;

import java.util.List;

/**
 * @author zzb
 */
public interface UserService {
    List<String> importUsers(List<User> list);

    List<User> getUserList();

    void updateUser(UpdateUserRequest user);

    void deleteUser(String userId);

    void addUser(User user);

    void createUser(@Valid CreateUserRequest request);

}
