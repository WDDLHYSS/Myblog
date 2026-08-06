package com.wddlhyss.myblog.entity.VO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ScheduleRow {

    private LocalDate currentDate;

    private int dayOfWeek;

    private boolean working;

    private String displayShift;

    public ScheduleRow(LocalDate currentDate, int value, boolean working, String displayShift) {
        this.currentDate = currentDate;
        this.dayOfWeek = value;
        this.working = working;
        this.displayShift = displayShift;
    }
}
