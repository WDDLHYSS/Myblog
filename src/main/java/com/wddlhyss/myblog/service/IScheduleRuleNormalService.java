package com.wddlhyss.myblog.service;

import com.wddlhyss.myblog.entity.ScheduleRuleNormal;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wddlhyss.myblog.entity.dto.MakeSchedulePlanOfNormalRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-09-19
 */
public interface IScheduleRuleNormalService extends IService<ScheduleRuleNormal> {

    void saveRuleNormal(Long ruleId, MakeSchedulePlanOfNormalRequest makeSchedulePlanOfNormalRequest);
}
