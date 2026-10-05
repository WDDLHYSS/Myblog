package com.wddlhyss.myblog.service;

import com.wddlhyss.myblog.entity.ScheduleRuleSpecialDay;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wddlhyss.myblog.entity.dto.SpecialDayRequest;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-09-19
 */
public interface IScheduleRuleSpecialDayService extends IService<ScheduleRuleSpecialDay> {

    void saveSpecialDay(Long ruleId, List<SpecialDayRequest> dayList);
}
