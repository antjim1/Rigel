package ch.epfl.rigel.astronomy;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents standard epochs
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public enum Epoch {
    J2000(2000, 1, 1, 12), J2010(2009, 12, 31, 0);

    private static final double MILLISECONDS_PER_DAY = 86_400_000.0;
    private static final double DAYS_PER_CENTURY = 36525.0;
    private final ZonedDateTime zonedDateTime;

    Epoch(int year, int month, int dayOfMonth, int hour) {
        zonedDateTime = ZonedDateTime.of(year, month, dayOfMonth,
                                         hour, 0, 0, 0,
                                         ZoneOffset.UTC);
    }

    /**
     * Computes the number of days separating the provided moment in time from this epoch
     *
     * @param when other time moment
     *
     * @return number of days separating he provided moment in time from this epoch
     */
    public double daysUntil(ZonedDateTime when) {
        long milliseconds = zonedDateTime.until(when, ChronoUnit.MILLIS);
        return milliseconds / MILLISECONDS_PER_DAY;
    }

    /**
     * Computes the number of julian centuries separating the provided moment in time from this epoch
     *
     * @param when other time moment
     *
     * @return number of julian centuries separating he provided moment in time from this epoch
     */
    public double julianCenturiesUntil(ZonedDateTime when) {
        return daysUntil(when) / DAYS_PER_CENTURY;
    }
}
