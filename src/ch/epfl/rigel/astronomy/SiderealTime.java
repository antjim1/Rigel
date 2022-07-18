package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static ch.epfl.rigel.astronomy.Epoch.J2000;

/**
 * Performs solar to sidereal time conversions
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class SiderealTime {
    private static final Polynomial s0 = Polynomial.of(0.000025862, 2400.051336, 6.697374558);
    private static final Polynomial s1 = Polynomial.of(1.002737909, 0);
    private static final double HOURS_PER_MILLISECOND = 1d / (60d * 60d * 1000d);

    private SiderealTime() {
    }


    /**
     * Computes the sidereal time of Greenwich
     *
     * @param when other time moment
     *
     * @return Greenwich sidereal time in radians.
     */
    public static double greenwich(ZonedDateTime when) {
        ZonedDateTime whenInUTC = when.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime whenTruncatedToDays = whenInUTC.truncatedTo(ChronoUnit.DAYS);

        double T = J2000.julianCenturiesUntil(whenTruncatedToDays); // Difference between J2000 and when in centuries
        double t = whenTruncatedToDays.until(whenInUTC, ChronoUnit.MILLIS) * HOURS_PER_MILLISECOND;

        double timeInHr = s0.at(T) + s1.at(t);
        double timeInRAD = Angle.ofHr(timeInHr);
        return Angle.normalizePositive(timeInRAD);
    }

    /**
     * Computes the sidereal time of a geographic position
     *
     * @param when  other time moment
     * @param where other geographic coordinates
     *
     * @return Time of Greenwich in sidereal time in radians.
     */
    public static double local(ZonedDateTime when, GeographicCoordinates where) {
        return Angle.normalizePositive(greenwich(when) + where.lon());
    }
}
