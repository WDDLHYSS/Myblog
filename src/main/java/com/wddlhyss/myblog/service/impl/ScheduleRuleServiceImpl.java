package com.wddlhyss.myblog.service.impl;

import com.wddlhyss.myblog.entity.ScheduleRule;
import com.wddlhyss.myblog.entity.dto.MakeSchedulePlanOfNormalRequest;
import com.wddlhyss.myblog.entity.dto.MakeSchedulePlanOfSpeciallRequest;
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
    public Long addNormalRule(Long userId, MakeSchedulePlanOfNormalRequest makeSchedulePlanOfNormalRequest) {
        ScheduleRule scheduleRule = new ScheduleRule();
        scheduleRule.setUserId(userId);
        scheduleRule.setRuleName(makeSchedulePlanOfNormalRequest.getRuleName());
        scheduleRule.setRuleType(makeSchedulePlanOfNormalRequest.getRuleType());
        scheduleRule.setEnabled((byte)(makeSchedulePlanOfNormalRequest.getEnabled() == null ||
                makeSchedulePlanOfNormalRequest.getEnabled() ? 1 : 0));
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

    @Override
    public Long addSpecialRule(Long userId, MakeSchedulePlanOfSpeciallRequest makeSchedulePlanOfSpeciallRequest) {

        ScheduleRule scheduleRule = new ScheduleRule();

        scheduleRule.setUserId(userId);

        scheduleRule.setRuleName(makeSchedulePlanOfSpeciallRequest.getRuleName());

        scheduleRule.setRuleType(makeSchedulePlanOfSpeciallRequest.getRuleType());

        scheduleRule.setEnabled((byte)(makeSchedulePlanOfSpeciallRequest.getEnabled() == null ||
                makeSchedulePlanOfSpeciallRequest.getEnabled() ? 1 : 0));

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
