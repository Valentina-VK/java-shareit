package ru.practicum.shareit;

import java.time.Duration;
import java.time.Instant;

public final class TestConstant {
    public static final long NOT_EXISTING_ID = 100L;

    public static final long NOT_OWNER_ID = 22L;
    public static final Instant TIME_BEFORE = Instant.now().minus(Duration.ofDays(2));
    public static final Instant TIME_AFTER = Instant.now().plus(Duration.ofDays(2));
}