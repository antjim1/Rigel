package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

/**
 * Coordinates in the equatorial coordinate system
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class EquatorialCoordinates extends SphericalCoordinates {
    private static final RightOpenInterval RA_INTERVAL = RightOpenInterval.of(0.0, Angle.TAU);
    private static final ClosedInterval DEC_INTERVAL = ClosedInterval.symmetric(Angle.TAU / 2.0);

    /**
     * Constructs new {@code EquatorialCoordinates}
     *
     * @param longitude longitude in radians
     * @param latitude  latitude in radians
     */
    private EquatorialCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }

    /**
     * Constructs new {@code EquatorialCoordinates}
     *
     * @param ra  right ascension in radians
     * @param dec declination in radians
     *
     * @return {@code EquatorialCoordinates}
     *
     * @throws IllegalArgumentException if the right ascension (resp. declination) is not in [0, TAU[ (resp. [-PI/2,
     *                                  PI/2])
     */
    public static EquatorialCoordinates of(double ra, double dec) {
        Preconditions.checkInInterval(RA_INTERVAL, ra);
        Preconditions.checkInInterval(DEC_INTERVAL, dec);
        return new EquatorialCoordinates(ra, dec);
    }

    /**
     * Gives the right ascension in radians
     *
     * @return right ascension in radians
     */
    public double ra() {
        return super.lon();
    }

    /**
     * Gives the right ascension in degrees
     *
     * @return right ascension in degrees
     */
    public double raDeg() {
        return super.lonDeg();
    }

    /**
     * Gives the right ascension in hours
     *
     * @return right ascension in hours
     */
    public double raHr() {
        return Angle.toHr(ra());
    }

    /**
     * Gives the declination in radians
     *
     * @return declination in radians
     */
    public double dec() {
        return super.lat();
    }

    /**
     * Gives the declination in degrees
     *
     * @return declination in degrees
     */
    public double decDeg() {
        return super.latDeg();
    }

    /**
     * Gives the text representation of the coordinates
     *
     * @return {@code String} representing the coordinates
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(ra=%.4fh, dec=%.4f°)", raHr(), decDeg());
    }
}
