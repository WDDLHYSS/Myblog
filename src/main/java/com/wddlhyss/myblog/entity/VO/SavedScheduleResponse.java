package com.wddlhyss.myblog.entity.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedScheduleResponse {

    /**
     * 当前规则是否已经生成过排班。
     */
    private Boolean exists;

    private Long ruleId;

    private LocalDate startDate;

    //NORMAL
    private String initialShift;

    //SPECIAL
    private Integer getInitialRotationIndex;

    private Integer totalDays;

    private List<ScheduleRow> scheduleRows;
}