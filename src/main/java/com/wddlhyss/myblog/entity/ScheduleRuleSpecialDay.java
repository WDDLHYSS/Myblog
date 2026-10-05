package com.wddlhyss.myblog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("schedule_rule_special_day")
@Schema(name = "ScheduleRuleSpecialDay", description = "")
public class ScheduleRuleSpecialDay implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long ruleId;

    private Integer dayOffset;

    private String dayType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getDayOffset() {
        return dayOffset;
    }

    public void setDayOffset(Integer dayOffset) {
        this.dayOffset = dayOffset;
    }

    public String getDayType() {
        return dayType;
    }

    public void setDayType(String dayType) {
        this.dayType = dayType;
    }

    @Override
    public String toString() {
        return "ScheduleRuleSpecialDay{" +
        "id = " + id +
        ", ruleId = " + ruleId +
        ", dayOffset = " + dayOffset +
        ", dayType = " + dayType +
        "}";
    }
}
