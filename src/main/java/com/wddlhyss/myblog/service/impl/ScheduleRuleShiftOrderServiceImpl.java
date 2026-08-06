package com.wddlhyss.myblog.service.impl;

import com.wddlhyss.myblog.entity.ScheduleRuleShiftOrder;
import com.wddlhyss.myblog.mapper.ScheduleRuleShiftOrderMapper;
import com.wddlhyss.myblog.service.IScheduleRuleShiftOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 排班规则班次顺序表 服务实现类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
@Service
public class ScheduleRuleShiftOrderServiceImpl extends ServiceImpl<ScheduleRuleShiftOrderMapper, ScheduleRuleShiftOrder> implements IScheduleRuleShiftOrderService {

    @Override
    public void saveShiftOrder(Long ruleId, List<ScheduleRuleShiftOrder> shiftOrderList) {
        if (ruleId == null) {
            throw new IllegalArgumentException("规则ID不能为空");
        }

        if (shiftOrderList == null || shiftOrderList.isEmpty()) {
            throw new IllegalArgumentException("班次轮换顺序不能为空");
        }

        for (int i = 0; i < shiftOrderList.size(); i++) {
            ScheduleRuleShiftOrder shiftOrder = shiftOrderList.get(i);

            if (shiftOrder == null) {
                throw new IllegalArgumentException("第" + (i + 1) + "条班次数据不能为空");
            }

            if (shiftOrder.getShiftCode() == null || shiftOrder.getShiftCode().isBlank()) {
                throw new IllegalArgumentException("第" + (i + 1) + "个班次编码不能为空");
            }

            if (shiftOrder.getShiftName() == null || shiftOrder.getShiftName().isBlank()) {
                throw new IllegalArgumentException("第" + (i + 1) + "个班次名称不能为空");
            }

            shiftOrder.setId(null);
            shiftOrder.setRuleId(ruleId);

            // 顺序以列表位置为准，防止前端乱传
            shiftOrder.setSortNumber(i + 1);

            shiftOrder.setShiftCode(shiftOrder.getShiftCode().trim().toUpperCase());

            shiftOrder.setShiftName(shiftOrder.getShiftName().trim());
        }

        boolean success = this.saveBatch(shiftOrderList);

        if (!success) {
            throw new IllegalStateException(
                    "保存班次轮换顺序失败"
            );
        }
    }
}
