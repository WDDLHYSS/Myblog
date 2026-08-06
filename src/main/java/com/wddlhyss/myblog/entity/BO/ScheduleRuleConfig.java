package com.wddlhyss.myblog.entity.BO;

import com.wddlhyss.myblog.entity.ScheduleRuleShiftOrder;
import lombok.Data;

import java.util.List;
import java.util.Set;


@Data
public class ScheduleRuleConfig {

    /**
     * 连续上班天数。
     */
    private Integer workDays;

    /**
     * 连续休息天数。
     */
    private Integer restDays;

    /**
     * 班次轮换顺序。
     */
    private List<ScheduleRuleShiftOrder> shiftOrderList;

    /**
     * 倒班星期配置。
     */
    private Set<Integer> rotationDaySet;

    /**
     * 休息期间是否继续倒班。
     */
    private Boolean rotateOnRestDay;

}