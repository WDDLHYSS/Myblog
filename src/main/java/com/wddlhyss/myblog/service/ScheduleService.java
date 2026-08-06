package com.wddlhyss.myblog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wddlhyss.myblog.entity.BO.ScheduleRuleConfig;
import com.wddlhyss.myblog.entity.ScheduleRule;
import com.wddlhyss.myblog.entity.ScheduleRuleRotationDay;
import com.wddlhyss.myblog.entity.ScheduleRuleShiftOrder;
import com.wddlhyss.myblog.entity.VO.ScheduleRow;
import com.wddlhyss.myblog.entity.dto.ScheduleTestRequest;
import com.wddlhyss.myblog.mapper.ScheduleRuleMapper;
import com.wddlhyss.myblog.mapper.ScheduleRuleRotationDayMapper;
import com.wddlhyss.myblog.mapper.ScheduleRuleShiftOrderMapper;
import org.springframework.stereotype.Service;
import com.wddlhyss.myblog.utils.ShiftScheduler;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScheduleService {


    private final ScheduleRuleMapper scheduleRuleMapper;

    private final ScheduleRuleShiftOrderMapper
             scheduleRuleShiftOrderMapper;

    private final ScheduleRuleRotationDayMapper
            scheduleRuleRotationDayMapper;

    public ScheduleService(
            ScheduleRuleMapper scheduleRuleMapper, ScheduleRuleShiftOrderMapper scheduleRuleShiftOrderMapper, ScheduleRuleRotationDayMapper scheduleRuleRotationDayMapper) {
        this.scheduleRuleMapper = scheduleRuleMapper;
        this.scheduleRuleShiftOrderMapper = scheduleRuleShiftOrderMapper;
        this.scheduleRuleRotationDayMapper = scheduleRuleRotationDayMapper;
    }

    public List<ScheduleRow> testRule(
            ScheduleTestRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "请求参数不能为空"
            );
        }

        if (request.getRuleId() == null) {
            throw new IllegalArgumentException(
                    "排班规则ID不能为空"
            );
        }

        if (request.getStartDate() == null) {
            throw new IllegalArgumentException(
                    "排班开始日期不能为空"
            );
        }

        if (request.getInitialShift() == null
                || request.getInitialShift().isBlank()) {
            throw new IllegalArgumentException(
                    "初始班次不能为空"
            );
        }

        if (request.getTotalDays() == null
                || request.getTotalDays() <= 0) {
            throw new IllegalArgumentException(
                    "生成天数必须大于0"
            );
        }

        Long ruleId = request.getRuleId();

        /*
         * 查询排班规则主表。
         */
        ScheduleRule scheduleRule =
                scheduleRuleMapper.selectById(ruleId);

        if (scheduleRule == null) {
            throw new IllegalArgumentException(
                    "排班规则不存在，ruleId：" + ruleId
            );
        }

        /*
         * 查询班次轮换顺序。
         *
         * 这里必须按照 sortNumber 升序。
         */
        List<ScheduleRuleShiftOrder> shiftOrderList =
                scheduleRuleShiftOrderMapper.selectList(
                        new LambdaQueryWrapper<ScheduleRuleShiftOrder>()
                                .eq(
                                        ScheduleRuleShiftOrder::getRuleId,
                                        ruleId
                                )
                                .orderByAsc(
                                        ScheduleRuleShiftOrder::getSortNumber
                                )
                );

        if (shiftOrderList.isEmpty()) {
            throw new IllegalArgumentException(
                    "当前规则未配置班次轮换顺序"
            );
        }

        /*
         * 查询倒班星期。
         */
        List<ScheduleRuleRotationDay> rotationDayList =
                scheduleRuleRotationDayMapper.selectList(
                        new LambdaQueryWrapper<ScheduleRuleRotationDay>()
                                .eq(
                                        ScheduleRuleRotationDay::getRuleId,
                                        ruleId
                                )
                );

        Set<Integer> rotationDaySet =
                rotationDayList.stream()
                        .map(
                                ScheduleRuleRotationDay::getDayOfWeek
                        )
                        .collect(Collectors.toSet());

        /*
         * 把三张表的数据组装成计算规则。
         */
        ScheduleRuleConfig scheduleRuleConfig =
                new ScheduleRuleConfig();

//        scheduleRuleConfig.setWorkDays(
//                scheduleRule.getWorkDays()
//        );
//
//        scheduleRuleConfig.setRestDays(
//                scheduleRule.getRestDays()
//        );
//
//        scheduleRuleConfig.setRotateOnRestDay(
//                scheduleRule.getRotateOnRestDay() != null && scheduleRule.getRotateOnRestDay() == 1
//        );

        scheduleRuleConfig.setShiftOrderList(
                shiftOrderList
        );

        scheduleRuleConfig.setRotationDaySet(
                rotationDaySet
        );

        /*
         * 调用排班计算方法。
         */
        return ShiftScheduler.generateMonthSchedule(
                request.getStartDate(),
                request.getInitialShift(),
                request.getTotalDays(),
                scheduleRuleConfig
        );
    }
}