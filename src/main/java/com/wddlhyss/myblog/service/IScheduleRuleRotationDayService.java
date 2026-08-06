package com.wddlhyss.myblog.service;

import com.wddlhyss.myblog.entity.ScheduleRuleRotationDay;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 排班规则倒班星期表 服务类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
public interface IScheduleRuleRotationDayService extends IService<ScheduleRuleRotationDay> {

    void saveRotationDays(Long ruleId, List<Integer> rotationDayList);
}
