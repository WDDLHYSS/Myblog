package com.wddlhyss.myblog.utils;

import com.wddlhyss.myblog.entity.ScheduleRuleShiftOrder;
import com.wddlhyss.myblog.entity.VO.ScheduleRow;
import com.wddlhyss.myblog.entity.BO.ScheduleRuleConfig;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


/**
 * 排版规则计算
 */
public class ShiftScheduler {

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


}




