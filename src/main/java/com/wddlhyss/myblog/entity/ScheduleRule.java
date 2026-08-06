package com.wddlhyss.myblog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 排班规则表
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
@TableName("schedule_rule")
@Schema(name = "ScheduleRule", description = "排班规则表")
public class ScheduleRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "连续上班天数")
    private Integer workDays;

    @Schema(description = "连续休息天数")
    private Integer restDays;

    @Schema(description = "休息日遇到倒班日是否倒班：1是，0否")
    private Byte rotateOnRestDay;

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

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
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


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ScheduleRule{" +
        "id = " + id +
        ", ruleName = " + ruleName +
        ", workDays = " + workDays +
        ", restDays = " + restDays +
        ", rotateOnRestDay = " + rotateOnRestDay +
        ", enabled = " + enabled +
        ", createTime = " + createTime +
        ", updateTime = " + updateTime +
        "}";
    }
}
