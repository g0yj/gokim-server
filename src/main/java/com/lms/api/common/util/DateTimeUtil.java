package com.lms.api.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 오늘이면 시간, 아니면 날짜로 반환
     */
    public static String formatConditionalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";

        LocalDate today = LocalDate.now();
        LocalDate target = dateTime.toLocalDate();

        boolean isToday = today.isEqual(target);

        return isToday
                ? dateTime.format(TIME_FORMATTER)
                : dateTime.format(DATE_FORMATTER);
    }
}
