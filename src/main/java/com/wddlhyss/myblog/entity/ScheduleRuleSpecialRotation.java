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
@TableName("schedule_rule_special_rotation")
@Schema(name = "ScheduleRuleSpecialRotation", description = "")
public class ScheduleRuleSpecialRotation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long ruleId;

    private String shiftCode;

    private Integer repeatCount;

    private Integer sortNumber;

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

    public String getShiftCode() {
        return shiftCode;
    }

    public void setShiftCode(String shiftCode) {
        this.shiftCode = shiftCode;
    }

    public Integer getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(Integer repeatCount) {
        this.repeatCount = repeatCount;
    }

    public Integer getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(Integer sortNumber) {
        this.sortNumber = sortNumber;
    }

    @Override
    public String toString() {
        return "ScheduleRuleSpecialRotation{" +
        "id = " + id +
        ", ruleId = " + ruleId +
        ", shiftCode = " + shiftCode +
        ", repeatCount = " + repeatCount +
        ", sortNumber = " + sortNumber +
        "}";
    }
}
