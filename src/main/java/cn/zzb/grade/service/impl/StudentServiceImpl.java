package cn.zzb.grade.service.impl;

import cn.zzb.grade.dto.request.UpdateStudentRequest;
import cn.zzb.grade.dto.request.UpdateUserRequest;
import cn.zzb.grade.dto.response.StudentDetail;
import cn.zzb.grade.entity.*;
import cn.zzb.grade.entity.Class;
import cn.zzb.grade.exceptions.ApiException;
import cn.zzb.grade.mapper.*;
import cn.zzb.grade.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.zzb.grade.constants.ExceptionEnum.*;

/**
 * @author zzb
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentMapper studentMapper;
    private final ClassMapper classMapper;
    private final RegionMapper regionMapper;
    private final UserMapper userMapper;
    private final CourseOfferMapper courseOfferMapper;
    private final GradeMapper gradeMapper;

    @Override
    public List<String> importStudents(List<Student> list) {
        if (list == null || list.isEmpty()) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 对 list 去重
        List<Student> uniqueList = deduplicateStudents(list);
        try {
            // 获取所有学生的 学号
            List<Student> existingStudents = studentMapper.selectList(null);
            // 去掉uniqueList中已存在的学号
            Map<String, Student> existingStudentMap = existingStudents.stream()
                    .collect(Collectors.toMap(Student::getSno, student -> student));
            uniqueList = uniqueList.stream()
                    .filter(student -> !existingStudentMap.containsKey(student.getSno()))
                    .toList();
            for (Student s : uniqueList) {
                studentMapper.insert(s);
            }
        } catch (Exception e) {
            log.error("导入学生信息失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
        // 返回导入的学号列表
        return uniqueList.stream()
                .map(Student::getSno)
                .collect(Collectors.toList());
    }

    @Override
    public void addStudent(Student student) {
        if (student == null || student.getSno() == null) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 检查学生是否已存在
        Student existingStudent = studentMapper.selectById(student.getSno());
        if (existingStudent != null) {
            throw new ApiException(STUDENT_ALREADY_EXISTS);
        }
        // 检查用户是否已存在
        User existingUser = userMapper.selectById(student.getSno());
        if (existingUser == null) {
            throw new ApiException(USER_NOT_FOUND);
        } else if(!Objects.equals(existingUser.getRole(), "student")) {
            throw new ApiException(USER_ISNOT_STUDENT);
        }
        try {
            // 插入新学生
            studentMapper.insertStudent(student.getSno(),
                    student.getName(),
                    student.getSex(),
                    student.getAge(),
                    student.getRegionId(),
                    student.getClassId());
        } catch (Exception e) {
            log.error("添加学生失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    @Override
    public void deleteStudent(String sno) {
        if (sno == null || sno.isEmpty()) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 检查学生是否存在
        Student existingStudent = studentMapper.selectById(sno);
        if (existingStudent == null) {
            throw new ApiException(USER_NOT_FOUND);
        }
        try {
            // 删除学生
            studentMapper.deleteById(sno);
        } catch (Exception e) {
            log.error("删除学生失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    @Override
    public void updateStudent(UpdateStudentRequest student) {
        if (student == null) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 检查学生是否存在
        Student existingStudent = studentMapper.selectById(student.getOldSno());
        if (existingStudent == null) {
            throw new ApiException(USER_NOT_FOUND);
        }
        if (student.getNewSno() != null) {
            User newUser = userMapper.selectById(student.getNewSno());
            if (newUser != null && !newUser.getUserId().equals(existingStudent.getSno())) {
                throw new ApiException(USER_ALREADY_EXISTS);
            }
            existingStudent.setSno(student.getNewSno());
            // 不存在重复用户，更新用户
            userMapper.updateByOldId(UpdateUserRequest.builder()
                            .oldUserId(student.getOldSno())
                            .newUserId(student.getNewSno())
                            .userName(student.getName())
                            .build());
        }
        try {
            if (student.getName() != null) {
                existingStudent.setName(student.getName());
            }
            existingStudent.setSex(student.getSex());
            existingStudent.setAge(student.getAge());
            existingStudent.setRegionId(student.getRegionId());
            existingStudent.setClassId(student.getClassId());

            // 更新学生信息 只更新学号和姓名
            studentMapper.updateByOldId(UpdateUserRequest.builder()
                            .oldUserId(student.getOldSno())
                            .newUserId(student.getNewSno())
                            .userName(student.getName())
                            .build());

            // 更新学生信息
            studentMapper.updateStudent(existingStudent.getSno(),
                    existingStudent.getName(),
                    existingStudent.getSex(),
                    existingStudent.getAge(),
                    existingStudent.getRegionId(),
                    existingStudent.getClassId());

        }
        catch (Exception e) {
            log.error("更新学生信息失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    @Override
    public List<AvailableCourseView> getAvailableCourses(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null || !"student".equals(user.getRole())) {
            throw new ApiException(USER_NOT_FOUND);
        }
        try {
            // 获取学生班级
            Student student = studentMapper.selectById(userId);
            if (student == null) {
                throw new ApiException(USER_NOT_FOUND);
            }

            // 获取所有可选课程
            List<AvailableCourseView> courseOffers = courseOfferMapper.selectAvailableCourses();
            if (courseOffers == null || courseOffers.isEmpty()) {
                return new ArrayList<>();
            }
            // 过滤出当前学生所在班级的课程
            courseOffers = courseOffers.stream()
                    .filter(course -> course.getClassId().equals(student.getClassId()))
                    .collect(Collectors.toList());
            return courseOffers;
        } catch (Exception e) {
            log.error("获取可选课程失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    @Override
    public void selectCourse(String userId, Integer offerId) {
        if (userId == null || userId.isEmpty() || offerId == null) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null || !"student".equals(user.getRole())) {
            throw new ApiException(USER_NOT_FOUND);
        }
        // 检查课程是否存在
        CourseOffer courseOffer = courseOfferMapper.selectById(offerId);
        if (courseOffer == null) {
            throw new ApiException(COURSE_NOT_FOUND);
        }
        // 检查学生是否已选该课程
        Grade grade = Grade.builder()
                .sno(userId)
                .offerId(offerId)
                .score(null)
                .build();
        if (gradeMapper.selectByMultiId(grade) != null) {
            throw new ApiException(COURSE_ALREADY_SELECTED);
        }
        try {
            // 插入选课记录，分数默认为null
            gradeMapper.insert(grade);
        } catch (Exception e) {
            log.error("选课失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    @Override
    public List<SelectedCourseView> getSelectedCourses(String userId) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null || !"student".equals(user.getRole())) {
            throw new ApiException(USER_NOT_FOUND);
        }
        try {
            // 获取所有学生已选课程
            List<SelectedCourseView> grades = gradeMapper.selectSelectedCourses();
            if (grades == null || grades.isEmpty()) {
                return new ArrayList<>();
            }
            // 过滤出当前学生的已选课程
            grades = grades.stream()
                    .filter(grade -> grade.getSno().equals(userId))
                    .toList();
            return grades;
        } catch (Exception e) {
            log.error("获取已选课程失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    @Override
    public void dropCourse(String userId, Integer offerId) {
        if (userId == null || userId.isEmpty() || offerId == null) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null || !"student".equals(user.getRole())) {
            throw new ApiException(USER_NOT_FOUND);
        }
        // 检查课程是否存在
        CourseOffer courseOffer = courseOfferMapper.selectById(offerId);
        if (courseOffer == null) {
            throw new ApiException(COURSE_NOT_FOUND);
        }
        // 检查学生是否已选该课程
        Grade grade = Grade.builder()
                .sno(userId)
                .offerId(offerId)
                .score(null)
                .build();
        if (gradeMapper.selectByMultiId(grade) == null) {
            throw new ApiException(COURSE_NOT_SELECTED);
        }
        try {
            // 删除选课记录
            gradeMapper.deleteByMultiId(grade);
        } catch (Exception e) {
            log.error("退课失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    @Override
    public List<StudentGradeView> getStudentGrades(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null || !"student".equals(user.getRole())) {
            throw new ApiException(USER_NOT_FOUND);
        }
        try {
            // 获取学生成绩
            List<StudentGradeView> grades = gradeMapper.selectStudentGradeView();
            if (grades == null || grades.isEmpty()) {
                return new ArrayList<>();
            }
            // 过滤出当前学生的成绩
            grades = grades.stream()
                    .filter(grade -> grade.getSno().equals(userId))
                    .collect(Collectors.toList());
            return grades;
        } catch (Exception e) {
            log.error("获取学生成绩失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    @Override
    public String getStudentClass(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null || !"student".equals(user.getRole())) {
            throw new ApiException(USER_NOT_FOUND);
        }
        // 获取学生信息
        Student student = studentMapper.selectById(userId);
        if (student == null) {
            throw new ApiException(USER_NOT_FOUND);
        }
        // 获取班级信息
        Class studentClass = classMapper.selectById(student.getClassId());
        if (studentClass == null) {
            throw new ApiException(CLASS_NOT_FOUND);
        }
        return studentClass.getClassName();
    }

    @Override
    public List<StudentDetail> getStudentList() {
        try {
            List<Student> studentList = studentMapper.selectList(null);
            if (studentList == null || studentList.isEmpty()) {
                return new ArrayList<>();
            }
            // 转化为StudentDetail
            return studentList.stream()
                    .map(student -> StudentDetail.builder()
                            .sno(student.getSno())
                            .name(student.getName())
                            .sex(student.getSex())
                            .age(student.getAge())
                            .region(regionMapper.selectById(student.getRegionId()).getRegionName())
                            .creditTotal(student.getCreditTotal())
                            .className(classMapper.selectById(student.getClassId()).getClassName())
                            .build()).toList();
        } catch (Exception e) {
            log.error("获取学生列表失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    private List<Student> deduplicateStudents(List<Student> list) {
        return list.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Student::getSno, student -> student, (existing, replacement) -> existing),
                        students -> new ArrayList<>(students.values())
                ));
    }
}
