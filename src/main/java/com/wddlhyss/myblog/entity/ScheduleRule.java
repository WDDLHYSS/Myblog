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


    @Schema(description = "是否启用：1是，0否")
    private Byte enabled;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "是否为正常班次")
    private String ruleType;

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

    public String getRuleType() {return ruleType;}

    public void setRuleType(String ruleType) {this.ruleType = ruleType;}

    @Override
    public String toString() {
        return "ScheduleRule{" +
        "id = " + id +
        ", ruleName = " + ruleName +
        ", enabled = " + enabled +
        ", createTime = " + createTime +
        ", updateTime = " + updateTime +
        ", ruleType = " + ruleType +
        "}";
    }
}
