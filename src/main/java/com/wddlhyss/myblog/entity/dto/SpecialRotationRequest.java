package com.wddlhyss.myblog.entity.dto;

import lombok.Data;

@Data
public class SpecialRotationRequest {

    private String shiftCode;

    private Integer repeatCount;

    private Integer sortNumber;
}