package com.wddlhyss.myblog.entity.dto;

import lombok.Data;

@Data
public class SpecialDayRequest {

    private Integer dayOffset;

    private String dayType;
}