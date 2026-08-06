package com.wddlhyss.myblog.service;

import com.wddlhyss.myblog.entity.ScheduleRuleShiftOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 排班规则班次顺序表 服务类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
public interface IScheduleRuleShiftOrderService extends IService<ScheduleRuleShiftOrder> {

    void saveShiftOrder(Long ruleId, List<ScheduleRuleShiftOrder> shiftOrderList);
}
