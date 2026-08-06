package com.wddlhyss.myblog.service.impl;

import com.wddlhyss.myblog.entity.ScheduleRuleRotationDay;
import com.wddlhyss.myblog.mapper.ScheduleRuleRotationDayMapper;
import com.wddlhyss.myblog.service.IScheduleRuleRotationDayService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 排班规则倒班星期表 服务实现类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
@Service
public class ScheduleRuleRotationDayServiceImpl extends ServiceImpl<ScheduleRuleRotationDayMapper, ScheduleRuleRotationDay> implements IScheduleRuleRotationDayService {

    @Override
    public void saveRotationDays(Long ruleId, List<Integer> rotationDayList) {
        if (ruleId == null) {
            throw new IllegalArgumentException("规则ID不能为空");
        }

        if (rotationDayList == null || rotationDayList.isEmpty()) {
            throw new IllegalArgumentException("倒班星期不能为空");
        }

        Set<Integer> rotationDaySet = new LinkedHashSet<>(rotationDayList);

        List<ScheduleRuleRotationDay> entityList = new ArrayList<>();

        for (Integer rotationDay : rotationDaySet) {
            if (rotationDay == null || rotationDay < 1 || rotationDay > 7) {
                throw new IllegalArgumentException("倒班星期必须在1到7之间");
            }

            ScheduleRuleRotationDay entity =
                    new ScheduleRuleRotationDay();

            entity.setRuleId(ruleId);
            entity.setDayOfWeek(rotationDay);
            entityList.add(entity);
        }

        boolean success = this.saveBatch(entityList);

        if (!success) {
            throw new IllegalStateException("保存倒班星期失败");
        }
    }
}
