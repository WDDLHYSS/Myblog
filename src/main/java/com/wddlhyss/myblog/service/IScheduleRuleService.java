package com.wddlhyss.myblog.service;

import com.wddlhyss.myblog.entity.ScheduleRule;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wddlhyss.myblog.entity.dto.MakeSchedulePlanOfNormalRequest;
import com.wddlhyss.myblog.entity.dto.MakeSchedulePlanOfSpeciallRequest;

/**
 * <p>
 * 排班规则表 服务类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
public interface IScheduleRuleService extends IService<ScheduleRule> {

    Long addNormalRule(Long userId, MakeSchedulePlanOfNormalRequest makeSchedulePlanOfNormalRequest);

    Long addSpecialRule(Long userId, MakeSchedulePlanOfSpeciallRequest makeSchedulePlanOfSpeciallRequest);
}
