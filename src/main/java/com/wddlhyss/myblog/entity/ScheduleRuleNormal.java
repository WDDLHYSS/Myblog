package com.wddlhyss.myblog.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 
 * </p>
 *
 * @author haoyanlu
 * @since 2026-09-19
 */
@TableName("schedule_rule_normal")
@Schema(name = "ScheduleRuleNormal", description = "")
public class ScheduleRuleNormal implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("rule_id")
    private Long ruleId;

    private Integer workDays;

    private Integer restDays;

    private Byte rotateOnRestDay;

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getWorkDays() {
        return workDays;
    }

    public void setWorkDays(Integer workDays) {
        this.workDays = workDays;
    }

    public Integer getRestDays() {
        return restDays;
    }

    public void setRestDays(Integer restDays) {
        this.restDays = restDays;
    }

    public Byte getRotateOnRestDay() {
        return rotateOnRestDay;
    }

    public void setRotateOnRestDay(Byte rotateOnRestDay) {
        this.rotateOnRestDay = rotateOnRestDay;
    }

    @Override
    public String toString() {
        return "ScheduleRuleNormal{" +
        "ruleId = " + ruleId +
        ", workDays = " + workDays +
        ", restDays = " + restDays +
        ", rotateOnRestDay = " + rotateOnRestDay +
        "}";
    }
}
