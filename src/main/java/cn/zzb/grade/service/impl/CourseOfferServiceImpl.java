package cn.zzb.grade.service.impl;

import cn.zzb.grade.constants.ExceptionEnum;
import cn.zzb.grade.entity.CourseOffer;
import cn.zzb.grade.entity.TeacherCourseView;
import cn.zzb.grade.exceptions.ApiException;
import cn.zzb.grade.mapper.CourseOfferMapper;
import cn.zzb.grade.service.CourseOfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zzb
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CourseOfferServiceImpl implements CourseOfferService {
    private final CourseOfferMapper courseOfferMapper;


    @Override
    public List<TeacherCourseView> getCourseListByTeacher(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
        }
        try {
            List<TeacherCourseView> courseList = courseOfferMapper.selectTeacherCourseView();
            // 过滤出指定教师的课程
            return courseList.stream()
                    .filter(course -> course.getTno().equals(userId))
                    .toList();
        } catch (Exception e) {
            log.error("获取教师课程列表失败: {}", e.getMessage());
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }
}
