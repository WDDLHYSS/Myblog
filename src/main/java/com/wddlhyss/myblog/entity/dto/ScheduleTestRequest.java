package com.wddlhyss.myblog.entity.dto;

import java.time.LocalDate;

public class ScheduleTestRequest {

    /**
     * schedule_rule 表的主键。
     */
    private Long ruleId;

    /**
     * 排班开始日期。
     */
    private LocalDate startDate;

    /**
     * 初始班次编码 NORMAL。
     */
    private String initialShift;

    /**
     *  初始班次 SPECIAL
     */
    private Integer initialRotationIndex;

    /**
     * 生成天数。
     */
    private Integer totalDays;

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getInitialShift() {
        return initialShift;
    }

    public void setInitialShift(String initialShift) {
        this.initialShift = initialShift;
    }

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public Integer getInitialRotationIndex() {return initialRotationIndex;}

    public void setInitialRotationIndex(Integer initialRotationIndex) {this.initialRotationIndex = initialRotationIndex;}
}

