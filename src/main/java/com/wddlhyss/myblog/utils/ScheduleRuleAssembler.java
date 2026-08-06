package com.wddlhyss.myblog.utils;

import com.wddlhyss.myblog.entity.*;
import com.wddlhyss.myblog.entity.VO.*;

import java.util.ArrayList;
import java.util.List;

public class ScheduleRuleAssembler {

    private ScheduleRuleAssembler() {
    }

    /**
     * 针对数据库byte与代码boolean的转换
     * @param value
     * @return
     */
    public static Boolean toBoolean(Byte value) {

        if (value == null) {
            return null;
        }

        return value != 0;
    }

    /**
     * 反向转换Boolean -> Byte
     * @param value
     * @return
     */
    public static Byte toByte(Boolean value) {

        if (value == null) {
            return null;
        }

        return value ? (byte) 1 : (byte) 0;
    }

    /**
     * 公共字段
     * @param rule
     * @return
     */
    public static ScheduleRuleResponse buildBase(
            ScheduleRule rule
    ) {

        ScheduleRuleResponse response =
                new ScheduleRuleResponse();

        response.setRuleId(rule.getId());

        response.setRuleName(rule.getRuleName());

        response.setRuleType(rule.getRuleType());

        response.setEnabled(toBoolean(rule.getEnabled()));

        response.setCreateTime(rule.getCreateTime());

        response.setUpdateTime(rule.getUpdateTime());

        return response;
    }

    /**
     * nomal规则
     * @param rule
     * @param normal
     * @param shiftOrderList
     * @param rotationDayList
     * @return
     */
    public static ScheduleRuleResponse buildNormal(
            ScheduleRule rule,
            ScheduleRuleNormal normal,
            List<ScheduleRuleShiftOrder> shiftOrderList,
            List<Integer> rotationDayList) {

        ScheduleRuleResponse response = buildBase(rule);

        ScheduleRuleNormalResponse normalResponse = new ScheduleRuleNormalResponse();

        normalResponse.setWorkDays(normal.getWorkDays());

        normalResponse.setRestDays(normal.getRestDays());

        normalResponse.setRotateOnRestDay(toBoolean(normal.getRotateOnRestDay()));

        normalResponse.setShiftOrderList(shiftOrderList);

        normalResponse.setRotationDayList(rotationDayList);

        response.setNormalRule(normalResponse);

        return response;
    }

    public static ScheduleRuleResponse buildSpecial(
            ScheduleRule rule,
            ScheduleRuleSpecial special,
            List<ScheduleRuleSpecialRotation> rotationList,
            List<ScheduleRuleSpecialDay> dayList
    ) {

        /*
         * 先组装公共字段
         */
        ScheduleRuleResponse response =
                buildBase(rule);


        /*
         * SPECIAL详情
         */
        ScheduleRuleSpecialResponse specialResponse =
                new ScheduleRuleSpecialResponse();


        /*
         * 周期天数
         */
        if (special != null) {
            specialResponse.setCycleDays(
                    special.getCycleDays()
            );
        }


        /*
         * 倒班循环
         *
         * 例如：
         * BIG_FOUR × 2
         * DAY × 1
         */
        List<SpecialRotationResponse> rotationResponseList =
                new ArrayList<>();

        if (rotationList != null) {

            for (
                    ScheduleRuleSpecialRotation item :
                    rotationList
            ) {

                SpecialRotationResponse rotationResponse =
                        new SpecialRotationResponse();

                rotationResponse.setShiftCode(
                        item.getShiftCode()
                );

                rotationResponse.setRepeatCount(
                        item.getRepeatCount()
                );

                rotationResponse.setSortNumber(
                        item.getSortNumber()
                );

                rotationResponseList.add(
                        rotationResponse
                );
            }
        }


        /*
         * 周期日期
         *
         * 例如：
         * offset 0 -> WORK
         * offset 1 -> WORK
         * offset 4 -> REST
         */
        List<SpecialDayResponse> dayResponseList =
                new ArrayList<>();

        if (dayList != null) {

            for (
                    ScheduleRuleSpecialDay item :
                    dayList
            ) {

                SpecialDayResponse dayResponse =
                        new SpecialDayResponse();

                dayResponse.setDayOffset(
                        item.getDayOffset()
                );

                dayResponse.setDayType(
                        item.getDayType()
                );

                dayResponseList.add(
                        dayResponse
                );
            }
        }


        specialResponse.setRotationList(
                rotationResponseList
        );

        specialResponse.setDayList(
                dayResponseList
        );


        /*
         * 放进总Response
         */
        response.setSpecialRule(
                specialResponse
        );


        return response;
    }




}