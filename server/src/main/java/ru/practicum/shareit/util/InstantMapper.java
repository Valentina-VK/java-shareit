package ru.practicum.shareit.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class InstantMapper {
    public static Instant mapStringToInstant(String date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.DATE_PATTERN);
        LocalDateTime localDateTime = LocalDateTime.parse(date, dateTimeFormatter);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return zonedDateTime.toInstant();
    }

    public static String mapInstantToString(Instant instant) {
        if (instant == null) {
            return null;
        }
        return DateTimeFormatter
                .ofPattern(Constants.DATE_PATTERN)
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }
}