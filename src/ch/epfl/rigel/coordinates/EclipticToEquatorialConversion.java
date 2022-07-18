package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static ch.epfl.rigel.astronomy.Epoch.J2000;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Converts {@code EclipticCoordinates} to {@code EquatorialCoordinates}
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {
    private static final Polynomial ECLIPTIC_OBLIQUITY_POLYNOMIAL = Polynomial.of(Angle.ofArcsec(0.00181),
                                                                                  Angle.ofArcsec(-0.0006),
                                                                                  Angle.ofArcsec(-46.815),
                                                                                  Angle.ofDMS(23, 26, 21.45));
    private final double sinEclipticObliquity;
    private final double cosEclipticObliquity;

    /**
     * Calculates the ecliptic obliquity (epsilon)
     *
     * @param when Date and time of the area to convert
     */
    public EclipticToEquatorialConversion(ZonedDateTime when) {
        double T = J2000.julianCenturiesUntil(when);
        double eclipticObliquity = ECLIPTIC_OBLIQUITY_POLYNOMIAL.at(T);
        sinEclipticObliquity = sin(eclipticObliquity);
        cosEclipticObliquity = cos(eclipticObliquity);
    }

    /**
     * Transforms ecliptic coordinates to equatorial coordinates
     *
     * @param ecl The ecliptic coordinates
     *
     * @return Equatorial coordinates of {@code ecl}
     */
    @Override
    public EquatorialCoordinates apply(EclipticCoordinates ecl) {
        double sinLon = sin(ecl.lon());
        double sinLat = sin(ecl.lat());
        double cosLat = cos(ecl.lat());
        double tanLat = sinLat / cosLat;  /* faster than computing tan and using sin = tan * cos (tested with 1e8
                                             iterations, on only one machine) */

        double alpha = atan2((sinLon * cosEclipticObliquity - tanLat * sinEclipticObliquity), cos(ecl.lon()));
        alpha = Angle.normalizePositive(alpha);
        double delta = asin(sinLat * cosEclipticObliquity + cosLat * sinEclipticObliquity * sinLon);

        return EquatorialCoordinates.of(alpha, delta);
    }

    /**
     * Method disabled
     *
     * @throws UnsupportedOperationException will throw an exception if called
     * @see ch.epfl.rigel.math.Interval#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * Method disabled
     *
     * @throws UnsupportedOperationException will throw an exception if called
     * @see ch.epfl.rigel.math.Interval#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
}
