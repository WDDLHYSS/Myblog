package com.wddlhyss.myblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wddlhyss.myblog.entity.*;
import com.wddlhyss.myblog.entity.BO.ScheduleRuleConfig;
import com.wddlhyss.myblog.entity.BO.ScheduleSpecialRuleConfig;
import com.wddlhyss.myblog.entity.VO.SavedScheduleResponse;
import com.wddlhyss.myblog.entity.VO.ScheduleRow;
import com.wddlhyss.myblog.entity.VO.ScheduleRuleResponse;
import com.wddlhyss.myblog.entity.dto.MakeSchedulePlanOfNormalRequest;
import com.wddlhyss.myblog.entity.dto.MakeSchedulePlanOfSpeciallRequest;
import com.wddlhyss.myblog.entity.dto.ScheduleTestRequest;
import com.wddlhyss.myblog.entity.dto.SpecialRuleRequest;
import com.wddlhyss.myblog.mapper.UserSchedulePlanMapper;
import com.wddlhyss.myblog.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wddlhyss.myblog.utils.ScheduleRuleAssembler;
import com.wddlhyss.myblog.utils.ShiftScheduler;
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

    @Autowired
    private IScheduleRuleNormalService scheduleRuleNormalService;

    @Autowired
    private IScheduleRuleSpecialService scheduleRuleSpecialService;

    @Autowired
    private IScheduleRuleSpecialDayService scheduleRuleSpecialDayService;

    @Autowired
    private  IScheduleRuleSpecialRotationService scheduleRuleSpecialRotationService;

    @Override
    @Transactional
    public Long makeScheduleNormalPlan(Long userId, MakeSchedulePlanOfNormalRequest makeSchedulePlanOfNormalRequest) {

        Long ruleId = scheduleRuleService.addNormalRule(userId, makeSchedulePlanOfNormalRequest);

        scheduleRuleNormalService.saveRuleNormal(ruleId, makeSchedulePlanOfNormalRequest);

        scheduleRuleShiftOrderService.saveShiftOrder(ruleId, makeSchedulePlanOfNormalRequest.getNormalRule().getShiftOrderList());

        scheduleRuleRotationDayService.saveRotationDays(ruleId, makeSchedulePlanOfNormalRequest.getNormalRule().getRotationDayList());

        return ruleId;
    }

    @Override
    @Transactional
    public Long makeScheduleSpecialPlan(Long userId, MakeSchedulePlanOfSpeciallRequest makeSchedulePlanOfSpeciallRequest) {

        SpecialRuleRequest specialRuleRequest = makeSchedulePlanOfSpeciallRequest.getSpecialRule();

        Long ruleId = scheduleRuleService.addSpecialRule(userId,makeSchedulePlanOfSpeciallRequest);

        scheduleRuleSpecialService.saveRuleSpecial(ruleId,specialRuleRequest.getCycleDays());

        scheduleRuleSpecialRotationService.saveRotations(ruleId,specialRuleRequest.getRotationList());

        scheduleRuleSpecialDayService.saveSpecialDay(ruleId,specialRuleRequest.getDayList());

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
            /**
             * 普通规则
             */
            if (rule.getRuleType().equals("NORMAL")) {

                //查询scheduleRuleNormal获取restday,workday,rotateonrestday
                ScheduleRuleNormal  scheduleRuleNormal =
                        scheduleRuleNormalService.getById(rule.getId());

                //获取排班规则
                List<ScheduleRuleShiftOrder> shiftOrderList =
                        scheduleRuleShiftOrderService.list(new LambdaQueryWrapper<ScheduleRuleShiftOrder>()
                                .eq(ScheduleRuleShiftOrder::getRuleId, rule.getId())
                                .orderByAsc(ScheduleRuleShiftOrder::getSortNumber));

                //获取倒班时间
                List<ScheduleRuleRotationDay> rotationDayEntityList =
                        scheduleRuleRotationDayService.list(new LambdaQueryWrapper<ScheduleRuleRotationDay>()
                                .eq(ScheduleRuleRotationDay::getRuleId, rule.getId())
                                .orderByAsc(ScheduleRuleRotationDay::getDayOfWeek));

                //倒班时间以list存储方便前端解析
                List<Integer> rotationDayList =
                        rotationDayEntityList.stream()
                                .map(ScheduleRuleRotationDay::getDayOfWeek)
                                .toList();

                response = ScheduleRuleAssembler.buildNormal(rule,scheduleRuleNormal,shiftOrderList,rotationDayList);
            }
            /**
             * 特殊规则，目前只有一个specail
             * @todo 后续有新规则再加入
             */
            else{

                ScheduleRuleSpecial scheduleRuleSpecial =
                        scheduleRuleSpecialService.getById(rule.getId());

                List<ScheduleRuleSpecialRotation> rotationList =
                        scheduleRuleSpecialRotationService.list(new LambdaQueryWrapper<ScheduleRuleSpecialRotation>()
                                .eq(ScheduleRuleSpecialRotation::getRuleId, rule.getId())
                                .orderByAsc(ScheduleRuleSpecialRotation::getSortNumber));

                List<ScheduleRuleSpecialDay> specialDaysList =
                        scheduleRuleSpecialDayService.list(new LambdaQueryWrapper<ScheduleRuleSpecialDay>()
                        .eq(ScheduleRuleSpecialDay::getRuleId, rule.getId())
                                .orderByAsc(ScheduleRuleSpecialDay :: getDayOffset));

                response = ScheduleRuleAssembler.buildSpecial(rule,scheduleRuleSpecial,rotationList,specialDaysList);

            }

            responseList.add(response);
        }
        return responseList;
    }

    @Override
    @Transactional
    public boolean deleteByRulesId(Long ruleId, Long userId) {

        if (ruleId == null) {
            throw new IllegalArgumentException("规则ID不能为空");
        }

        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        ScheduleRule rule = scheduleRuleService.getOne(new LambdaQueryWrapper<ScheduleRule>()
                                                .eq(ScheduleRule::getId, ruleId)
                                                .eq(ScheduleRule::getUserId, userId));
        if (rule == null) {
            throw new IllegalArgumentException("规则不存在或无权删除");
        }
        /**
         * NORMAL
         */
        if(rule.getRuleType().equals("NORMAL")){

            scheduleRuleNormalService.removeById(ruleId);

            scheduleRuleShiftOrderService.remove(new LambdaQueryWrapper<ScheduleRuleShiftOrder>()
                    .eq(ScheduleRuleShiftOrder::getRuleId, ruleId));

            scheduleRuleRotationDayService.remove(new LambdaQueryWrapper<ScheduleRuleRotationDay>()
                    .eq(ScheduleRuleRotationDay::getRuleId, ruleId));

        }
        /**
         * SPECIAL
         */
        else if (rule.getRuleType().equals("SPECIAL")){

            scheduleRuleSpecialService.removeById(ruleId);

            scheduleRuleSpecialDayService.remove(new LambdaQueryWrapper<ScheduleRuleSpecialDay>()
                    .eq(ScheduleRuleSpecialDay::getRuleId, ruleId));

            scheduleRuleSpecialRotationService.remove(new LambdaQueryWrapper<ScheduleRuleSpecialRotation>()
                    .eq(ScheduleRuleSpecialRotation::getRuleId, ruleId));

        }

        boolean removed = scheduleRuleService.removeById(ruleId);



        if (!removed) {
            throw new IllegalArgumentException("规则不存在或删除失败");
        }

        return true;
    }

    @Override
    public ScheduleRuleResponse getRuleDetail(Long userId, Long ruleId) {
        ScheduleRuleResponse response = new ScheduleRuleResponse();

        ScheduleRule rule = scheduleRuleService.getOne(new LambdaQueryWrapper<ScheduleRule>()
                                .eq(ScheduleRule::getId, ruleId)
                                .eq(ScheduleRule::getUserId, userId));

        if (rule == null) {
            throw new IllegalArgumentException("规则不存在或无权访问");
        }

        /**
         * 普通规则
         */
        if (rule.getRuleType().equals("NORMAL")) {

            //查询scheduleRuleNormal获取restday,workday,rotateonrestday
            ScheduleRuleNormal  scheduleRuleNormal =
                    scheduleRuleNormalService.getById(rule.getId());

            //获取排班规则
            List<ScheduleRuleShiftOrder> shiftOrderList =
                    scheduleRuleShiftOrderService.list(new LambdaQueryWrapper<ScheduleRuleShiftOrder>()
                            .eq(ScheduleRuleShiftOrder::getRuleId, rule.getId())
                            .orderByAsc(ScheduleRuleShiftOrder::getSortNumber));

            //获取倒班时间
            List<ScheduleRuleRotationDay> rotationDayEntityList =
                    scheduleRuleRotationDayService.list(new LambdaQueryWrapper<ScheduleRuleRotationDay>()
                            .eq(ScheduleRuleRotationDay::getRuleId, rule.getId())
                            .orderByAsc(ScheduleRuleRotationDay::getDayOfWeek));

            //倒班时间以list存储方便前端解析
            List<Integer> rotationDayList =
                    rotationDayEntityList.stream()
                            .map(ScheduleRuleRotationDay::getDayOfWeek)
                            .toList();

            response = ScheduleRuleAssembler.buildNormal(rule,scheduleRuleNormal,shiftOrderList,rotationDayList);
        }
        /**
         * 特殊规则，目前只有一个specail
         * @todo 后续有新规则再加入
         */
        else{

            ScheduleRuleSpecial scheduleRuleSpecial =
                    scheduleRuleSpecialService.getById(rule.getId());

            List<ScheduleRuleSpecialRotation> rotationList =
                    scheduleRuleSpecialRotationService.list(new LambdaQueryWrapper<ScheduleRuleSpecialRotation>()
                            .eq(ScheduleRuleSpecialRotation::getRuleId, rule.getId())
                            .orderByAsc(ScheduleRuleSpecialRotation::getSortNumber));

            List<ScheduleRuleSpecialDay> specialDaysList =
                    scheduleRuleSpecialDayService.list(new LambdaQueryWrapper<ScheduleRuleSpecialDay>()
                            .eq(ScheduleRuleSpecialDay::getRuleId, rule.getId())
                            .orderByAsc(ScheduleRuleSpecialDay :: getDayOffset));

            response = ScheduleRuleAssembler.buildSpecial(rule,scheduleRuleSpecial,rotationList,specialDaysList);

        }

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRule(Long userId, Long ruleId, MakeSchedulePlanOfNormalRequest request) {

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
        updateRule.setEnabled((byte) (Boolean.TRUE.equals(request.getEnabled()) ? 1 : 0));

        boolean updated = scheduleRuleService.updateById(updateRule);

        if (!updated) {
            throw new IllegalStateException("修改规则主表失败");
        }

        scheduleRuleNormalService.removeById(ruleId);

        scheduleRuleShiftOrderService.remove(
                new LambdaQueryWrapper<ScheduleRuleShiftOrder>()
                        .eq(ScheduleRuleShiftOrder::getRuleId, ruleId));

        scheduleRuleRotationDayService.remove(
                new LambdaQueryWrapper<ScheduleRuleRotationDay>()
                        .eq(ScheduleRuleRotationDay::getRuleId, ruleId));

        scheduleRuleNormalService.saveRuleNormal(ruleId,request);

        scheduleRuleShiftOrderService.saveShiftOrder(ruleId, request.getNormalRule().getShiftOrderList());

        scheduleRuleRotationDayService.saveRotationDays(ruleId, request.getNormalRule().getRotationDayList());

        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSpecialRule(Long userId, Long ruleId, MakeSchedulePlanOfSpeciallRequest request) {

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

        updateRule.setEnabled((byte) (Boolean.TRUE.equals(request.getEnabled()) ? 1 : 0));

        boolean updated = scheduleRuleService.updateById(updateRule);

        if (!updated) {
            throw new IllegalStateException("修改规则主表失败");
        }

        scheduleRuleSpecialService.removeById(ruleId);

        scheduleRuleSpecialDayService.remove(new LambdaQueryWrapper<ScheduleRuleSpecialDay>()
                .eq(ScheduleRuleSpecialDay::getRuleId, ruleId));

        scheduleRuleSpecialRotationService.remove(new LambdaQueryWrapper<ScheduleRuleSpecialRotation>()
                .eq(ScheduleRuleSpecialRotation::getRuleId, ruleId));

        scheduleRuleSpecialService.saveRuleSpecial(ruleId,request.getSpecialRule().getCycleDays());

        scheduleRuleSpecialDayService.saveSpecialDay(ruleId,request.getSpecialRule().getDayList());

        scheduleRuleSpecialRotationService.saveRotations(ruleId,request.getSpecialRule().getRotationList());

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

        if (request.getTotalDays() == null
                || request.getTotalDays() <= 0) {
            throw new IllegalArgumentException("生成天数必须大于0");
        }

        Long ruleId = request.getRuleId();

        /*
         * 2.1 查询当前用户自己的排班规则
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

        if (!Byte.valueOf((byte) 1)
                .equals(scheduleRule.getEnabled())) {
            throw new IllegalStateException("当前排班规则已停用，无法生成排班");
        }

        List<ScheduleRow> scheduleRowList = new ArrayList<ScheduleRow>();
        /**
         * 判断排班规则是normal还是special
         *
         * NORMAL
         */
        if (scheduleRule.getRuleType().equals("NORMAL")){

            /*
             * 2. 判断初始班次不能为空
             *
             */
            if (request.getInitialShift() == null
                    || request.getInitialShift().isBlank()) {
                throw new IllegalArgumentException("初始班次不能为空");
            }

            String initialShift =
                    request.getInitialShift()
                            .trim()
                            .toUpperCase(Locale.ROOT);

            /*
             * 3. 查询当前用户自己的排班规则的restday，workday，restonwork
             *
             * 通过ruleid查询
             */
            ScheduleRuleNormal scheduleRuleNormal =
                    scheduleRuleNormalService.getOne(
                            new LambdaQueryWrapper<ScheduleRuleNormal>()
                                    .eq(ScheduleRuleNormal::getRuleId, scheduleRule.getId()));

            if (scheduleRuleNormal == null) {
                throw new IllegalStateException("普通排班规则配置不存在");
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

            scheduleRuleConfig.setWorkDays(scheduleRuleNormal.getWorkDays());

            scheduleRuleConfig.setRestDays(scheduleRuleNormal.getRestDays());

            scheduleRuleConfig.setRotateOnRestDay(
                    Byte.valueOf((byte) 1).equals(scheduleRuleNormal.getRotateOnRestDay()));

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
            scheduleRowList =
                    ShiftScheduler.generateMonthSchedule(
                            request.getStartDate(),
                            initialShift,
                            request.getTotalDays(),
                            scheduleRuleConfig
                    );
        }
        else if (scheduleRule.getRuleType().equals("SPECIAL")){

            if (request.getInitialRotationIndex() == null
                    || request.getInitialRotationIndex() < 0) {
                throw new IllegalArgumentException("特殊排班初始轮次不能为空");
            }

            int initialRotationIndex = request.getInitialRotationIndex();

            //获取cycledays
            ScheduleRuleSpecial scheduleRuleSpecial =
                    scheduleRuleSpecialService.getOne(
                            new LambdaQueryWrapper<ScheduleRuleSpecial>()
                                    .eq(ScheduleRuleSpecial ::getRuleId , scheduleRule.getId()));

            if (scheduleRuleSpecial == null) {
                throw new IllegalStateException("特殊排班规则配置不存在");
            }

            List<ScheduleRuleSpecialDay> specialDayList  =
                    scheduleRuleSpecialDayService.list(
                            new LambdaQueryWrapper<ScheduleRuleSpecialDay>()
                                    .eq(ScheduleRuleSpecialDay::getRuleId, scheduleRule.getId())
                                    .orderByAsc(ScheduleRuleSpecialDay::getDayOffset)
                    );

            List<ScheduleRuleSpecialRotation> specialRotationList =
                    scheduleRuleSpecialRotationService.list(
                            new LambdaQueryWrapper<ScheduleRuleSpecialRotation>()
                                    .eq(ScheduleRuleSpecialRotation::getRuleId, scheduleRule.getId())
                                    .orderByAsc(ScheduleRuleSpecialRotation::getSortNumber)
                    );

            ScheduleSpecialRuleConfig specialRuleConfig = new ScheduleSpecialRuleConfig();

            specialRuleConfig.setCycleDays(scheduleRuleSpecial.getCycleDays());

            specialRuleConfig.setSpecialRotations(specialRotationList);

            specialRuleConfig.setSpecialDaysList(specialDayList);

            scheduleRowList =
                    ShiftScheduler.generateSpecialMonthSchedule(
                            request.getStartDate(),
                            initialRotationIndex,
                            request.getTotalDays(),
                            specialRuleConfig
                    );
        }else {

            throw new IllegalArgumentException("未知排班规则类型：" + scheduleRule.getRuleType());
        }

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

            if ("NORMAL".equals(scheduleRule.getRuleType())) {

                userSchedulePlan.setInitialShiftCode(
                        request.getInitialShift()
                                .trim()
                                .toUpperCase(Locale.ROOT)
                );

                userSchedulePlan.setInitialRotationIndex(null);
            }

            else if ("SPECIAL".equals(scheduleRule.getRuleType())) {

                userSchedulePlan.setInitialShiftCode(null);

                userSchedulePlan.setInitialRotationIndex(
                        request.getInitialRotationIndex()
                );
            }

            userSchedulePlan.setTotalDays(request.getTotalDays());

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

            if ("NORMAL".equals(scheduleRule.getRuleType())) {

                userSchedulePlan.setInitialShiftCode(
                        request.getInitialShift()
                                .trim()
                                .toUpperCase(Locale.ROOT)
                );

                userSchedulePlan.setInitialRotationIndex(null);
            }

            else if ("SPECIAL".equals(scheduleRule.getRuleType())) {

                userSchedulePlan.setInitialShiftCode(null);

                userSchedulePlan.setInitialRotationIndex(
                        request.getInitialRotationIndex()
                );
            }

            userSchedulePlan.setTotalDays(request.getTotalDays());

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


        if (plan.getTotalDays() == null
                || plan.getTotalDays() <= 0) {
            throw new IllegalStateException(
                    "已保存排班的生成天数无效"
            );
        }

        List<ScheduleRow> scheduleRows = new ArrayList<>();

        if ("NORMAL".equals(scheduleRule.getRuleType())) {
            if (plan.getInitialShiftCode() == null
                    || plan.getInitialShiftCode().isBlank()) {
                throw new IllegalStateException(
                        "已保存排班缺少初始班次"
                );
            }

            /*
             * 7. 根据数据库中保存的参数重新计算排班
             *
             * 不能调用 makeSchedulePlanRow()，
             * 否则读取排班时会再次更新数据库。
             */
             scheduleRows =
                    calculateScheduleNormalRows(
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

            return new SavedScheduleResponse(
                    true,
                    ruleId,
                    plan.getStartDate(),
                    plan.getInitialShiftCode(),
                    null,
                    plan.getTotalDays(),
                    scheduleRows
            );
        }
        else if ("SPECIAL".equals(scheduleRule.getRuleType())) {

            if (plan.getInitialRotationIndex() == null
                    || plan.getInitialRotationIndex() < 0){
                throw new IllegalStateException("特殊排班初始轮次不能为空");
            }

                scheduleRows =
                        calculateScheduleSpecialRows(
                                userId,
                                ruleId,
                                plan.getStartDate(),
                                plan.getInitialRotationIndex(),
                                plan.getTotalDays()
                        );

               return new SavedScheduleResponse(
                       true,
                       ruleId,
                       plan.getStartDate(),
                       null,
                       plan.getInitialRotationIndex(),
                       plan.getTotalDays(),
                       scheduleRows
               );
        }
        return null;
    }

    private List<ScheduleRow> calculateScheduleSpecialRows(
            Long userId,
            Long ruleId,
            LocalDate startDate,
            Integer initialRotationIndex,
            Integer totalDays
    ){

        ScheduleRule scheduleRule =
                scheduleRuleService.getOne(
                        new LambdaQueryWrapper<ScheduleRule>()
                                .eq(ScheduleRule::getId, ruleId)
                                .eq(ScheduleRule::getUserId, userId)
                                .last("LIMIT 1")
                );

        if (scheduleRule == null) {
            throw new IllegalArgumentException(
                    "排班规则不存在或无权使用"
            );
        }

        ScheduleRuleSpecial scheduleRuleSpecial =
                scheduleRuleSpecialService.getOne(
                    new LambdaQueryWrapper<ScheduleRuleSpecial>()
                            .eq(ScheduleRuleSpecial::getRuleId,scheduleRule.getId())
            );

        if (scheduleRuleSpecial == null) {
            throw new IllegalStateException("特殊排班规则配置不存在");
        }

        List<ScheduleRuleSpecialDay> specialDayList  =
                scheduleRuleSpecialDayService.list(
                        new LambdaQueryWrapper<ScheduleRuleSpecialDay>()
                                .eq(ScheduleRuleSpecialDay::getRuleId, scheduleRule.getId())
                                .orderByAsc(ScheduleRuleSpecialDay::getDayOffset)
                );

        List<ScheduleRuleSpecialRotation> specialRotationList =
                scheduleRuleSpecialRotationService.list(
                        new LambdaQueryWrapper<ScheduleRuleSpecialRotation>()
                                .eq(ScheduleRuleSpecialRotation::getRuleId, scheduleRule.getId())
                                .orderByAsc(ScheduleRuleSpecialRotation::getSortNumber)
                );

        ScheduleSpecialRuleConfig specialRuleConfig = new ScheduleSpecialRuleConfig();

        specialRuleConfig.setCycleDays(scheduleRuleSpecial.getCycleDays());

        specialRuleConfig.setSpecialDaysList(specialDayList);

        specialRuleConfig.setSpecialRotations(specialRotationList);

        /**
         * LocalDate startDate, //排班开始日期
         * Integer initialRotationIndex, //轮次初始
         * int totalDays,  //生成天数
         * ScheduleSpecialRuleConfig specialRuleConfig
         */
        return ShiftScheduler.generateSpecialMonthSchedule(
                startDate,
                initialRotationIndex,
                totalDays,
                specialRuleConfig
        ) ;
    }

    private List<ScheduleRow> calculateScheduleNormalRows(
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
        ScheduleRuleNormal scheduleRuleNormal =
                scheduleRuleNormalService.getOne(
                        new LambdaQueryWrapper<ScheduleRuleNormal>()
                                .eq(ScheduleRuleNormal::getRuleId, scheduleRule.getId()));


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

        config.setRestDays(scheduleRuleNormal.getRestDays());

        config.setWorkDays(scheduleRuleNormal.getWorkDays());

        config.setRotateOnRestDay(
                Byte.valueOf((byte) 1).equals(scheduleRuleNormal.getRotateOnRestDay()));

        config.setShiftOrderList(shiftOrderList);

        config.setRotationDaySet(rotationDaySet);

        return ShiftScheduler.generateMonthSchedule(
                startDate,
                initialShift,
                totalDays,
                config
        );
    }
}
