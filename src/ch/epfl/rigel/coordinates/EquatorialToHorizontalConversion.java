package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.math.Angle;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Converts {@code EquatorialCoordinates} to {@code HorizontalCoordinates}
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {
    private final double sinPhi, cosPhi, siderealLocal;

    /**
     * Establishes the hour angle (H) and the observer's latitude
     *
     * @param when  Date and time of the area to convert
     * @param where Coordinates of the area to convert
     */
    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        siderealLocal = SiderealTime.local(when, where);
        sinPhi = sin(where.lat());
        cosPhi = cos(where.lat());
    }

    /**
     * Transforms equatorial coordinates to horizontal coordinates
     *
     * @param equ The ecliptic coordinates
     * @return Horizontal coordinates of {@code equ}
     */
    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equ) {
        double sinDelta = sin(equ.dec());
        double cosDelta = cos(equ.dec());
        double H = siderealLocal - equ.ra();
        double sinH = sin(H);
        double cosH = cos(H);

        double h = asin(sinDelta * sinPhi + cosDelta * cosPhi * cosH);
        double A = atan2(-cosDelta * cosPhi * sinH, sinDelta - sinPhi * sin(h));
        A = Angle.normalizePositive(A);

        return HorizontalCoordinates.of(A, h);
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
