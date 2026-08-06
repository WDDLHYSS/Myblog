package com.wddlhyss.myblog;

import java.time.LocalDate;

public class testt {
    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        LocalDate localDate = today.plusDays(5);

                ;
        System.out.println(localDate.getDayOfWeek().toString());
    }
}
