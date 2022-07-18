package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

/**
 * Coordinates in the ecliptic coordinate system
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class EclipticCoordinates extends SphericalCoordinates {
    private static final RightOpenInterval LONGITUDE_INTERVAL = RightOpenInterval.of(0.0, Angle.TAU);
    private static final ClosedInterval LATITUDE_INTERVAL = ClosedInterval.symmetric(Angle.TAU / 2.0);

    /**
     * Constructs new {@code EclipticCoordinates}
     *
     * @param longitude longitude in radians
     * @param latitude  latitude in radians
     */
    EclipticCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }

    /**
     * Constructs new {@code EclipticCoordinates}
     *
     * @param lon longitude in radians
     * @param lat latitude in radians
     *
     * @return {@code EclipticCoordinates}
     *
     * @throws IllegalArgumentException if the longitude (resp. latitude) is not in [0, TAU[ (resp. [-PI/2, PI/2])
     */
    public static EclipticCoordinates of(double lon, double lat) {
        Preconditions.checkInInterval(LONGITUDE_INTERVAL, lon);
        Preconditions.checkInInterval(LATITUDE_INTERVAL, lat);
        return new EclipticCoordinates(lon, lat);
    }

    /**
     * Gives the longitude in radians
     *
     * @return longitude in radians
     */
    @Override
    public double lon() {
        return super.lon();
    }

    /**
     * Gives the longitude in degrees
     *
     * @return latitude in degrees
     */
    @Override
    public double lonDeg() {
        return super.lonDeg();
    }

    /**
     * Gives the latitude in radians
     *
     * @return latitude in radians
     */
    @Override
    public double lat() {
        return super.lat();
    }

    /**
     * Gives the latitude in degrees
     *
     * @return latitude in degrees
     */
    @Override
    public double latDeg() {
        return super.latDeg();
    }

    /**
     * Gives the text representation of the coordinates
     *
     * @return {@code String} representing the coordinates
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(λ=%.4f°, β=%.4f°)", lonDeg(), latDeg());
    }
}
