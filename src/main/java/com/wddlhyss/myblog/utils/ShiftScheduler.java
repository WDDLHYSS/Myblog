package com.wddlhyss.myblog.utils;

import com.wddlhyss.myblog.entity.BO.ScheduleSpecialRuleConfig;
import com.wddlhyss.myblog.entity.ScheduleRuleShiftOrder;
import com.wddlhyss.myblog.entity.ScheduleRuleSpecialDay;
import com.wddlhyss.myblog.entity.ScheduleRuleSpecialRotation;
import com.wddlhyss.myblog.entity.VO.ScheduleRow;
import com.wddlhyss.myblog.entity.BO.ScheduleRuleConfig;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;


/**
 * 排班规则计算
 */
public class ShiftScheduler {

    /**
     * NORMAL排班计算
     * @param startDate
     * @param initialShift
     * @param totalDays
     * @param scheduleRuleConfig
     * @return
     */
    public static List<ScheduleRow> generateMonthSchedule(
            LocalDate startDate, //排班开始日期
            String initialShift, //初始班次
            int totalDays,  //生成天数
            ScheduleRuleConfig scheduleRuleConfig //倒班规则
    ) {

        if (startDate == null) {
            throw new IllegalArgumentException("排班开始日期不能为空");
        }

        if (initialShift == null || initialShift.isBlank()) {
            throw new IllegalArgumentException("初始班次不能为空");
        }

        if (scheduleRuleConfig == null) {
            throw new IllegalArgumentException("排班规则不能为空");
        }

        if (totalDays <= 0) {
            throw new IllegalArgumentException("生成天数必须大于0");
        }

        if (scheduleRuleConfig.getWorkDays() == null
                || scheduleRuleConfig.getWorkDays() <= 0) {
            throw new IllegalArgumentException("上班天数必须大于0");
        }

        if (scheduleRuleConfig.getRestDays() == null
                || scheduleRuleConfig.getRestDays() < 0) {
            throw new IllegalArgumentException("休息天数不能小于0");
        }

        if (scheduleRuleConfig.getShiftOrderList() == null
                || scheduleRuleConfig.getShiftOrderList().isEmpty()) {
            throw new IllegalArgumentException("班次轮换顺序不能为空");
        }
        List<ScheduleRow> result = new ArrayList<>();

        String currentShift = initialShift;

        int cycleLength =
                scheduleRuleConfig.getWorkDays()
                        + scheduleRuleConfig.getRestDays();//获取一个总的上班循环周期

        for (int dayIndex = 0; dayIndex < totalDays; dayIndex++) {
            // 根据开始日期和天数下标计算当前日期
            LocalDate currentDate = startDate.plusDays(dayIndex);

            /*
             * 当前日期位于“上班天数 + 休息天数”
             * 这个完整周期中的第几天。
             */
            int cycleDay = dayIndex % cycleLength;

            /*
             * 周期前 workDays 天属于上班日。
             */
            boolean working =
                    cycleDay < scheduleRuleConfig.getWorkDays();

            /*
             * DayOfWeek.getValue()：
             * 周一为1，周日为7。
             */
            int dayOfWeek =
                    currentDate.getDayOfWeek().getValue();

            /*
             * 判断当天是否属于规则设置的倒班日。
             */
            boolean rotationDay =
                    scheduleRuleConfig.getRotationDaySet() != null
                            && scheduleRuleConfig
                            .getRotationDaySet()
                            .contains(dayOfWeek);

            /*
             * 是否允许休息期间继续倒班。
             */
            boolean rotateOnRestDay =
                    Boolean.TRUE.equals(
                            scheduleRuleConfig.getRotateOnRestDay()
                    );

            /*
             * 当天是倒班日，并且：
             * 1. 当天是上班日；
             * 2. 或者规则允许休息日倒班。
             */
            boolean shouldRotate =
                    rotationDay
                            && (working || rotateOnRestDay);

            /*
             * 先倒班，再记录当天班次。
             */
            if (shouldRotate) {
                currentShift = getNextShiftCode(
                        currentShift,
                        scheduleRuleConfig.getShiftOrderList()
                );
            }

            /*
             * 上班日显示实际班次。
             * 休息日的班次编码请使用你项目中实际定义的休息编码。
             */
            String displayShift = working
                    ? currentShift
                    : "REST";

            result.add(
                    new ScheduleRow(
                            currentDate,
                            currentDate.getDayOfWeek().getValue(),
                            working,
                            displayShift
                    )
            );
        }


        return result;
    }

