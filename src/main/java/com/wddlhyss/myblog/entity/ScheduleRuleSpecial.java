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
@TableName("schedule_rule_special")
@Schema(name = "ScheduleRuleSpecial", description = "")
public class ScheduleRuleSpecial implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("rule_id")
    private Long ruleId;

    private Integer cycleDays;

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getCycleDays() {
        return cycleDays;
    }

    public void setCycleDays(Integer cycleDays) {
        this.cycleDays = cycleDays;
    }

    @Override
    public String toString() {
        return "ScheduleRuleSpecial{" +
        "ruleId = " + ruleId +
        ", cycleDays = " + cycleDays +
        "}";
    }
}
