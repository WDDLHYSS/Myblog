package com.wddlhyss.myblog.service.impl;

import com.wddlhyss.myblog.entity.ScheduleRuleNormal;
import com.wddlhyss.myblog.entity.dto.MakeSchedulePlanOfNormalRequest;
import com.wddlhyss.myblog.mapper.ScheduleRuleNormalMapper;
import com.wddlhyss.myblog.service.IScheduleRuleNormalService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-09-19
 */
@Service
public class ScheduleRuleNormalServiceImpl extends ServiceImpl<ScheduleRuleNormalMapper, ScheduleRuleNormal> implements IScheduleRuleNormalService {

    @Override
    public void saveRuleNormal(Long ruleId, MakeSchedulePlanOfNormalRequest makeSchedulePlanOfNormalRequest) {

        if (ruleId == null) {
            throw new IllegalArgumentException("规则ID不能为空");
        }

        if (makeSchedulePlanOfNormalRequest == null) {
            throw new IllegalArgumentException("排班条件不能为空");
        }

        ScheduleRuleNormal scheduleRuleNormal = new ScheduleRuleNormal();

        scheduleRuleNormal.setRuleId(ruleId);

        scheduleRuleNormal.setRestDays(makeSchedulePlanOfNormalRequest.getNormalRule().getRestDays());

        scheduleRuleNormal.setWorkDays(makeSchedulePlanOfNormalRequest.getNormalRule().getWorkDays());

        scheduleRuleNormal.setRotateOnRestDay((byte) (Boolean.TRUE.equals(
                makeSchedulePlanOfNormalRequest.getNormalRule().getRotateOnRestDay()) ? 1 : 0));

        int affectedRows = baseMapper.insert(scheduleRuleNormal);

        if (affectedRows != 1) {
            throw new IllegalStateException("排班规则保存失败");
        }

    }
}