    private static String getNextShiftCode(
            String currentShift,
            List<ScheduleRuleShiftOrder> shiftOrderList
    ) {
        for (int index = 0; index < shiftOrderList.size(); index++) {

            ScheduleRuleShiftOrder shiftOrder =
                    shiftOrderList.get(index);

            /*
             * 找到当前班次在集合中的位置。
             */
            if (currentShift.equals(shiftOrder.getShiftCode())) {

                /*
                 * 计算下一个班次的位置。
                 *
                 * 如果当前已经是最后一个班次，
                 * 通过取模重新回到第一个班次。
                 */
                int nextIndex =
                        (index + 1) % shiftOrderList.size();

                return shiftOrderList
                        .get(nextIndex)
                        .getShiftCode();
            }
        }

        throw new IllegalArgumentException(
                "当前班次不在班次轮换顺序中：" + currentShift
        );
    }


    /**
     * SPECIAL排班计算
     */

    public static List<ScheduleRow> generateSpecialMonthSchedule(
            LocalDate startDate, //排班开始日期
            Integer initialRotationIndex, //轮次初始
            int totalDays,  //生成天数
            ScheduleSpecialRuleConfig specialRuleConfig //倒班规则
    ) {
        if (startDate == null) {
            throw new IllegalArgumentException("排班开始日期不能为空");
        }

        if (initialRotationIndex == null || initialRotationIndex < 0) {
            throw new IllegalArgumentException("初始轮次不能为空且不能小于0");
        }

        if (specialRuleConfig == null) {
            throw new IllegalArgumentException("排班规则不能为空");
        }

        if (totalDays <= 0) {
            throw new IllegalArgumentException("生成天数必须大于0");
        }

        if (specialRuleConfig.getCycleDays() == null
                || specialRuleConfig.getCycleDays() <= 0) {

            throw new IllegalArgumentException("单轮周期天数必须大于0");
        }

        if (specialRuleConfig.getSpecialRotations() == null
                || specialRuleConfig.getSpecialRotations().isEmpty()) {

            throw new IllegalArgumentException("大轮轮次数据不能为空");
        }

        if (specialRuleConfig.getSpecialDaysList() == null
                || specialRuleConfig.getSpecialDaysList().isEmpty()) {

            throw new IllegalArgumentException("轮次中每日排班安排不能为空");
        }

        int cycleDays = specialRuleConfig.getCycleDays();

        /**
         * =========================
         * 1. 展开外层轮次(先排序再展开)
         * BIG_FOUR × 2           BIG_FOUR
         *              =>        BIG_FOUR
         * DAY × 1                DAY
         * =========================
         */
        List<ScheduleRuleSpecialRotation> rotationList =
                specialRuleConfig
                        .getSpecialRotations()
                        .stream()
                        .sorted(Comparator.comparingInt(
                                ScheduleRuleSpecialRotation::getSortNumber
                        ))
                        .toList();

        List<String> expandedRotations = new ArrayList<>();

        for (ScheduleRuleSpecialRotation rotation : rotationList) {

            Integer repeatCountValue = rotation.getRepeatCount();

            int repeatCount = repeatCountValue == null ? 1 : repeatCountValue;

            if (repeatCount <= 0) {
                throw new IllegalArgumentException("轮次repeatCount必须大于0");
            }

            for (int i = 0; i < repeatCount; i++) {

                expandedRotations.add(rotation.getShiftCode());
            }
        }

        if (expandedRotations.isEmpty()) {
            throw new IllegalArgumentException("展开后的轮次不能为空");
        }

        if (initialRotationIndex >= expandedRotations.size()) {
            throw new IllegalArgumentException("初始轮次超出范围，当前轮次数量：" + expandedRotations.size());
        }

        /**
         * =========================
         * 2. 构建每天的WORK / REST模板
         * key: dayOffset
         * value: WORK / REST
         * =========================
         */

        Map<Integer,String> dayTypeMap = new HashMap<>();

        for (ScheduleRuleSpecialDay specialDay : specialRuleConfig.getSpecialDaysList()) {

            Integer dayOffset = specialDay.getDayOffset();

            if (dayOffset == null) {
                throw new IllegalArgumentException("dayOffset不能为空");
            }

            if (dayOffset < 0 || dayOffset >= cycleDays) {
                throw new IllegalArgumentException("dayOffset超出周期范围：" + dayOffset);
            }


            if (dayTypeMap.containsKey(dayOffset)) {
                throw new IllegalArgumentException("dayOffset重复：" + dayOffset);
            }

            dayTypeMap.put(dayOffset, specialDay.getDayType());
        }

        for (int dayOffset = 0; dayOffset < cycleDays; dayOffset++) {

            if (!dayTypeMap.containsKey(dayOffset)) {
                throw new IllegalArgumentException( "缺少dayOffset配置：" + dayOffset);
            }
        }

        List<ScheduleRow> result = new ArrayList<>(totalDays);

        for (int dayIndex = 0; dayIndex < totalDays; dayIndex++) {

            //当前日期
            LocalDate currentDate = startDate.plusDays(dayIndex);

            //当前是第几个cycle（向下取整取商）
            int cycleIndex = Math.floorDiv(dayIndex, cycleDays);

            //当前在cycle里的位置(取余)
            int dayOffset =Math.floorMod(dayIndex, cycleDays);

            //当前属于哪个大轮轮次
            int rotationIndex = Math.floorMod(initialRotationIndex + cycleIndex, expandedRotations.size());

            //获取轮次名称
            String rotationCode = expandedRotations.get(rotationIndex);

            //获取班次类型
            String dayType = dayTypeMap.get(dayOffset);

            //是否属于工作日  (ZERO当天 dayType = REST ---> working = false 但displayShift会是ZERO)
            boolean working = "WORK".equals(dayType);

            String displayShift =
                    resolveSpecialShift(
                            rotationCode,
                            dayOffset,
                            cycleDays,
                            dayTypeMap
                    );

            ScheduleRow scheduleRow = new ScheduleRow(currentDate , currentDate.getDayOfWeek().getValue(), working, displayShift);

            result.add(scheduleRow);
        }

        return result;
    }

