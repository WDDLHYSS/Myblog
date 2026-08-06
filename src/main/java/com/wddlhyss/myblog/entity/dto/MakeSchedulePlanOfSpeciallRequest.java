package com.wddlhyss.myblog.entity.dto;

import com.wddlhyss.myblog.entity.ScheduleRuleShiftOrder;
import lombok.Data;

import java.util.List;

@Data
public class MakeSchedulePlanOfSpeciallRequest {
    /**
     * 方案名称。
     */
    private String ruleName;

    /**
     * 规则类型
     */
    private String ruleType;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 班次轮换顺序
     */
    private SpecialRuleRequest specialRule;


}
