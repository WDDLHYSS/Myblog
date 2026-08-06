package com.wddlhyss.myblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wddlhyss.myblog.entity.ScheduleRule;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wddlhyss.myblog.entity.dto.MakeSchedulePlanRequest;

/**
 * <p>
 * 排班规则表 服务类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
public interface IScheduleRuleService extends IService<ScheduleRule> {

    Long addRule(Long userId, MakeSchedulePlanRequest makeSchedulePlanRequest);

}