    /**
     * SPECIAL排班班次解析
     */
    private static String resolveSpecialShift(
            String rotationCode,
            int dayOffset,
            int cycleDays,
            Map<Integer, String> dayTypeMap
    ) {

        String currentDayType =
                dayTypeMap.get(dayOffset);

        /*
         * =========================
         * 白班轮
         * =========================
         *
         * 白班轮根本不参与ZERO判断
         */
        if ("DAY".equals(rotationCode)) {

            if ("WORK".equals(currentDayType)) {
                return "DAY";
            }

            return "REST";
        }


        /*
         * =========================
         * 大班轮
         * =========================
         */
        if ("BIG_FOUR".equals(rotationCode)) {

            int previousOffset =
                    Math.floorMod(
                            dayOffset - 1,
                            cycleDays
                    );

            String previousDayType =
                    dayTypeMap.get(previousOffset);


            /*
             * REST -> WORK
             *
             * 工作段第一天
             */
            if ("WORK".equals(currentDayType)
                    && !"WORK".equals(previousDayType)) {

                return "FOUR";
            }


            /*
             * WORK -> WORK
             */
            if ("WORK".equals(currentDayType)
                    && "WORK".equals(previousDayType)) {

                return "BIG_FOUR";
            }


            /*
             * WORK -> REST
             *
             * 只有这种状态才能出现ZERO
             */
            if ("REST".equals(currentDayType)
                    && "WORK".equals(previousDayType)) {

                return "ZERO";
            }


            /*
             * REST -> REST
             */
            return "REST";
        }


        throw new IllegalArgumentException(
                "未知特殊轮次类型：" + rotationCode
        );
    }
}




