package com.wddlhyss.myblog.entity.VO;

import com.wddlhyss.myblog.entity.ScheduleRuleShiftOrder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ScheduleRuleResponse {

    private Long ruleId;

    private String ruleName;

    private Integer workDays;

    private Integer restDays;

    private Boolean rotateOnRestDay;

    private Boolean enabled;

    private List<ScheduleRuleShiftOrder>
            shiftOrderList;

    private List<Integer> rotationDayList;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}