package cn.zzb.grade.service;

import cn.zzb.grade.entity.Grade;
import com.github.jeffreyning.mybatisplus.service.IMppService;

import java.util.List;

/**
 * @author zzb
 */
public interface GradeService extends IMppService<Grade> {
    void updateGrade(Grade grade);

    List<Grade> getGradesByOfferId(Integer offerId);
}
