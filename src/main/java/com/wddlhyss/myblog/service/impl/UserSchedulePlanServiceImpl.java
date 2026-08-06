package com.wddlhyss.myblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.wddlhyss.myblog.entity.BO.ScheduleRuleConfig;
import com.wddlhyss.myblog.entity.ScheduleRule;
import com.wddlhyss.myblog.entity.ScheduleRuleRotationDay;
import com.wddlhyss.myblog.entity.ScheduleRuleShiftOrder;
import com.wddlhyss.myblog.entity.UserSchedulePlan;
import com.wddlhyss.myblog.entity.VO.SavedScheduleResponse;
import com.wddlhyss.myblog.entity.VO.ScheduleRow;
import com.wddlhyss.myblog.entity.VO.ScheduleRuleResponse;
import com.wddlhyss.myblog.entity.dto.MakeSchedulePlanRequest;
import com.wddlhyss.myblog.entity.dto.ScheduleTestRequest;
import com.wddlhyss.myblog.mapper.ScheduleRuleRotationDayMapper;
import com.wddlhyss.myblog.mapper.UserSchedulePlanMapper;
import com.wddlhyss.myblog.service.IScheduleRuleRotationDayService;
import com.wddlhyss.myblog.service.IScheduleRuleService;
import com.wddlhyss.myblog.service.IScheduleRuleShiftOrderService;
import com.wddlhyss.myblog.service.IUserSchedulePlanService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wddlhyss.myblog.utils.ShiftScheduler;
import org.apache.http.impl.execchain.TunnelRefusedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户个人排班方案表 服务实现类
 * </p>
 *
 * @author haoyanlu
 * @since 2026-08-02
 */
@Service
public class UserSchedulePlanServiceImpl extends ServiceImpl<UserSchedulePlanMapper, UserSchedulePlan> implements IUserSchedulePlanService {

    @Autowired
    private IScheduleRuleService scheduleRuleService;

    @Autowired
    private IScheduleRuleShiftOrderService scheduleRuleShiftOrderService;

    @Autowired
    private IScheduleRuleRotationDayService scheduleRuleRotationDayService;

    @Override
    @Transactional
    public Long makeSchedulePlan(Long userId, MakeSchedulePlanRequest makeSchedulePlanRequest) {
        Long ruleId = scheduleRuleService.addRule(userId, makeSchedulePlanRequest);
        scheduleRuleShiftOrderService.saveShiftOrder(ruleId, makeSchedulePlanRequest.getShiftOrderList());
        scheduleRuleRotationDayService.saveRotationDays(ruleId,makeSchedulePlanRequest.getRotationDayList());
        return ruleId;
    }

    @Override
    public List<ScheduleRuleResponse> findUserRule(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        List<ScheduleRule> ruleList = scheduleRuleService.list(new LambdaQueryWrapper<ScheduleRule>()
                                .eq(ScheduleRule::getUserId, userId)
                                .orderByDesc(ScheduleRule::getCreateTime));

        List<ScheduleRuleResponse> responseList = new ArrayList<>();

        for (ScheduleRule rule : ruleList) {
            ScheduleRuleResponse response = new ScheduleRuleResponse();
            response.setRuleId(rule.getId());
            response.setRuleName(rule.getRuleName());
            response.setWorkDays(rule.getWorkDays());
            response.setRestDays(rule.getRestDays());
            response.setRotateOnRestDay(rule.getRotateOnRestDay() != null && rule.getRotateOnRestDay() == 1);
            response.setEnabled(rule.getEnabled() != null && rule.getEnabled() == 1);

            List<ScheduleRuleShiftOrder> shiftOrderList =
                    scheduleRuleShiftOrderService.list(new LambdaQueryWrapper<ScheduleRuleShiftOrder>()
                                    .eq(ScheduleRuleShiftOrder::getRuleId, rule.getId())
                                    .orderByAsc(ScheduleRuleShiftOrder::getSortNumber));
            response.setShiftOrderList(shiftOrderList);

            List<ScheduleRuleRotationDay> rotationDayEntityList =
                    scheduleRuleRotationDayService.list(new LambdaQueryWrapper<ScheduleRuleRotationDay>()
                                    .eq(ScheduleRuleRotationDay::getRuleId, rule.getId())
                                    .orderByAsc(ScheduleRuleRotationDay::getDayOfWeek));

            List<Integer> rotationDayList =
                    rotationDayEntityList.stream()
                            .map(ScheduleRuleRotationDay::getDayOfWeek)
                            .toList();

            response.setRotationDayList(rotationDayList);
            response.setCreateTime(rule.getCreateTime());
            response.setUpdateTime(rule.getUpdateTime());

            responseList.add(response);
        }
        return responseList;
    }

