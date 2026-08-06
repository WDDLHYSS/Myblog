package com.wddlhyss.myblog.service;

import com.wddlhyss.myblog.entity.UserSchedulePlan;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wddlhyss.myblog.entity.VO.SavedScheduleResponse;
import com.wddlhyss.myblog.entity.VO.ScheduleRow;
import com.wddlhyss.myblog.entity.VO.ScheduleRuleResponse;
import com.wddlhyss.myblog.entity.dto.MakeSchedulePlanOfNormalRequest;
import com.wddlhyss.myblog.entity.dto.MakeSchedulePlanOfSpeciallRequest;
import com.wddlhyss.myblog.entity.dto.ScheduleTestRequest;

import java.util.List;

/**
 * <p>
 * 用户个人排班方案表 服务类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
public interface IUserSchedulePlanService extends IService<UserSchedulePlan> {

    Long makeScheduleNormalPlan(Long userId, MakeSchedulePlanOfNormalRequest makeSchedulePlanOfNormalRequest);

    List<ScheduleRuleResponse> findUserRule(Long userId);

    boolean deleteByRulesId(Long ruleId,Long userId);

    ScheduleRuleResponse getRuleDetail(Long userId, Long ruleId);

    boolean updateRule(Long userId, Long ruleId, MakeSchedulePlanOfNormalRequest request);

    List<ScheduleRow> makeSchedulePlanRow(Long userId, ScheduleTestRequest request);

    SavedScheduleResponse getSavedSchedule(Long userId, Long ruleId);

    Long makeScheduleSpecialPlan(Long userId, MakeSchedulePlanOfSpeciallRequest makeSchedulePlanOfSpeciallRequest);

    boolean updateSpecialRule(Long userId, Long ruleId, MakeSchedulePlanOfSpeciallRequest request);
}
