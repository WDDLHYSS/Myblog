package com.wddlhyss.myblog.service;

import com.wddlhyss.myblog.entity.ScheduleRuleSpecial;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-09-19
 */
public interface IScheduleRuleSpecialService extends IService<ScheduleRuleSpecial> {

    void saveRuleSpecial(Long ruleId, Integer cycleDays);
}
