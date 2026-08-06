package com.wddlhyss.myblog.entity.BO;

import com.wddlhyss.myblog.entity.ScheduleRuleSpecialDay;
import com.wddlhyss.myblog.entity.ScheduleRuleSpecialRotation;
import lombok.Data;

import java.util.List;

@Data
public class ScheduleSpecialRuleConfig {

    /**
     * 单轮上班规则天数
     */
    private Integer cycleDays;

    /**
     * 大轮轮次数据
     */
    private List<ScheduleRuleSpecialRotation> specialRotations;

    /**
     * 轮次中每日排班安排
     */
    private List<ScheduleRuleSpecialDay> specialDaysList;

}
