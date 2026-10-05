package com.wddlhyss.myblog.service.impl;

import com.wddlhyss.myblog.entity.ScheduleRuleSpecialRotation;
import com.wddlhyss.myblog.entity.dto.SpecialRotationRequest;
import com.wddlhyss.myblog.mapper.ScheduleRuleSpecialRotationMapper;
import com.wddlhyss.myblog.service.IScheduleRuleSpecialRotationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-09-19
 */
@Service
public class ScheduleRuleSpecialRotationServiceImpl extends ServiceImpl<ScheduleRuleSpecialRotationMapper, ScheduleRuleSpecialRotation> implements IScheduleRuleSpecialRotationService {

    @Override
    public void saveRotations(Long ruleId, List<SpecialRotationRequest> rotationList) {

        if (ruleId == null) {
            throw new IllegalArgumentException("规则ID不能为空");
        }

        if (rotationList == null || rotationList.isEmpty()) {
            throw new IllegalArgumentException("班次循环不能为空");
        }

        List<ScheduleRuleSpecialRotation> specialRotationList = new ArrayList<ScheduleRuleSpecialRotation>();

        for (SpecialRotationRequest rotation : rotationList) {

            ScheduleRuleSpecialRotation specialRotation = new ScheduleRuleSpecialRotation();

            specialRotation.setRuleId(ruleId);

            specialRotation.setShiftCode(rotation.getShiftCode());

            specialRotation.setSortNumber(rotation.getSortNumber());

            specialRotation.setRepeatCount(rotation.getRepeatCount());

            specialRotationList.add(specialRotation);
        }

        boolean success = this.saveBatch(specialRotationList);

        if (!success) {
            throw new IllegalStateException("保存倒班班次信息失败");
        }
    }
}
