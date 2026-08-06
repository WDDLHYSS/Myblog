package com.wddlhyss.myblog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 排班规则班次顺序表
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
@TableName("schedule_rule_shift_order")
@Schema(name = "ScheduleRuleShiftOrder", description = "排班规则班次顺序表")
public class ScheduleRuleShiftOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "排班规则ID")
    private Long ruleId;

    @Schema(description = "班次编码")
    private String shiftCode;

    @Schema(description = "班次名称")
    private String shiftName;

    @Schema(description = "轮换顺序")
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

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public Integer getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(Integer sortNumber) {
        this.sortNumber = sortNumber;
    }

    @Override
    public String toString() {
        return "ScheduleRuleShiftOrder{" +
        "id = " + id +
        ", ruleId = " + ruleId +
        ", shiftCode = " + shiftCode +
        ", shiftName = " + shiftName +
        ", sortNumber = " + sortNumber +
        "}";
    }
}
