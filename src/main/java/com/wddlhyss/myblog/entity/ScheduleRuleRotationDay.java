package com.wddlhyss.myblog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 排班规则倒班星期表
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
@TableName("schedule_rule_rotation_day")
@Schema(name = "ScheduleRuleRotationDay", description = "排班规则倒班星期表")
public class ScheduleRuleRotationDay implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "排班规则ID")
    private Long ruleId;

    @Schema(description = "星期：1周一至7周日")
    private Integer dayOfWeek;

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

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public String toString() {
        return "ScheduleRuleRotationDay{" +
        "id = " + id +
        ", ruleId = " + ruleId +
        ", dayOfWeek = " + dayOfWeek +
        "}";
    }
}
