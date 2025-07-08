package cn.zzb.grade.controller;

import cn.zzb.grade.constants.ExceptionEnum;
import cn.zzb.grade.dto.request.*;
import cn.zzb.grade.dto.response.*;
import cn.zzb.grade.entity.*;
import cn.zzb.grade.entity.Class;
import cn.zzb.grade.exceptions.ApiException;
import cn.zzb.grade.mapper.ClassMapper;
import cn.zzb.grade.mapper.CourseOfferMapper;
import cn.zzb.grade.mapper.MajorMapper;
import cn.zzb.grade.mapper.RegionMapper;
import cn.zzb.grade.models.AjaxResult;
import cn.zzb.grade.service.*;
import cn.zzb.grade.utils.JwtUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zzb
 */
@Slf4j
@RequestMapping("/api/admin")
@RestController
@Tag(name = "管理员", description = "管理员相关接口")
public class AdminController {
    @Resource
    private UserService userService;
    @Resource
    private StudentService studentService;
    @Resource
    private TeacherService teacherService;
    @Resource
    private CourseService courseService;

    @Resource
    private RegionMapper regionMapper;
    @Resource
    private ClassMapper classMapper;
    @Resource
    private MajorMapper majorMapper;
    @Resource
    private CourseOfferMapper courseOfferMapper;

