package com.wddlhyss.myblog.entity.VO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleRuleResponse {

    /**
     * 规则ID
     */
    private Long ruleId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * NORMAL / SPECIAL /....
     */
    private String ruleType;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 普通规则详情
     * SPECIAL 时为 null
     */
    private ScheduleRuleNormalResponse normalRule;

    /**
     * 特殊规则详情
     * NORMAL 时为 null
     */
    private ScheduleRuleSpecialResponse specialRule;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}