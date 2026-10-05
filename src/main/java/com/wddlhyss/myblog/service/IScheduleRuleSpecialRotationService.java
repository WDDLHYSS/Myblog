package com.wddlhyss.myblog.service;

import com.wddlhyss.myblog.entity.ScheduleRuleSpecialRotation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wddlhyss.myblog.entity.dto.SpecialRotationRequest;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-09-19
 */
public interface IScheduleRuleSpecialRotationService extends IService<ScheduleRuleSpecialRotation> {

    void saveRotations(Long ruleId, List<SpecialRotationRequest> rotationList);
}
