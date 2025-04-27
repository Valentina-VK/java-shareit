package ru.practicum.shareit;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class TestConstant {
    public static final long NOT_EXISTING_ID = 1000L;

    public static final long NOT_OWNER_ID = 13L;
    public static final Instant TIME_BEFORE = Instant.now()
            .minus(Duration.ofDays(2))
            .truncatedTo(ChronoUnit.SECONDS);
    public static final Instant TIME_AFTER = Instant.now()
            .plus(Duration.ofDays(2))
            .truncatedTo(ChronoUnit.SECONDS);
}