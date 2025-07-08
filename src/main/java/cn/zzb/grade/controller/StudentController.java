package cn.zzb.grade.controller;

import cn.zzb.grade.constants.ExceptionEnum;
import cn.zzb.grade.entity.*;
import cn.zzb.grade.entity.Class;
import cn.zzb.grade.exceptions.ApiException;
import cn.zzb.grade.models.AjaxResult;
import cn.zzb.grade.service.StudentService;
import cn.zzb.grade.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zzb
 */
@Slf4j
@RequestMapping("/api/student")
@RestController
@Tag(name = "学生", description = "学生相关接口")
public class StudentController {
    @Resource
    private StudentService studentService;


    @PostMapping("/course")
    @Operation(summary = "选课")
    public AjaxResult<Void> selectCourse(@RequestHeader("Authorization") String token,
                                         @RequestParam("offerId") Integer offerId) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"student".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }

        studentService.selectCourse(user.getUserId(), offerId);
        return AjaxResult.success();
    }

    @DeleteMapping("/course")
    @Operation(summary = "退课")
    public AjaxResult<Void> dropCourse(@RequestHeader("Authorization") String token,
                                       @RequestParam("offerId") Integer offerId) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"student".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }

        studentService.dropCourse(user.getUserId(), offerId);
        return AjaxResult.success();
    }


    @GetMapping("/courses")
    @Operation(summary = "获取当前学生选课信息")
    public AjaxResult<List<SelectedCourseView>> getSelectedCourses(@RequestHeader("Authorization") String token) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"student".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        List<SelectedCourseView> selectedCourses = studentService.getSelectedCourses(user.getUserId());

        return AjaxResult.success(selectedCourses);
    }

    @GetMapping("/course")
    @Operation(summary = "获取当前学生可选课程列表")
    public AjaxResult<List<AvailableCourseView>> getAvailableCourses(@RequestHeader("Authorization") String token) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"student".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }

        List<AvailableCourseView> availableCourses = studentService.getAvailableCourses(user.getUserId());
        if (availableCourses == null || availableCourses.isEmpty()) {
            return AjaxResult.success("没有可选课程", null);
        }
        return AjaxResult.success(availableCourses);
    }

    @GetMapping("/grade")
    @Operation(summary = "获取当前学生成绩信息")
    public AjaxResult<List<StudentGradeView>> getStudentGrades(@RequestHeader("Authorization") String token) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"student".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        List<StudentGradeView> grades = studentService.getStudentGrades(user.getUserId());
        return AjaxResult.success(grades);
    }

    @GetMapping("/class")
    @Operation(summary = "获取当前学生班级信息")
    public AjaxResult<String> getStudentClass(@RequestHeader("Authorization") String token) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"student".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }

        String studentClass = studentService.getStudentClass(user.getUserId());
        if (studentClass == null) {
            return AjaxResult.success("当前学生没有班级信息", null);
        }

        return AjaxResult.success(studentClass);
    }
}
