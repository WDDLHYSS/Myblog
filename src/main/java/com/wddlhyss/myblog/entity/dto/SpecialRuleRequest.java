package com.wddlhyss.myblog.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class SpecialRuleRequest {

    private Integer cycleDays;

    private List<SpecialRotationRequest> rotationList;

    private List<SpecialDayRequest> dayList;

}
