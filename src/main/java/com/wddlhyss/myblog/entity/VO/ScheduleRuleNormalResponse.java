package com.wddlhyss.myblog.entity.VO;

import com.wddlhyss.myblog.entity.ScheduleRuleShiftOrder;
import lombok.Data;

import java.util.List;

@Data
public class ScheduleRuleNormalResponse {

    private Integer workDays;

    private Integer restDays;

    private Boolean rotateOnRestDay;

    private List<ScheduleRuleShiftOrder>
            shiftOrderList;

    private List<Integer>
            rotationDayList;
}