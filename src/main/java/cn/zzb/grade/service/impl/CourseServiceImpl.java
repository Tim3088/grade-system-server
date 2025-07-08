package cn.zzb.grade.service.impl;

import cn.zzb.grade.dto.request.OpenCourseRequest;
import cn.zzb.grade.dto.request.UpdateCourseRequest;
import cn.zzb.grade.entity.Class;
import cn.zzb.grade.entity.Course;
import cn.zzb.grade.entity.CourseOffer;
import cn.zzb.grade.entity.Teacher;
import cn.zzb.grade.exceptions.ApiException;
import cn.zzb.grade.mapper.ClassMapper;
import cn.zzb.grade.mapper.CourseMapper;
import cn.zzb.grade.mapper.CourseOfferMapper;
import cn.zzb.grade.mapper.TeacherMapper;
import cn.zzb.grade.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.zzb.grade.constants.ExceptionEnum.*;

/**
 * @author zzb
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;
    private final CourseOfferMapper courseOfferMapper;
    private final ClassMapper classMapper;

    @Override
    public List<String> importCourses(List<Course> list) {
        if (list == null || list.isEmpty()) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 对 list 去重
        List<Course> uniqueList = deduplicateCourses(list);
        try {
            // 获取所有课程编号
            List<Course> existingCourses = courseMapper.selectList(null);
            // 去掉uniqueList中已存在的课程
            Map<String, Course> existingCourseMap = existingCourses.stream()
                    .collect(Collectors.toMap(Course::getCno, c -> c));
            uniqueList = uniqueList.stream()
                    .filter(c -> !existingCourseMap.containsKey(c.getCno()))
                    .toList();
            for (Course course : uniqueList) {
                courseMapper.insert(course);
            }
        } catch (Exception e) {
            log.error("导入课程信息失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
        // 返回导入的课程ID列表
        return uniqueList.stream()
                .map(Course::getCno)
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> getCourseList() {
        try {
            List<Course> courseList = courseMapper.selectList(null);
            if (courseList == null || courseList.isEmpty()) {
                return new ArrayList<>();
            }
            // 去重
            return deduplicateCourses(courseList);
        } catch (Exception e) {
            log.error("获取课程列表失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    @Override
    public void updateCourse(UpdateCourseRequest course) {
        if (course == null) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 检查课程是否存在
        Course existingCourse = courseMapper.selectById(course.getOldCno());
        if (existingCourse == null) {
            throw new ApiException(COURSE_NOT_FOUND);
        }
        if (course.getNewCno() != null) {
            Course newCourse = courseMapper.selectById(course.getNewCno());
            if (newCourse != null && !newCourse.getCno().equals(existingCourse.getCno())) {
                throw new ApiException(COURSE_ALREADY_EXISTS);
            }
        }
        try {
            if (course.getName() != null) {
                existingCourse.setName(course.getName());
            }
            // 更新课程信息
            courseMapper.updateByOldId(course);
        }
        catch (Exception e) {
            log.error("更新课程信息失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);

        }
    }

    @Override
    public void deleteCourse(String cno) {
        if (cno == null || cno.isEmpty()) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 检查课程是否存在
        Course existingCourse = courseMapper.selectById(cno);
        if (existingCourse == null) {
            throw new ApiException(COURSE_NOT_FOUND);
        }
        try {
            // 删除课程
            courseMapper.deleteById(cno);
        } catch (Exception e) {
            log.error("删除课程失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    @Override
    public void addCourse(Course course) {
        if (course == null || course.getCno() == null) {
            throw new ApiException(INVALID_PARAMETER);
        }
        // 检查课程是否已存在
        Course existingCourse = courseMapper.selectById(course.getCno());
        if (existingCourse != null) {
            throw new ApiException(COURSE_ALREADY_EXISTS);
        }
        try {
            // 插入新课程
            courseMapper.insert(course);
        } catch (Exception e) {
            log.error("添加课程失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }
    }

    @Override
    public void openCourse(OpenCourseRequest course) {
        // 检查开设课程是否存在
        CourseOffer existingOffer = courseOfferMapper.selectById(course.getOfferId());
        if (existingOffer != null) {
            throw new ApiException(COURSE_ALREADY_OPENED);
        }
        // 检查课程是否存在
        Course existingCourse = courseMapper.selectById(course.getCno());
        if (existingCourse == null) {
            throw new ApiException(COURSE_NOT_FOUND);
        }
        // 检查教师是否存在
        Teacher existingTeacher = teacherMapper.selectById(course.getTno());
        if (existingTeacher == null) {
            throw new ApiException(TEACHER_NOT_FOUND);
        }
        // 检查班级是否存在
        Class existingClass = classMapper.selectById(course.getClassId());
        if (existingClass == null) {
            throw new ApiException(CLASS_NOT_FOUND);
        }
        try {
            // 插入课程开设信息
            CourseOffer courseOffer = CourseOffer.builder()
                    .offerId(course.getOfferId())
                    .cno(course.getCno())
                    .tno(course.getTno())
                    .classId(course.getClassId())
                    .term(course.getTerm())
                    .build();
            courseOfferMapper.insert(courseOffer);
        } catch (Exception e) {
            log.error("开设课程失败: {}", e.getMessage());
            throw new ApiException(UNKNOWN_ERROR);
        }

    }

    private List<Course> deduplicateCourses(List<Course> list) {
        return list.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Course::getCno, c -> c, (existing, replacement) -> existing),
                        courses -> new ArrayList<>(courses.values())
                ));
    }
}
