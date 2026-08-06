package com.wddlhyss.myblog.entity.dto;

import com.wddlhyss.myblog.entity.ScheduleRuleShiftOrder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class MakeSchedulePlanRequest {

    /**
     * 方案名称。
     */
    private String ruleName;

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
    private List<Integer> rotationDayList;

    /**
     * 休息期间是否继续倒班。
     */
    private Boolean rotateOnRestDay;

    /**
     * 是否啓用
     */
    private Boolean enabled;

}