package com.wddlhyss.myblog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 用户个人排班方案表
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
@TableName("user_schedule_plan")
@Schema(name = "UserSchedulePlan", description = "用户个人排班方案表")
@AllArgsConstructor
@NoArgsConstructor
public class UserSchedulePlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "排班规则ID")
    private Long ruleId;

    @Schema(description = "个人方案名称")
    private String planName;

    @Schema(description = "排班计算起始日期")
    private LocalDate startDate;

    @Schema(description = "起始日期对应的初始班次")
    private String initialShiftCode;

    @Schema(description = "方案结束日期，空表示长期有效")
    private Integer totalDays;

    @Schema(description = "是否启用：1是，0否")
    private Byte enabled;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getInitialShiftCode() {
        return initialShiftCode;
    }

    public void setInitialShiftCode(String initialShiftCode) {
        this.initialShiftCode = initialShiftCode;
    }

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public Byte getEnabled() {
        return enabled;
    }

    public void setEnabled(Byte enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "UserSchedulePlan{" +
        "id = " + id +
        ", userId = " + userId +
        ", ruleId = " + ruleId +
        ", planName = " + planName +
        ", startDate = " + startDate +
        ", initialShiftCode = " + initialShiftCode +
        ", totalDays = " + totalDays +
        ", enabled = " + enabled +
        ", createTime = " + createTime +
        ", updateTime = " + updateTime +
        "}";
    }
}