    @Operation(summary = "批量导入用户信息")
    @PostMapping(path = "/import-users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult<List<String>> importUsers(@RequestHeader("Authorization") String token,
                                                   @RequestPart("file") MultipartFile file) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        List <String> result;
        try {
            List<User> list = EasyExcel.read(file.getInputStream())
                    .head(User.class)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .doReadSync();
            result = userService.importUsers(list);
        } catch (Exception e) {
            log.error("导入用户信息失败: {}", e.getMessage());
            return AjaxResult.fail(ExceptionEnum.INVALID_PARAMETER);
        }
        return AjaxResult.success(result);
    }

    @GetMapping("/user")
    @Operation(summary = "获取用户列表")
    public AjaxResult<GetUserListResponse> getUserList(@RequestHeader("Authorization") String token) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        List<User> userList = userService.getUserList();
        // 删除密码字段
        userList.forEach(u -> u.setPassword(null));
        GetUserListResponse response =GetUserListResponse.builder()
                .userList(userList)
                .build();
        return AjaxResult.success(response);
    }

    @PutMapping("/user")
    @Operation(summary = "更新用户信息")
    public AjaxResult<Void> updateUser(@RequestHeader("Authorization") String token,
                                       @RequestBody UpdateUserRequest request) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        userService.updateUser(request);
        return AjaxResult.success();
    }

    @DeleteMapping("/user")
    @Operation(summary = "删除用户")
    public AjaxResult<Void> deleteUser(@RequestHeader("Authorization") String token,
                                       @RequestParam("userId") String userId) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        userService.deleteUser(userId);
        return AjaxResult.success();
    }

    @PostMapping("/user")
    @Operation(summary = "添加用户")
    public AjaxResult<Void> addUser(@RequestHeader("Authorization") String token,
                                    @RequestBody User user) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        userService.addUser(user);
        return AjaxResult.success();
    }

    @GetMapping("/export-users")
    @Operation(summary = "批量导出用户信息")
    public AjaxResult<Void> exportUsers(@RequestHeader("Authorization") String token,
                               HttpServletResponse response) throws IOException {
        // 验证用户权限
        User user = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("用户信息表.xlsx", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);

        try {
            // 获取用户数据
            List<User> userList = userService.getUserList();

            userList.forEach(u -> u.setPassword(null));

            // 使用EasyExcel导出数据
            EasyExcel.write(response.getOutputStream(), User.class)
                    .sheet("用户信息")
                    .doWrite(userList);
        } catch (Exception e) {
            log.error("导出用户信息失败: {}", e.getMessage());
            return AjaxResult.fail();
        }

        return AjaxResult.success();
    }

    @Operation(summary = "批量导入学生信息")
    @PostMapping(path = "/import-students", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult<List<String>> importStudents(@RequestHeader("Authorization") String token,
                                                @RequestPart("file") MultipartFile file) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        List <String> result;
        try {
            List<User> userList = userService.getUserList();
            // 将userlist转化为map
            Map<String, User> userMap = userList.stream()
                    .collect(Collectors.toMap(User::getUserId, Function.identity()));
            List<Student> list = EasyExcel.read(file.getInputStream())
                    .head(Student.class)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .doReadSync();
            // 将不存在userMap中的学生从list中删除
            list.removeIf(student -> !userMap.containsKey(student.getSno()));
            // 将role不为student的学生从list中删除
            list.removeIf(student -> !"student".equals(userMap.get(student.getSno()).getRole()));
            result = studentService.importStudents(list);
        } catch (Exception e) {
            log.error("导入学生信息失败: {}", e.getMessage());
            return AjaxResult.fail(ExceptionEnum.INVALID_PARAMETER);
        }
        return AjaxResult.success(result);
    }

    @GetMapping("/student")
    @Operation(summary = "获取学生列表")
    public AjaxResult<GetStudentListResponse> getStudentList(@RequestHeader("Authorization") String token) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(user.getRole()) && !"teacher".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        List<StudentDetail> studentList = studentService.getStudentList();

        GetStudentListResponse response =GetStudentListResponse.builder()
                .studentList(studentList)
                .build();
        return AjaxResult.success(response);
    }

    @PostMapping("/student")
    @Operation(summary = "添加学生")
    public AjaxResult<Void> addStudent(@RequestHeader("Authorization") String token,
                                       @RequestBody Student student) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        studentService.addStudent(student);
        return AjaxResult.success();
    }

    @GetMapping("/region")
    @Operation(summary = "获取籍贯列表")
    public AjaxResult<List<Region>> getRegionList(@RequestHeader("Authorization") String token) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        List<Region> regionList = regionMapper.selectList(null);
        return AjaxResult.success(regionList);
    }

    @GetMapping("/class")
    @Operation(summary = "获取班级列表")
    public AjaxResult<List<Class>> getClassList(@RequestHeader("Authorization") String token) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(user.getRole()) && !"teacher".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        List<Class> classList = classMapper.selectList(null);
        return AjaxResult.success(classList);
    }

    @PostMapping("/class")
    @Operation(summary = "添加班级")
    public AjaxResult<Void> addClass(@RequestHeader("Authorization") String token,
                                     @RequestBody Class name) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        classMapper.insert(name);
        return AjaxResult.success();
    }

    @PutMapping("/class")
    @Operation(summary = "更新班级信息")
    public AjaxResult<Void> updateClass(@RequestHeader("Authorization") String token,
                                       @RequestBody Class name) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        classMapper.updateById(name);
        return AjaxResult.success();
    }

    @DeleteMapping("/class")
    @Operation(summary = "删除班级")
    public AjaxResult<Void> deleteClass(@RequestHeader("Authorization") String token,
                                       @RequestParam("cno") String cno) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        classMapper.deleteById(cno);
        return AjaxResult.success();
    }

    @GetMapping("/major")
    @Operation(summary = "获取专业列表")
    public AjaxResult<List<Major>> getMajorList(@RequestHeader("Authorization") String token) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        List<Major> majorList = majorMapper.selectList(null);
        return AjaxResult.success(majorList);
    }

    @PostMapping("/major")
    @Operation(summary = "添加专业")
    public AjaxResult<Void> addMajor(@RequestHeader("Authorization") String token,
                                     @RequestBody Major major) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        majorMapper.insert(major);
        return AjaxResult.success();
    }

    @PutMapping("/major")
    @Operation(summary = "更新专业信息")
    public AjaxResult<Void> updateMajor(@RequestHeader("Authorization") String token,
                                       @RequestBody Major major) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        majorMapper.updateById(major);
        return AjaxResult.success();
    }

    @DeleteMapping("/major")
    @Operation(summary = "删除专业")
    public AjaxResult<Void> deleteMajor(@RequestHeader("Authorization") String token,
                                       @RequestParam("majorId") String majorId) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        majorMapper.deleteById(majorId);
        return AjaxResult.success();
    }

    @GetMapping("/export-students")
    @Operation(summary = "批量导出学生信息")
    public AjaxResult<Void> exportStudents(@RequestHeader("Authorization") String token,
                                        HttpServletResponse response) throws IOException {
        // 验证用户权限
        User user = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("学生信息表.xlsx", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);

        try {
            // 获取学生数据
            List<StudentDetail> studentList = studentService.getStudentList();

            // 使用EasyExcel导出数据
            EasyExcel.write(response.getOutputStream(), StudentDetail.class)
                    .sheet("学生信息")
                    .doWrite(studentList);
        } catch (Exception e) {
            log.error("导出学生信息失败: {}", e.getMessage());
            return AjaxResult.fail();
        }

        return AjaxResult.success();
    }

    @DeleteMapping("/student")
    @Operation(summary = "删除学生")
    public AjaxResult<Void> deleteStudent(@RequestHeader("Authorization") String token,
                                       @RequestParam("sno") String sno) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        studentService.deleteStudent(sno);
        return AjaxResult.success();
    }

    @PutMapping("/student")
    @Operation(summary = "更新学生信息")
    public AjaxResult<Void> updateStudent(@RequestHeader("Authorization") String token,
                                       @RequestBody UpdateStudentRequest request) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        studentService.updateStudent(request);
        return AjaxResult.success();
    }

    @GetMapping("/teacher")
    @Operation(summary = "获取教师列表")
    public AjaxResult<GetTeacherListResponse> getTeacherList(@RequestHeader("Authorization") String token) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        List<Teacher> teacherList = teacherService.getTeacherList();

        GetTeacherListResponse response =GetTeacherListResponse.builder()
                .teacherList(teacherList)
                .build();
        return AjaxResult.success(response);
    }

    @PostMapping("/teacher")
    @Operation(summary = "添加教师")
    public AjaxResult<Void> addTeacher(@RequestHeader("Authorization") String token,
                                       @RequestBody Teacher teacher) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        teacherService.addTeacher(teacher);
        return AjaxResult.success();
    }

    @DeleteMapping("/teacher")
    @Operation(summary = "删除教师")
    public AjaxResult<Void> deleteTeacher(@RequestHeader("Authorization") String token,
                                          @RequestParam("tno") String tno) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        teacherService.deleteTeacher(tno);
        return AjaxResult.success();
    }

    @PutMapping("/teacher")
    @Operation(summary = "更新教师信息")
    public AjaxResult<Void> updateTeacher(@RequestHeader("Authorization") String token,
                                          @RequestBody UpdateTeacherRequest request) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        teacherService.updateTeacher(request);
        return AjaxResult.success();
    }

    @Operation(summary = "批量导入教师信息")
    @PostMapping(path = "/import-teachers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult<List<String>> importTeachers(@RequestHeader("Authorization") String token,
                                                   @RequestPart("file") MultipartFile file) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        List <String> result;
        try {
            List<User> userList = userService.getUserList();
            // 将userlist转化为map
            Map<String, User> userMap = userList.stream()
                    .collect(Collectors.toMap(User::getUserId, Function.identity()));
            List<Teacher> list = EasyExcel.read(file.getInputStream())
                    .head(Teacher.class)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .doReadSync();
            // 将不存在userMap中的教师从list中删除
            list.removeIf(teacher -> !userMap.containsKey(teacher.getTno()));
            // 将role不为teacher的教师从list中删除
            list.removeIf(teacher -> !"teacher".equals(userMap.get(teacher.getTno()).getRole()));
            result = teacherService.importTeachers(list);
        } catch (Exception e) {
            log.error("导入教师信息失败: {}", e.getMessage());
            return AjaxResult.fail(ExceptionEnum.INVALID_PARAMETER);
        }
        return AjaxResult.success(result);
    }

    @GetMapping("/export-teachers")
    @Operation(summary = "批量导出教师信息")
    public AjaxResult<Void> exportTeachers(@RequestHeader("Authorization") String token,
                                           HttpServletResponse response) throws IOException {
        // 验证用户权限
        User user = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("教师信息表.xlsx", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);

        try {
            // 获取教师数据
            List<Teacher> teacherList = teacherService.getTeacherList();

            // 使用EasyExcel导出数据
            EasyExcel.write(response.getOutputStream(), Teacher.class)
                    .sheet("教师信息")
                    .doWrite(teacherList);
        } catch (Exception e) {
            log.error("导出教师信息失败: {}", e.getMessage());
            return AjaxResult.fail();
        }

        return AjaxResult.success();
    }

    @Operation(summary = "批量导入课程信息")
    @PostMapping(path = "/import-courses", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult<List<String>> importCourses(@RequestHeader("Authorization") String token,
                                                @RequestPart("file") MultipartFile file) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        List <String> result;
        try {
            List<Course> list = EasyExcel.read(file.getInputStream())
                    .head(Course.class)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .doReadSync();
            result = courseService.importCourses(list);
        } catch (Exception e) {
            log.error("导入课程信息失败: {}", e.getMessage());
            return AjaxResult.fail(ExceptionEnum.INVALID_PARAMETER);
        }
        return AjaxResult.success(result);
    }

    @GetMapping("/course")
    @Operation(summary = "获取课程列表")
    public AjaxResult<GetCourseListResponse> getCourseList(@RequestHeader("Authorization") String token) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(user.getRole()) && !"teacher".equals(user.getRole()) && !"student".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        List<Course> courseList = courseService.getCourseList();
        GetCourseListResponse response =GetCourseListResponse.builder()
                .courseList(courseList)
                .build();
        return AjaxResult.success(response);
    }

    @PutMapping("/course")
    @Operation(summary = "更新课程信息")
    public AjaxResult<Void> updateCourse(@RequestHeader("Authorization") String token,
                                       @RequestBody UpdateCourseRequest request) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        courseService.updateCourse(request);
        return AjaxResult.success();
    }

    @DeleteMapping("/course")
    @Operation(summary = "删除课程")
    public AjaxResult<Void> deleteCourse(@RequestHeader("Authorization") String token,
                                       @RequestParam("cno") String cno) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        courseService.deleteCourse(cno);
        return AjaxResult.success();
    }

    @PostMapping("/course")
    @Operation(summary = "添加课程")
    public AjaxResult<Void> addCourse(@RequestHeader("Authorization") String token,
                                    @RequestBody Course course) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        courseService.addCourse(course);
        return AjaxResult.success();
    }

    @GetMapping("/export-courses")
    @Operation(summary = "批量导出课程信息")
    public AjaxResult<Void> exportCourses(@RequestHeader("Authorization") String token,
                                        HttpServletResponse response) throws IOException {
        // 验证用户权限
        User user = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("课程信息表.xlsx", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);

        try {
            // 获取课程数据
            List<Course> courseList = courseService.getCourseList();

            // 使用EasyExcel导出数据
            EasyExcel.write(response.getOutputStream(), Course.class)
                    .sheet("课程信息")
                    .doWrite(courseList);
        } catch (Exception e) {
            log.error("导出课程信息失败: {}", e.getMessage());
            return AjaxResult.fail();
        }

        return AjaxResult.success();
    }

    @PostMapping("/course/offer")
    @Operation(summary = "开设课程")
    public AjaxResult<Void> openCourse(@RequestHeader("Authorization") String token,
                                       @RequestBody OpenCourseRequest request) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        courseService.openCourse(request);
        return AjaxResult.success();
    }

    @GetMapping("/course/offer")
    @Operation(summary = "获取开设课程列表")
    public AjaxResult<List<CourseOffer>> getCourseOfferList(@RequestHeader("Authorization") String token) {
        User user = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(user.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        List<CourseOffer> courseOfferList = courseOfferMapper.selectList(null);
        return AjaxResult.success(courseOfferList);
    }

    @DeleteMapping("/course/offer")
    @Operation(summary = "删除开设课程")
    public AjaxResult<Void> deleteCourseOffer(@RequestHeader("Authorization") String token,
                                               @RequestParam("offerId") String offerId) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        courseOfferMapper.deleteById(offerId);
        return AjaxResult.success();
    }

    @PutMapping("/course/offer")
    @Operation(summary = "更新开设课程信息")
    public AjaxResult<Void> updateCourseOffer(@RequestHeader("Authorization") String token,
                                               @RequestBody CourseOffer request) {
        User currentUser = JwtUtil.getUserFromToken(token);
        if (!"admin".equals(currentUser.getRole())) {
            throw new ApiException(ExceptionEnum.ROLE_ERROR);
        }
        courseOfferMapper.updateById(request);
        return AjaxResult.success();
    }

}
