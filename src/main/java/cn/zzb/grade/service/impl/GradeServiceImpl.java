package cn.zzb.grade.service.impl;

import cn.zzb.grade.constants.ExceptionEnum;
import cn.zzb.grade.entity.Grade;
import cn.zzb.grade.exceptions.ApiException;
import cn.zzb.grade.mapper.GradeMapper;
import cn.zzb.grade.service.GradeService;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
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
public class GradeServiceImpl extends MppServiceImpl<GradeMapper, Grade> implements GradeService {
    private final GradeMapper gradeMapper;
    @Override
    public void updateGrade(Grade grade) {
        if (grade == null || grade.getSno() == null || grade.getOfferId() == null) {
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
        }
        // 检查成绩是否已存在
        Grade existingGrade = gradeMapper.selectByMultiId(grade);
        if (existingGrade == null) {
            throw new ApiException(ExceptionEnum.GRADE_NOT_FOUND);
        }

        // 更新成绩
        gradeMapper.updateByMultiId(grade);
    }

    @Override
    public List<Grade> getGradesByOfferId(Integer offerId) {
        if (offerId == null) {
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
        }
        // 查询指定课程的所有成绩
        List<Grade> list = gradeMapper.selectList(null);
        List<Grade> grades = list.stream()
                .filter(grade -> grade.getOfferId().equals(offerId))
                .toList();
        if (grades.isEmpty()) {
            return List.of();
        }
        return grades;
    }
}
