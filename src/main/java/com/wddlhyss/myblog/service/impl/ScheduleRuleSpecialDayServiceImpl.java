package com.wddlhyss.myblog.service.impl;

import com.wddlhyss.myblog.entity.ScheduleRuleSpecialDay;
import com.wddlhyss.myblog.entity.dto.SpecialDayRequest;
import com.wddlhyss.myblog.mapper.ScheduleRuleSpecialDayMapper;
import com.wddlhyss.myblog.service.IScheduleRuleSpecialDayService;
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
public class ScheduleRuleSpecialDayServiceImpl extends ServiceImpl<ScheduleRuleSpecialDayMapper, ScheduleRuleSpecialDay> implements IScheduleRuleSpecialDayService {

    @Override
    public void saveSpecialDay(Long ruleId, List<SpecialDayRequest> dayList) {
        if (ruleId == null) {
            throw new IllegalArgumentException("规则ID不能为空");
        }

        if (dayList == null || dayList.isEmpty()) {
            throw new IllegalArgumentException("循环倒班天数不能为null");
        }

        List<ScheduleRuleSpecialDay> specialDayList = new ArrayList<ScheduleRuleSpecialDay>();
        for (SpecialDayRequest day : dayList) {

            ScheduleRuleSpecialDay specialDay = new ScheduleRuleSpecialDay();

            specialDay.setRuleId(ruleId);

            specialDay.setDayOffset(day.getDayOffset());

            specialDay.setDayType(day.getDayType());

            specialDayList.add(specialDay);
        }
        boolean success = this.saveBatch(specialDayList);

        if (!success) {
            throw new IllegalArgumentException("保存具体上班情况失败");
        }
    }
}
