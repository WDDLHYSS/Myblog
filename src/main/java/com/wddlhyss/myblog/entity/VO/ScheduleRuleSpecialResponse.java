package com.wddlhyss.myblog.entity.VO;

import lombok.Data;

import java.util.List;

@Data
public class ScheduleRuleSpecialResponse {

    /**
     * 单周期天数
     */
    private Integer cycleDays;

    /**
     * 大班×2 -> 白班×1
     */
    private List<SpecialRotationResponse>
            rotationList;

    /**
     * 每天 WORK / REST
     */
    private List<SpecialDayResponse>
            dayList;
}