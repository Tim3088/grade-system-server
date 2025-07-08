package cn.zzb.grade.controller;

import cn.zzb.grade.constants.ExceptionEnum;
import cn.zzb.grade.entity.*;
import cn.zzb.grade.exceptions.ApiException;
import cn.zzb.grade.mapper.GradeMapper;
import cn.zzb.grade.models.AjaxResult;
import cn.zzb.grade.service.CourseOfferService;
import cn.zzb.grade.service.CourseService;
import cn.zzb.grade.service.GradeService;
import cn.zzb.grade.service.TeacherService;
import cn.zzb.grade.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zzb
 */
@Slf4j
@RequestMapping("/api/teacher")
@RestController
@Tag(name = "教师", description = "教师相关接口")
public class TeacherController {
    @Resource
    private GradeService gradeService;
    @Resource
    private CourseOfferService courseOfferService;
    @Autowired
    private TeacherService teacherService;

    @PutMapping("/grade")
    @Operation(summary = "录入/修改学生成绩")
    public AjaxResult<Void> updateGrade(@RequestHeader("Authorization") String token,
                                        @RequestBody Grade grade) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"teacher".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        gradeService.updateGrade(grade);
        return AjaxResult.success();
    }

    @GetMapping("/grade")
    @Operation(summary = "获取某门课的学生成绩")
    public AjaxResult<List<Grade>> getGradesByOfferId(@RequestHeader("Authorization") String token,
                                                       @RequestParam("offerId") Integer offerId) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"teacher".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }

        List<Grade> grades = gradeService.getGradesByOfferId(offerId);
        if (grades == null || grades.isEmpty()) {
            return AjaxResult.success("没有找到该课程的成绩", null);
        }

        return AjaxResult.success(grades);
    }

    @GetMapping("/course")
    @Operation(summary = "获取当前教师课程列表")
    public AjaxResult<List<TeacherCourseView>> getTeacherCourses(@RequestHeader("Authorization") String token) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"teacher".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }

        List<TeacherCourseView> courseList = courseOfferService.getCourseListByTeacher(user.getUserId());

        if (courseList == null || courseList.isEmpty()) {
            return AjaxResult.success("当前教师没有课程", null);
        }

        return AjaxResult.success(courseList);
    }

    @GetMapping("/phone")
    @Operation(summary = "获取教师手机号")
    public AjaxResult<String> getTeacherPhone(@RequestHeader("Authorization") String token) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"teacher".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }

        String phone = teacherService.getTeacherPhone(user.getUserId());
        if (phone == null || phone.isEmpty()) {
            return AjaxResult.success("教师手机号未设置", null);
        }

        return AjaxResult.success(phone);
    }

    @PutMapping("/phone")
    @Operation(summary = "修改教师手机号")
    public AjaxResult<Void> updateTeacherPhone(@RequestHeader("Authorization") String token,
                                                        @RequestParam("phone") String phone) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"teacher".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        if (phone == null || phone.isEmpty()) {
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
        }

        teacherService.updateTeacherPhone(user.getUserId(), phone);
        return AjaxResult.success();
    }
}
