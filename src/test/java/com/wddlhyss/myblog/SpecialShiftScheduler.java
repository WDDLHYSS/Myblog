package com.wddlhyss.myblog;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecialShiftScheduler {

    /**
     * =========================
     * 实际显示班次
     * =========================
     */
    public enum ShiftType {

        DAY("白班"),

        FOUR("四点"),

        BIG_FOUR("大四点"),

        ZERO("零点"),

        REST("休息");

        private final String description;

        ShiftType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }


    /**
     * =========================
     * 外层轮次类型
     * =========================
     */
    public enum RotationType {

        BIG_FOUR("大班"),

        DAY("白班");

        private final String description;

        RotationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }


    /**
     * =========================
     * 周期日类型
     * =========================
     */
    public enum DayType {

        WORK,

        REST
    }


    /**
     * =========================
     * 模拟 schedule_rule_special
     * =========================
     */
    public record SpecialRule(

            int cycleDays
    ) {
    }


    /**
     * =========================
     * 模拟 schedule_rule_special_rotation
     *
     * BIG_FOUR × 2
     * DAY × 1
     * =========================
     */
    public record RotationConfig(

            RotationType rotationType,

            int repeatCount,

            int sortNumber
    ) {
    }


    /**
     * =========================
     * 模拟 schedule_rule_special_day
     * =========================
     */
    public record DayConfig(

            int dayOffset,

            DayType dayType
    ) {
    }


    /**
     * 展开后的轮次
     *
     * BIG_FOUR × 2
     * DAY × 1
     *
     * =>
     *
     * 0 BIG_FOUR
     * 1 BIG_FOUR
     * 2 DAY
     */
    public record ExpandedRotation(

            int rotationIndex,

            RotationType rotationType
    ) {
    }


    /**
     * =========================
     * 最终每天的排班结果
     * =========================
     */
    public record ScheduleRow(

            LocalDate date,

            String weekName,

            /**
             * 第几个完整cycle
             */
            int cycleIndex,

            /**
             * 当前cycle中的位置
             */
            int dayOffset,

            /**
             * 外层轮次位置
             */
            int rotationIndex,

            /**
             * 大班 / 白班
             */
            RotationType rotationType,

            /**
             * WORK / REST
             */
            DayType dayType,

            /**
             * 实际显示班次
             */
            ShiftType shiftType
    ) {

        public boolean working() {

            return dayType == DayType.WORK;
        }
    }


    /**
     * =========================
     * 核心算法
     * =========================
     *
     * startDate 对应 dayOffset = 0
     */
    public static List<ScheduleRow> generateSchedule(

            LocalDate startDate,

            int totalDays,

            int initialRotationIndex,

            SpecialRule specialRule,

            List<RotationConfig> rotationConfigList,

            List<DayConfig> dayConfigList
    ) {

        validate(

                startDate,

                totalDays,

                initialRotationIndex,

                specialRule,

                rotationConfigList,

                dayConfigList
        );


        /**
         * 展开：
         *
         * BIG_FOUR × 2
         * DAY × 1
         *
         * =>
         *
         * BIG_FOUR
         * BIG_FOUR
         * DAY
         */
        List<ExpandedRotation> rotationList =
                expandRotationList(
                        rotationConfigList
                );


        /**
         * dayOffset -> WORK / REST
         */
        Map<Integer, DayType> dayTypeMap =
                buildDayTypeMap(
                        specialRule.cycleDays(),
                        dayConfigList
                );


        List<ScheduleRow> result =
                new ArrayList<>();


        for (
                int diffDays = 0;
                diffDays < totalDays;
                diffDays++
        ) {

            LocalDate currentDate =
                    startDate.plusDays(
                            diffDays
                    );


            /**
             * 当前属于第几个完整周期
             */
            int cycleIndex =
                    Math.floorDiv(

                            diffDays,

                            specialRule.cycleDays()
                    );


            /**
             * 当前在周期中的位置
             */
            int dayOffset =
                    Math.floorMod(

                            diffDays,

                            specialRule.cycleDays()
                    );


            /**
             * 当前外层轮次
             */
            int rotationIndex =
                    Math.floorMod(

                            initialRotationIndex
                                    + cycleIndex,

                            rotationList.size()
                    );


            ExpandedRotation rotation =
                    rotationList.get(
                            rotationIndex
                    );


            DayType dayType =
                    dayTypeMap.get(
                            dayOffset
                    );


            /**
             * 根据：
             *
             * 大班 / 白班
             * +
             * WORK / REST模板
             *
             * 推导真实班次
             */
            ShiftType shiftType =
                    resolveShiftType(

                            rotation.rotationType(),

                            dayOffset,

                            specialRule.cycleDays(),

                            dayTypeMap
                    );


            result.add(

                    new ScheduleRow(

                            currentDate,

                            getChineseWeekName(
                                    currentDate.getDayOfWeek()
                            ),

                            cycleIndex,

                            dayOffset,

                            rotationIndex,

                            rotation.rotationType(),

                            dayType,

                            shiftType
                    )
            );
        }


        return result;
    }


    /**
     * =========================
     * 真实班次解释器
     * =========================
     */
    private static ShiftType resolveShiftType(

            RotationType rotationType,

            int dayOffset,

            int cycleDays,

            Map<Integer, DayType> dayTypeMap
    ) {

        DayType currentDayType =
                dayTypeMap.get(
                        dayOffset
                );


        /**
         * =========================
         * 白班轮
         * =========================
         *
         * WORK -> 白班
         *
         * REST -> 休息
         */
        if (
                rotationType == RotationType.DAY
        ) {

            return currentDayType == DayType.WORK
                    ? ShiftType.DAY
                    : ShiftType.REST;
        }


        /**
         * =========================
         * 大班轮
         * =========================
         */
        if (
                rotationType == RotationType.BIG_FOUR
        ) {

            /**
             * WORK
             */
            if (
                    currentDayType == DayType.WORK
            ) {

                /**
                 * 连续WORK段第一天
                 *
                 * 四点
                 */
                if (
                        isFirstWorkDay(

                                dayOffset,

                                cycleDays,

                                dayTypeMap
                        )
                ) {

                    return ShiftType.FOUR;
                }


                /**
                 * WORK段后续
                 *
                 * 大四点
                 */
                return ShiftType.BIG_FOUR;
            }


            /**
             * REST
             *
             * 如果前一天是WORK：
             *
             * 今天虽然属于REST，
             * 但显示前一天大四点留下的零点尾班。
             */
            if (
                    isFirstRestDay(

                            dayOffset,

                            cycleDays,

                            dayTypeMap
                    )
            ) {

                return ShiftType.ZERO;
            }


            /**
             * 普通休息日
             */
            return ShiftType.REST;
        }


        throw new IllegalStateException(
                "未知轮次类型：" + rotationType
        );
    }


    /**
     * 连续WORK段第一天
     */
    private static boolean isFirstWorkDay(

            int dayOffset,

            int cycleDays,

            Map<Integer, DayType> dayTypeMap
    ) {

        DayType current =
                dayTypeMap.get(
                        dayOffset
                );


        if (
                current != DayType.WORK
        ) {

            return false;
        }


        int previousOffset =
                Math.floorMod(

                        dayOffset - 1,

                        cycleDays
                );


        DayType previous =
                dayTypeMap.get(
                        previousOffset
                );


        return previous != DayType.WORK;
    }


    /**
     * 连续REST段第一天
     *
     * 前一天WORK
     * 今天REST
     *
     * =>
     *
     * 对大班来说今天显示ZERO
     */
    private static boolean isFirstRestDay(

            int dayOffset,

            int cycleDays,

            Map<Integer, DayType> dayTypeMap
    ) {

        DayType current =
                dayTypeMap.get(
                        dayOffset
                );


        if (
                current != DayType.REST
        ) {

            return false;
        }


        int previousOffset =
                Math.floorMod(

                        dayOffset - 1,

                        cycleDays
                );


        DayType previous =
                dayTypeMap.get(
                        previousOffset
                );


        return previous == DayType.WORK;
    }


    /**
     * =========================
     * 展开轮次
     * =========================
     */
    private static List<ExpandedRotation> expandRotationList(

            List<RotationConfig> configList
    ) {

        List<RotationConfig> sortedList =
                configList.stream()
                        .sorted(
                                Comparator.comparingInt(
                                        RotationConfig::sortNumber
                                )
                        )
                        .toList();


        List<ExpandedRotation> result =
                new ArrayList<>();


        int rotationIndex = 0;


        for (
                RotationConfig config
                : sortedList
        ) {

            for (
                    int i = 0;
                    i < config.repeatCount();
                    i++
            ) {

                result.add(

                        new ExpandedRotation(

                                rotationIndex,

                                config.rotationType()
                        )
                );


                rotationIndex++;
            }
        }


        System.out.println(result.toString());
        return result;
    }


    /**
     * =========================
     * 构建周期模板
     * =========================
     */
    private static Map<Integer, DayType> buildDayTypeMap(

            int cycleDays,

            List<DayConfig> dayConfigList
    ) {

        Map<Integer, DayType> result =
                new HashMap<>();


        for (
                DayConfig config
                : dayConfigList
        ) {

            if (
                    config.dayOffset() < 0 ||
                    config.dayOffset() >= cycleDays
            ) {

                throw new IllegalArgumentException(
                        "dayOffset超出cycleDays："
                                + config.dayOffset()
                );
            }


            if (
                    result.containsKey(
                            config.dayOffset()
                    )
            ) {

                throw new IllegalArgumentException(
                        "dayOffset重复："
                                + config.dayOffset()
                );
            }


            result.put(

                    config.dayOffset(),

                    config.dayType()
            );
        }


        /**
         * 要求0~cycleDays-1全部配置
         */
        for (
                int offset = 0;
                offset < cycleDays;
                offset++
        ) {

            if (
                    !result.containsKey(
                            offset
                    )
            ) {

                throw new IllegalArgumentException(
                        "缺少dayOffset配置："
                                + offset
                );
            }
        }


        return result;
    }


    /**
     * =========================
     * 参数校验
     * =========================
     */
    private static void validate(

            LocalDate startDate,

            int totalDays,

            int initialRotationIndex,

            SpecialRule specialRule,

            List<RotationConfig> rotationConfigList,

            List<DayConfig> dayConfigList
    ) {

        if (
                startDate == null
        ) {

            throw new IllegalArgumentException(
                    "startDate不能为空"
            );
        }


        if (
                totalDays <= 0
        ) {

            throw new IllegalArgumentException(
                    "totalDays必须大于0"
            );
        }


        if (
                specialRule == null ||
                specialRule.cycleDays() <= 0
        ) {

            throw new IllegalArgumentException(
                    "cycleDays必须大于0"
            );
        }


        if (
                rotationConfigList == null ||
                rotationConfigList.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "rotationList不能为空"
            );
        }


        if (
                dayConfigList == null ||
                dayConfigList.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "dayList不能为空"
            );
        }


        int rotationCount =
                rotationConfigList.stream()
                        .mapToInt(
                                RotationConfig::repeatCount
                        )
                        .sum();


        if (
                initialRotationIndex < 0 ||
                initialRotationIndex >= rotationCount
        ) {

            throw new IllegalArgumentException(
                    "initialRotationIndex非法"
            );
        }
    }


    /**
     * =========================
     * 打印
     * =========================
     */
    public static void printSchedule(

            List<ScheduleRow> rows
    ) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd"
                );


        int lastCycleIndex =
                -1;


        for (
                ScheduleRow row
                : rows
        ) {

            if (
                    row.cycleIndex()
                            != lastCycleIndex
            ) {

                lastCycleIndex =
                        row.cycleIndex();


                System.out.println();

                System.out.println(
                        "================================================================"
                );


                System.out.printf(

                        "周期=%d | rotation=%d | %s%n",

                        row.cycleIndex(),

                        row.rotationIndex(),

                        row.rotationType()
                                .getDescription()
                );


                System.out.println(
                        "================================================================"
                );
            }


            System.out.printf(

                    "%s %-3s | offset=%02d | %-4s | %-8s%n",

                    row.date()
                            .format(
                                    formatter
                            ),

                    row.weekName(),

                    row.dayOffset(),

                    row.dayType(),

                    row.shiftType()
                            .getDescription()
            );
        }
    }


    /**
     * 星期
     */
    private static String getChineseWeekName(

            DayOfWeek dayOfWeek
    ) {

        return switch (
                dayOfWeek
        ) {

            case MONDAY -> "周一";

            case TUESDAY -> "周二";

            case WEDNESDAY -> "周三";

            case THURSDAY -> "周四";

            case FRIDAY -> "周五";

            case SATURDAY -> "周六";

            case SUNDAY -> "周日";
        };
    }


    /**
     * =========================
     * 测试
     * =========================
     */
    public static void main(
            String[] args
    ) {

        /**
         * --------------------------------------------------
         * 1. SPECIAL规则
         *
         * 这只是当前测试规则。
         *
         * 算法本身完全不知道21是什么意思。
         * --------------------------------------------------
         */
        SpecialRule specialRule =
                new SpecialRule(
                        21
                );


        /**
         * --------------------------------------------------
         * 2. 外层倒班
         *
         * BIG_FOUR × 2
         * DAY × 1
         *
         * =>
         *
         * 0 大班
         * 1 大班
         * 2 白班
         * --------------------------------------------------
         */
        List<RotationConfig> rotationList =
                List.of(

                        new RotationConfig(
                                RotationType.BIG_FOUR,
                                2,
                                1
                        ),

                        new RotationConfig(
                                RotationType.DAY,
                                1,
                                2
                        )
                );


        /**
         * --------------------------------------------------
         * 3. 21天周期模板
         *
         * 大班解释以后：
         *
         * 0  WORK -> FOUR
         * 1  WORK -> BIG_FOUR
         * 2  WORK -> BIG_FOUR
         * 3  REST -> ZERO
         *
         * 4~9 REST
         *
         * 10 WORK -> FOUR
         * 11 WORK -> BIG_FOUR
         * 12 WORK -> BIG_FOUR
         * 13 WORK -> BIG_FOUR
         * 14 REST -> ZERO
         *
         * 15~20 REST
         *
         *
         * 白班解释以后：
         *
         * 所有WORK -> DAY
         * 所有REST -> REST
         * --------------------------------------------------
         */
        List<DayConfig> dayList =
                List.of(

                        new DayConfig(
                                0,
                                DayType.WORK
                        ),

                        new DayConfig(
                                1,
                                DayType.WORK
                        ),

                        new DayConfig(
                                2,
                                DayType.WORK
                        ),

                        new DayConfig(
                                3,
                                DayType.REST
                        ),

                        new DayConfig(
                                4,
                                DayType.REST
                        ),

                        new DayConfig(
                                5,
                                DayType.REST
                        ),

                        new DayConfig(
                                6,
                                DayType.REST
                        ),

                        new DayConfig(
                                7,
                                DayType.REST
                        ),

                        new DayConfig(
                                8,
                                DayType.REST
                        ),

                        new DayConfig(
                                9,
                                DayType.REST
                        ),

                        new DayConfig(
                                10,
                                DayType.WORK
                        ),

                        new DayConfig(
                                11,
                                DayType.WORK
                        ),

                        new DayConfig(
                                12,
                                DayType.WORK
                        ),

                        new DayConfig(
                                13,
                                DayType.WORK
                        ),

                        new DayConfig(
                                14,
                                DayType.REST
                        ),

                        new DayConfig(
                                15,
                                DayType.REST
                        ),

                        new DayConfig(
                                16,
                                DayType.REST
                        ),

                        new DayConfig(
                                17,
                                DayType.REST
                        ),

                        new DayConfig(
                                18,
                                DayType.REST
                        ),

                        new DayConfig(
                                19,
                                DayType.REST
                        ),

                        new DayConfig(
                                20,
                                DayType.REST
                        )
                );


        /**
         * startDate就是offset=0。
         *
         * 这里先用周一测试。
         *
         * 以后换成周二一样可以，
         * 算法不关心星期几。
         */
        LocalDate startDate =
                LocalDate.of(
                        2026,
                        9,
                        21
                );


        /**
         * 0 = 第一个大班轮
         */
        int initialRotationIndex =
                0;


        /**
         * 跑4个周期：
         *
         * 大班
         * 大班
         * 白班
         * 大班
         */
        int totalDays =
                21 * 4;


        List<ScheduleRow> rows =
                generateSchedule(

                        startDate,

                        totalDays,

                        initialRotationIndex,

                        specialRule,

                        rotationList,

                        dayList
                );


        printSchedule(
                rows
        );
    }
}