package cn.zzb.grade.service.impl;

import cn.zzb.grade.dto.request.UpdateTeacherRequest;
import cn.zzb.grade.dto.request.UpdateUserRequest;
import cn.zzb.grade.entity.Teacher;
import cn.zzb.grade.entity.User;
import cn.zzb.grade.exceptions.ApiException;
import cn.zzb.grade.mapper.TeacherMapper;
import cn.zzb.grade.mapper.UserMapper;
import cn.zzb.grade.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.zzb.grade.constants.ExceptionEnum;

/**
 * @author zzb
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private final TeacherMapper teacherMapper;
    private final UserMapper userMapper;


    @Override
    public List<Teacher> getTeacherList() {
        try {
            List<Teacher> teacherList = teacherMapper.selectList(null);
            if (teacherList == null || teacherList.isEmpty()) {
                return new ArrayList<>();
            }
            return teacherList;
        } catch (Exception e) {
            log.error("获取教师列表失败: {}", e.getMessage());
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    @Override
    public void addTeacher(Teacher teacher) {
        if (teacher == null || teacher.getTno() == null) {
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
        }
        // 检查教师是否已存在
        Teacher existingTeacher = teacherMapper.selectById(teacher.getTno());
        if (existingTeacher != null) {
            throw new ApiException(ExceptionEnum.TEACHER_ALREADY_EXISTS);
        }
        // 检查用户是否已存在
        User existingUser = userMapper.selectById(teacher.getTno());
        if (existingUser == null) {
            throw new ApiException(ExceptionEnum.USER_NOT_FOUND);
        } else if(!Objects.equals(existingUser.getRole(), "teacher")) {
            throw new ApiException(ExceptionEnum.USER_ISNOT_TEACHER);
        }
        try {
            // 插入新教师
            teacherMapper.insert(teacher);
        } catch (Exception e) {
            log.error("添加教师失败: {}", e.getMessage());
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    @Override
    public void deleteTeacher(String tno) {
        if (tno == null || tno.isEmpty()) {
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
        }
        // 检查教师是否存在
        Teacher existingTeacher = teacherMapper.selectById(tno);
        if (existingTeacher == null) {
            throw new ApiException(ExceptionEnum.USER_NOT_FOUND);
        }
        try {
            // 删除教师
            teacherMapper.deleteById(tno);
        } catch (Exception e) {
            log.error("删除教师失败: {}", e.getMessage());
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    @Override
    public void updateTeacher(UpdateTeacherRequest teacher) {
        if (teacher == null) {
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
        }
        // 检查教师是否存在
        Teacher existingTeacher = teacherMapper.selectById(teacher.getOldTno());
        if (existingTeacher == null) {
            throw new ApiException(ExceptionEnum.USER_NOT_FOUND);
        }
        if (teacher.getNewTno() != null) {
            User newUser = userMapper.selectById(teacher.getNewTno());
            if (newUser != null && !newUser.getUserId().equals(existingTeacher.getTno())) {
                throw new ApiException(ExceptionEnum.USER_ALREADY_EXISTS);
            }
            existingTeacher.setTno(teacher.getNewTno());
            // 不存在重复用户，更新用户
            userMapper.updateByOldId(UpdateUserRequest.builder()
                    .oldUserId(teacher.getOldTno())
                    .newUserId(teacher.getNewTno())
                    .userName(teacher.getName())
                    .build());
        }
        try {
            if (teacher.getName() != null) {
                existingTeacher.setName(teacher.getName());
            }
            existingTeacher.setSex(teacher.getSex());
            existingTeacher.setAge(teacher.getAge());
            existingTeacher.setTitle(teacher.getTitle());
            existingTeacher.setPhone(teacher.getPhone());

            // 更新教师信息 只更新编号和姓名
            teacherMapper.updateByOldId(UpdateUserRequest.builder()
                    .oldUserId(teacher.getOldTno())
                    .newUserId(teacher.getNewTno())
                    .userName(teacher.getName())
                    .build());

            // 更新教师信息
            teacherMapper.updateById(existingTeacher);

        }
        catch (Exception e) {
            log.error("更新教师信息失败: {}", e.getMessage());
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);

        }
    }

    @Override
    public List<String> importTeachers(List<Teacher> list) {
        if (list == null || list.isEmpty()) {
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
        }
        // 对 list 去重
        List<Teacher> uniqueList = deduplicateTeachers(list);
        try {
            // 获取所有教师的 编号
            List<Teacher> existingTeachers = teacherMapper.selectList(null);
            // 去掉uniqueList中已存在的编号
            Map<String, Teacher> existingTeacherMap = existingTeachers.stream()
                    .collect(Collectors.toMap(Teacher::getTno, t -> t));
            uniqueList = uniqueList.stream()
                    .filter(t -> !existingTeacherMap.containsKey(t.getTno()))
                    .toList();
            for (Teacher t : uniqueList) {
                teacherMapper.insert(t);
            }
        } catch (Exception e) {
            log.error("导入教师信息失败: {}", e.getMessage());
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
        // 返回导入的教师编号列表
        return uniqueList.stream()
                .map(Teacher::getTno)
                .collect(Collectors.toList());
    }

    @Override
    public void updateTeacherPhone(String userId, String phone) {
        if (userId == null || userId.isEmpty() || phone == null || phone.isEmpty()) {
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
        }
        // 检查教师是否存在
        Teacher existingTeacher = teacherMapper.selectById(userId);
        if (existingTeacher == null) {
            throw new ApiException(ExceptionEnum.TEACHER_NOT_FOUND);
        }
        try {
            existingTeacher.setPhone(phone);
            teacherMapper.updateById(existingTeacher);
        } catch (Exception e) {
            log.error("更新教师手机号失败: {}", e.getMessage());
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    @Override
    public String getTeacherPhone(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
        }
        // 检查教师是否存在
        Teacher existingTeacher = teacherMapper.selectById(userId);
        if (existingTeacher == null) {
            throw new ApiException(ExceptionEnum.TEACHER_NOT_FOUND);
        }
        String phone = existingTeacher.getPhone();
        if (phone == null || phone.isEmpty()) {
            throw new ApiException(ExceptionEnum.TEACHER_PHONE_NOT_SET);
        }
        return phone;
    }

    private List<Teacher> deduplicateTeachers(List<Teacher> list) {
        return list.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Teacher::getTno, t -> t, (existing, replacement) -> existing),
                        teachers -> new ArrayList<>(teachers.values())
                ));
    }
}
