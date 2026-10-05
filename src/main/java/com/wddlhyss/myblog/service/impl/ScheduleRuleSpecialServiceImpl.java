package com.wddlhyss.myblog.service.impl;

import com.wddlhyss.myblog.entity.ScheduleRuleSpecial;
import com.wddlhyss.myblog.mapper.ScheduleRuleSpecialMapper;
import com.wddlhyss.myblog.service.IScheduleRuleSpecialService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-09-19
 */
@Service
public class ScheduleRuleSpecialServiceImpl extends ServiceImpl<ScheduleRuleSpecialMapper, ScheduleRuleSpecial> implements IScheduleRuleSpecialService {

    @Override
    public void saveRuleSpecial(Long ruleId, Integer cycleDays) {
        if (ruleId == null) {
            throw new IllegalArgumentException("规则ID不能为空");
        }

        if (cycleDays == null || cycleDays < 1) {
            throw new IllegalArgumentException("循环天数不能为null或者小于1");
        }
        ScheduleRuleSpecial ruleSpecial = new ScheduleRuleSpecial();

        ruleSpecial.setRuleId(ruleId);

        ruleSpecial.setCycleDays(cycleDays);

        int affectedRows = baseMapper.insert(ruleSpecial);

        if (affectedRows != 1) {
            throw new IllegalStateException("排班规则保存失败");
        }

    }
}
