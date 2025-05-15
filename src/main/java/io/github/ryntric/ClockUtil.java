package io.github.ryntric;

import java.time.Clock;

public final class ClockUtil {
    private static final Clock clock = Clock.systemUTC();

    private ClockUtil() {}

    public static long inMillis() {
        return clock.millis();
    }

    public static long diffMillis(long millis) {
        return clock.millis() - millis;
    }

}