    @Override
    @Transactional
    public boolean deleteByRulesId(Long ruleId, Long userId) {

        ScheduleRule rule = scheduleRuleService.getOne(new LambdaQueryWrapper<ScheduleRule>()
                                                .eq(ScheduleRule::getId, ruleId)
                                                .eq(ScheduleRule::getUserId, userId));
        if (rule == null) {
            throw new IllegalArgumentException("规则不存在或无权删除");
        }

        if (ruleId == null) {
            throw new IllegalArgumentException("规则ID不能为空");
        }

        scheduleRuleRotationDayService.remove(new LambdaQueryWrapper<ScheduleRuleRotationDay>()
                        .eq(ScheduleRuleRotationDay::getRuleId, ruleId));

        scheduleRuleShiftOrderService.remove(new LambdaQueryWrapper<ScheduleRuleShiftOrder>()
                        .eq(ScheduleRuleShiftOrder::getRuleId, ruleId));

        boolean removed = scheduleRuleService.removeById(ruleId);

        if (!removed) {
            throw new IllegalArgumentException("规则不存在或删除失败");
        }

        return true;
    }

    @Override
    public ScheduleRuleResponse getRuleDetail(Long userId, Long ruleId) {

        ScheduleRule rule = scheduleRuleService.getOne(new LambdaQueryWrapper<ScheduleRule>()
                                .eq(ScheduleRule::getId, ruleId)
                                .eq(ScheduleRule::getUserId, userId));

        if (rule == null) {
            throw new IllegalArgumentException("规则不存在或无权访问");
        }

        List<ScheduleRuleShiftOrder> shiftOrderList =
                scheduleRuleShiftOrderService.list(
                        new LambdaQueryWrapper<ScheduleRuleShiftOrder>()
                                .eq(ScheduleRuleShiftOrder::getRuleId, ruleId)
                                .orderByAsc(ScheduleRuleShiftOrder::getSortNumber));

        List<Integer> rotationDayList =
                scheduleRuleRotationDayService.list(
                                new LambdaQueryWrapper<ScheduleRuleRotationDay>()
                                        .eq(ScheduleRuleRotationDay::getRuleId, ruleId))
                        .stream()
                        .map(ScheduleRuleRotationDay::getDayOfWeek)
                        .toList();

        ScheduleRuleResponse response = new ScheduleRuleResponse();

        response.setRuleId(rule.getId());
        response.setRuleName(rule.getRuleName());
        response.setWorkDays(rule.getWorkDays());
        response.setRestDays(rule.getRestDays());
        response.setRotateOnRestDay(rule.getRotateOnRestDay() != null && rule.getRotateOnRestDay() == 1);
        response.setEnabled(rule.getEnabled() != null && rule.getEnabled() == 1);
        response.setShiftOrderList(shiftOrderList);
        response.setRotationDayList(rotationDayList);
        response.setCreateTime(rule.getCreateTime());
        response.setUpdateTime(rule.getUpdateTime());

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRule(Long userId, Long ruleId, MakeSchedulePlanRequest request) {

        ScheduleRule existingRule =
                scheduleRuleService.getOne(new LambdaQueryWrapper<ScheduleRule>()
                                .eq(ScheduleRule::getId, ruleId)
                                .eq(ScheduleRule::getUserId, userId));

        if (existingRule == null) {
            throw new IllegalArgumentException("规则不存在或无权修改");
        }

        ScheduleRule updateRule = new ScheduleRule();
        updateRule.setId(ruleId);
        updateRule.setRuleName(request.getRuleName().trim());
        updateRule.setWorkDays(request.getWorkDays());
        updateRule.setRestDays(request.getRestDays());
        updateRule.setRotateOnRestDay((byte) (Boolean.TRUE.equals(request.getRotateOnRestDay()) ? 1 : 0));
        updateRule.setEnabled((byte) (Boolean.TRUE.equals(request.getEnabled()) ? 1 : 0));

        boolean updated = scheduleRuleService.updateById(updateRule);

        if (!updated) {
            throw new IllegalStateException("修改规则主表失败");
        }

        scheduleRuleShiftOrderService.remove(
                new LambdaQueryWrapper<ScheduleRuleShiftOrder>()
                        .eq(ScheduleRuleShiftOrder::getRuleId, ruleId));

        scheduleRuleRotationDayService.remove(
                new LambdaQueryWrapper<ScheduleRuleRotationDay>()
                        .eq(ScheduleRuleRotationDay::getRuleId, ruleId));

        scheduleRuleShiftOrderService.saveShiftOrder(ruleId, request.getShiftOrderList());

        scheduleRuleRotationDayService.saveRotationDays(ruleId, request.getRotationDayList());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ScheduleRow> makeSchedulePlanRow(
            Long userId,
            ScheduleTestRequest request
    ) {
        /*
         * 1. 基础参数校验
         */
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }

        if (request.getRuleId() == null) {
            throw new IllegalArgumentException("排班规则ID不能为空");
        }

        if (request.getStartDate() == null) {
            throw new IllegalArgumentException("排班开始日期不能为空");
        }

        if (request.getInitialShift() == null
                || request.getInitialShift().isBlank()) {
            throw new IllegalArgumentException("初始班次不能为空");
        }

        if (request.getTotalDays() == null
                || request.getTotalDays() <= 0) {
            throw new IllegalArgumentException("生成天数必须大于0");
        }

        Long ruleId = request.getRuleId();

        String initialShift =
                request.getInitialShift()
                        .trim()
                        .toUpperCase(Locale.ROOT);

        /*
         * 2. 查询当前用户自己的排班规则
         *
         * 不能直接使用 getById(ruleId)，
         * 否则可能使用到其他用户创建的规则。
         */
        ScheduleRule scheduleRule =
                scheduleRuleService.getOne(
                        new LambdaQueryWrapper<ScheduleRule>()
                                .eq(ScheduleRule::getId, ruleId)
                                .eq(ScheduleRule::getUserId, userId)
                                .last("LIMIT 1")
                );

        if (scheduleRule == null) {
            throw new IllegalArgumentException("排班规则不存在或无权使用，ruleId：" + ruleId);
        }

        /*
         * 3. 判断规则是否启用
         */
        if (!Byte.valueOf((byte) 1)
                .equals(scheduleRule.getEnabled())) {
            throw new IllegalStateException("当前排班规则已停用，无法生成排班");
        }

        /*
         * 4. 查询班次轮换顺序
         */
        List<ScheduleRuleShiftOrder> shiftOrderList =
                scheduleRuleShiftOrderService.list(
                        new LambdaQueryWrapper<ScheduleRuleShiftOrder>()
                                .eq(ScheduleRuleShiftOrder::getRuleId, ruleId)
                                .orderByAsc(ScheduleRuleShiftOrder::getSortNumber)
                );

        if (shiftOrderList == null
                || shiftOrderList.isEmpty()) {
            throw new IllegalArgumentException("当前规则未配置班次轮换顺序");
        }

        /*
         * 5. 检查初始班次是否属于当前规则
         */
        boolean initialShiftExists =
                shiftOrderList.stream()
                        .anyMatch(item ->
                                item.getShiftCode() != null
                                        && initialShift.equalsIgnoreCase(
                                        item.getShiftCode()
                                )
                        );

        if (!initialShiftExists) {
            throw new IllegalArgumentException("初始班次不属于当前排班规则：" + initialShift);
        }

        /*
         * 6. 查询规则倒班星期
         */
        List<ScheduleRuleRotationDay> rotationDayList =
                scheduleRuleRotationDayService.list(
                        new LambdaQueryWrapper<ScheduleRuleRotationDay>()
                                .eq(ScheduleRuleRotationDay::getRuleId, ruleId));

        Set<Integer> rotationDaySet;

        if (rotationDayList == null
                || rotationDayList.isEmpty()) {
            rotationDaySet = Collections.emptySet();
        } else {
            rotationDaySet =
                    rotationDayList.stream()
                            .map(ScheduleRuleRotationDay::getDayOfWeek)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
        }

        /*
         * 7. 组装排班计算配置
         */
        ScheduleRuleConfig scheduleRuleConfig = new ScheduleRuleConfig();

        scheduleRuleConfig.setWorkDays(scheduleRule.getWorkDays());

        scheduleRuleConfig.setRestDays(scheduleRule.getRestDays());

        scheduleRuleConfig.setRotateOnRestDay(
                Byte.valueOf((byte) 1).equals(scheduleRule.getRotateOnRestDay()));

        scheduleRuleConfig.setShiftOrderList(
                shiftOrderList
        );

        scheduleRuleConfig.setRotationDaySet(
                rotationDaySet
        );

        /*
         * 8. 先计算排班
         *
         * 计算失败时不修改数据库。
         */
        List<ScheduleRow> scheduleRowList =
                ShiftScheduler.generateMonthSchedule(
                        request.getStartDate(),
                        initialShift,
                        request.getTotalDays(),
                        scheduleRuleConfig
                );

        if (scheduleRowList == null) {
            throw new IllegalStateException("排班计算结果不能为空");
        }

        /*
         * 9. 查询相同用户、相同规则的已有排班
         */
        UserSchedulePlan userSchedulePlan =
                baseMapper.selectOne(
                        new LambdaQueryWrapper<UserSchedulePlan>()
                                .eq(UserSchedulePlan::getUserId, userId)
                                .eq(UserSchedulePlan::getRuleId, ruleId)
                                .last("LIMIT 1"));

        if (userSchedulePlan == null) {
            /*
             * 10. 第一次使用该规则生成排班
             */
            userSchedulePlan =
                    new UserSchedulePlan();

            userSchedulePlan.setUserId(userId);

            userSchedulePlan.setRuleId(ruleId);

            userSchedulePlan.setPlanName(scheduleRule.getRuleName());

            userSchedulePlan.setStartDate(request.getStartDate());

            userSchedulePlan.setInitialShiftCode(initialShift);

            userSchedulePlan.setTotalDays(request.getTotalDays());

            userSchedulePlan.setEnabled(scheduleRule.getEnabled());

            int inserted = baseMapper.insert(userSchedulePlan);

            if (inserted != 1) {
                throw new IllegalStateException("保存用户排班计划失败");
            }
        } else {
            /*
             * 11. 相同用户、相同规则已经生成过
             *
             * 直接覆盖之前的排班参数，
             * 不新增第二条记录。
             */
            userSchedulePlan.setPlanName(scheduleRule.getRuleName());

            userSchedulePlan.setStartDate(request.getStartDate());

            userSchedulePlan.setInitialShiftCode(initialShift);

            userSchedulePlan.setTotalDays(request.getTotalDays());

            userSchedulePlan.setEnabled(scheduleRule.getEnabled());

            int updated = baseMapper.updateById(userSchedulePlan);

            if (updated != 1) {
                throw new IllegalStateException("覆盖用户排班计划失败");
            }
        }

        /*
         * 12. 返回本次重新生成的排班结果
         */
        return scheduleRowList;
    }


    @Override
    public SavedScheduleResponse getSavedSchedule(
            Long userId,
            Long ruleId
    ) {
        /*
         * 1. 参数校验
         */
        if (userId == null) {
            throw new IllegalArgumentException(
                    "用户ID不能为空"
            );
        }

        if (ruleId == null) {
            throw new IllegalArgumentException(
                    "规则ID不能为空"
            );
        }

        /*
         * 2. 查询当前用户自己的排班规则
         *
         * 必须同时校验 userId，避免读取其他用户的规则。
         */
        ScheduleRule scheduleRule =
                scheduleRuleService.getOne(
                        new LambdaQueryWrapper<ScheduleRule>()
                                .eq(
                                        ScheduleRule::getId,
                                        ruleId
                                )
                                .eq(
                                        ScheduleRule::getUserId,
                                        userId
                                )
                                .last("LIMIT 1")
                );

        if (scheduleRule == null) {
            throw new IllegalArgumentException(
                    "排班规则不存在或无权访问，ruleId："
                            + ruleId
            );
        }

        /*
         * 3. 判断规则当前是否启用
         *
         * 这里必须读取 schedule_rule.enabled，
         * 不能使用 user_schedule_plan.enabled，
         * 因为计划表中的状态可能已经过期。
         */
        if (!Byte.valueOf((byte) 1)
                .equals(scheduleRule.getEnabled())) {
            throw new IllegalStateException(
                    "当前排班规则已停用，无法加载排班"
            );
        }

        /*
         * 4. 查询当前用户使用该规则保存的排班计划
         */
        UserSchedulePlan plan =
                baseMapper.selectOne(
                        new LambdaQueryWrapper<UserSchedulePlan>()
                                .eq(
                                        UserSchedulePlan::getUserId,
                                        userId
                                )
                                .eq(
                                        UserSchedulePlan::getRuleId,
                                        ruleId
                                )
                                .last("LIMIT 1")
                );

        /*
         * 5. 当前用户还没有使用该规则生成过排班
         */
        if (plan == null) {
            return new SavedScheduleResponse(
                    false,
                    ruleId,
                    null,
                    null,
                    null,
                    Collections.emptyList()
            );
        }

        /*
         * 6. 校验已经保存的排班参数
         */
        if (plan.getStartDate() == null) {
            throw new IllegalStateException(
                    "已保存排班缺少开始日期"
            );
        }

        if (plan.getInitialShiftCode() == null
                || plan.getInitialShiftCode().isBlank()) {
            throw new IllegalStateException(
                    "已保存排班缺少初始班次"
            );
        }

        if (plan.getTotalDays() == null
                || plan.getTotalDays() <= 0) {
            throw new IllegalStateException(
                    "已保存排班的生成天数无效"
            );
        }

        /*
         * 7. 根据数据库中保存的参数重新计算排班
         *
         * 不能调用 makeSchedulePlanRow()，
         * 否则读取排班时会再次更新数据库。
         */
        List<ScheduleRow> scheduleRows =
                calculateScheduleRows(
                        userId,
                        ruleId,
                        plan.getStartDate(),
                        plan.getInitialShiftCode(),
                        plan.getTotalDays()
                );

        if (scheduleRows == null) {
            scheduleRows =
                    Collections.emptyList();
        }

        /*
         * 8. 返回已保存的排班参数和计算结果
         */
        return new SavedScheduleResponse(
                true,
                ruleId,
                plan.getStartDate(),
                plan.getInitialShiftCode(),
                plan.getTotalDays(),
                scheduleRows
        );
    }
    private List<ScheduleRow> calculateScheduleRows(
            Long userId,
            Long ruleId,
            LocalDate startDate,
            String initialShift,
            Integer totalDays
    ) {
        ScheduleRule scheduleRule =
                scheduleRuleService.getOne(
                        new LambdaQueryWrapper<ScheduleRule>()
                                .eq(
                                        ScheduleRule::getId,
                                        ruleId
                                )
                                .eq(
                                        ScheduleRule::getUserId,
                                        userId
                                )
                                .last("LIMIT 1")
                );

        if (scheduleRule == null) {
            throw new IllegalArgumentException(
                    "排班规则不存在或无权使用"
            );
        }

        List<ScheduleRuleShiftOrder> shiftOrderList =
                scheduleRuleShiftOrderService.list(
                        new LambdaQueryWrapper<ScheduleRuleShiftOrder>()
                                .eq(
                                        ScheduleRuleShiftOrder::getRuleId,
                                        ruleId
                                )
                                .orderByAsc(
                                        ScheduleRuleShiftOrder::getSortNumber
                                )
                );

        if (shiftOrderList.isEmpty()) {
            throw new IllegalArgumentException(
                    "当前规则未配置班次轮换顺序"
            );
        }

        boolean initialShiftExists =
                shiftOrderList.stream()
                        .anyMatch(item ->
                                initialShift.equals(
                                        item.getShiftCode()
                                )
                        );

        if (!initialShiftExists) {
            throw new IllegalArgumentException(
                    "初始班次不属于当前规则"
            );
        }

        List<ScheduleRuleRotationDay> rotationDayList =
                scheduleRuleRotationDayService.list(
                        new LambdaQueryWrapper<ScheduleRuleRotationDay>()
                                .eq(
                                        ScheduleRuleRotationDay::getRuleId,
                                        ruleId
                                )
                );

        Set<Integer> rotationDaySet =
                rotationDayList.stream()
                        .map(
                                ScheduleRuleRotationDay::getDayOfWeek
                        )
                        .collect(Collectors.toSet());

        ScheduleRuleConfig config =
                new ScheduleRuleConfig();

        config.setWorkDays(
                scheduleRule.getWorkDays()
        );

        config.setRestDays(
                scheduleRule.getRestDays()
        );

        config.setRotateOnRestDay(
                scheduleRule.getRotateOnRestDay() != null
                        && scheduleRule.getRotateOnRestDay() == 1
        );

        config.setShiftOrderList(
                shiftOrderList
        );

        config.setRotationDaySet(
                rotationDaySet
        );

        return ShiftScheduler.generateMonthSchedule(
                startDate,
                initialShift,
                totalDays,
                config
        );
    }
}
