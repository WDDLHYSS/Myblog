package com.wddlhyss.myblog;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ShiftScheduler {

    /**
     * 班次类型
     */
    public enum ShiftType {

        DAY("白班"),
        FOUR("四点"),
        ZERO("零点"),
        REST("休息");

        private final String description;

        ShiftType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 周日倒班规则：
         * 白班 -> 零点
         * 零点 -> 四点
         * 四点 -> 白班
         */
        public ShiftType nextShift() {
            return switch (this) {
                case DAY -> ZERO;
                case ZERO -> FOUR;
                case FOUR -> DAY;
                case REST -> throw new IllegalStateException("休息状态不能直接倒班");
            };
        }
    }

    /**
     * 周日倒班方式
     */
    public enum RotationRule {

        /**
         * 每个自然周日都倒班，包括休息周期中的周日
         */
        EVERY_SUNDAY,

        /**
         * 只有上班周期中的周日才倒班
         */
        WORKING_SUNDAY_ONLY
    }

    /**
     * 一天的排班结果
     */
    public record ScheduleRow(
            LocalDate date,
            String weekName,
            boolean working,
            ShiftType shiftType
    ) {
    }

    /**
     * 生成排班表。
     *
     * @param firstWorkThursday 第一轮上班的周四日期
     * @param initialShift      第一轮周四的班次
     * @param totalDays         需要生成的总天数
     * @param rotationRule      周日倒班方式
     */
    public static List<ScheduleRow> generateSchedule(
            LocalDate firstWorkThursday,
            ShiftType initialShift,
            int totalDays,
            RotationRule rotationRule
    ) {
        if (firstWorkThursday == null) {
            throw new IllegalArgumentException("第一轮上班日期不能为空");
        }

        if (firstWorkThursday.getDayOfWeek() != DayOfWeek.THURSDAY) {
            throw new IllegalArgumentException(
                    "第一轮上班日期必须是星期四，当前日期为："
                            + firstWorkThursday
                            + "，星期"
                            + getChineseWeekName(firstWorkThursday.getDayOfWeek())
            );
        }

        if (initialShift == null || initialShift == ShiftType.REST) {
            throw new IllegalArgumentException("初始班次只能是白班、四点或零点");
        }

        if (totalDays <= 0) {
            throw new IllegalArgumentException("生成天数必须大于 0");
        }

        if (rotationRule == null) {
            throw new IllegalArgumentException("周日倒班方式不能为空");
        }

        List<ScheduleRow> scheduleRows = new ArrayList<>();

        ShiftType currentShift = initialShift;

        for (int dayIndex = 0; dayIndex < totalDays; dayIndex++) {
            LocalDate currentDate = firstWorkThursday.plusDays(dayIndex);

            /*
             * 一个周期共 14 天：
             * 第 0～6 天：上班，周四到下周三
             * 第 7～13 天：休息，周四到下周三
             */
            int cycleDay = dayIndex % 14;
            boolean working = cycleDay < 7;

            /*
             * 周日先完成倒班，再记录当天班次。
             *
             * 例如：
             * 周六白班，周日变成零点；
             * 周六四点，周日变成白班；
             * 周六零点，周日变成四点。
             */
            if (currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                boolean shouldRotate =
                        rotationRule == RotationRule.EVERY_SUNDAY || working;

                if (shouldRotate) {
                    currentShift = currentShift.nextShift();
                }
            }

            ShiftType displayShift = working
                    ? currentShift
                    : ShiftType.REST;

            scheduleRows.add(
                    new ScheduleRow(
                            currentDate,
                            getChineseWeekName(currentDate.getDayOfWeek()),
                            working,
                            displayShift
                    )
            );
        }

        return scheduleRows;
    }

    /**
     * 输出排班表
     */
    public static void printSchedule(List<ScheduleRow> scheduleRows) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd");

        System.out.printf(
                "%-12s %-6s %-6s %-6s%n",
                "日期",
                "星期",
                "状态",
                "班次"
        );

        System.out.println("--------------------------------");

        for (ScheduleRow row : scheduleRows) {
            System.out.printf(
                    "%-12s %-6s %-6s %-6s%n",
                    row.date().format(formatter),
                    row.weekName(),
                    row.working() ? "上班" : "休息",
                    row.shiftType().getDescription()
            );
        }
    }

    private static String getChineseWeekName(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "周一";
            case TUESDAY -> "周二";
            case WEDNESDAY -> "周三";
            case THURSDAY -> "周四";
            case FRIDAY -> "周五";
            case SATURDAY -> "周六";
            case SUNDAY -> "周日";
        };
    }

    public static void main(String[] args) {

        /*
         * 2026-08-06 是星期四。
         * 表示第一轮从这一天开始上班。
         */
        LocalDate firstWorkThursday =
                LocalDate.of(2026, 8, 6);

        /*
         * 第一轮周四为白班。
         */
        ShiftType initialShift = ShiftType.ZERO;

        /*
         * 生成 56 天，也就是四个“上7休7”周期。
         */
        int totalDays = 365;

        List<ScheduleRow> scheduleRows = generateSchedule(
                firstWorkThursday,
                initialShift,
                totalDays,

                /*
                 * 按“每个周日都倒班”处理。
                 */
                RotationRule.EVERY_SUNDAY
        );

        printSchedule(scheduleRows);
    }
}