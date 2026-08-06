package com.wddlhyss.myblog.entity.dto;

import com.wddlhyss.myblog.entity.ScheduleRuleRotationDay;
import com.wddlhyss.myblog.entity.ScheduleRuleShiftOrder;
import lombok.Data;

import java.util.List;

@Data
public class NormalRuleRequest {

    private int workDays;

    private int restDays;

    private boolean rotateOnRestDay;

    private List<ScheduleRuleShiftOrder> shiftOrderList;

    private List<Integer> rotationDayList;
}
