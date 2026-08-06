package com.wddlhyss.myblog.service.impl;

import com.wddlhyss.myblog.entity.ScheduleRule;
import com.wddlhyss.myblog.entity.ScheduleRuleShiftOrder;
import com.wddlhyss.myblog.entity.dto.MakeSchedulePlanRequest;
import com.wddlhyss.myblog.mapper.ScheduleRuleMapper;
import com.wddlhyss.myblog.service.IScheduleRuleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 排班规则表 服务实现类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
@Service
public class ScheduleRuleServiceImpl extends ServiceImpl<ScheduleRuleMapper, ScheduleRule> implements IScheduleRuleService {



    @Override
    public Long addRule(Long userId, MakeSchedulePlanRequest makeSchedulePlanRequest) {
        ScheduleRule scheduleRule = new ScheduleRule();
        scheduleRule.setUserId(userId);
        scheduleRule.setRuleName(makeSchedulePlanRequest.getRuleName());
        scheduleRule.setWorkDays(makeSchedulePlanRequest.getWorkDays());
        scheduleRule.setRestDays(makeSchedulePlanRequest.getRestDays());
        scheduleRule.setRotateOnRestDay((byte) (Boolean.TRUE.equals(
                makeSchedulePlanRequest.getRotateOnRestDay()) ? 1 : 0));
        scheduleRule.setEnabled((byte)(makeSchedulePlanRequest.getEnabled() == null ||
                makeSchedulePlanRequest.getEnabled() ? 1 : 0));
        int affectedRows = baseMapper.insert(scheduleRule);

        if (affectedRows != 1) {
            throw new IllegalStateException("排班规则保存失败");
        }

        Long ruleId = scheduleRule.getId();

        if (ruleId == null) {
            throw new IllegalStateException("规则保存成功，但未获得ruleId");
        }

        return ruleId;
    }
}
