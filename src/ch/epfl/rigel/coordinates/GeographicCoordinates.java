package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

/**
 * Coordinates in the geographic coordinate system
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class GeographicCoordinates extends SphericalCoordinates {
    private static final RightOpenInterval LONGITUDE_INTERVAL = RightOpenInterval.symmetric(Angle.TAU);
    private static final ClosedInterval LATITUDE_INTERVAL = ClosedInterval.symmetric(Angle.TAU / 2.0);

    /**
     * Constructs new {@code GeographicCoordinates}
     *
     * @param longitude longitude in radians
     * @param latitude  latitude in radians
     */
    private GeographicCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }

    /**
     * Constructs new {@code GeographicCoordinates}
     *
     * @param lonDeg longitude in degrees
     * @param latDeg latitude in degrees
     *
     * @throws IllegalArgumentException if the longitude (resp. latitude) is not in [-180°, 180°] (resp. [-90°, 90°])
     */
    public static GeographicCoordinates ofDeg(double lonDeg, double latDeg) {
        double lon = Angle.ofDeg(lonDeg);
        double lat = Angle.ofDeg(latDeg);
        Preconditions.checkInInterval(LONGITUDE_INTERVAL, lon);
        Preconditions.checkInInterval(LATITUDE_INTERVAL, lat);
        return new GeographicCoordinates(lon, lat);
    }

    /**
     * Indicates whether the provided longitude is valid
     *
     * @param lonDeg longitude in degrees
     * @return {@code boolean} indicating whether the longitude is valid
     */
    public static boolean isValidLonDeg(double lonDeg) {
        return LONGITUDE_INTERVAL.contains(Angle.ofDeg(lonDeg));
    }

    /**
     * Indicates whether the provided latitude is valid
     *
     * @param latDeg latitude in degrees
     * @return {@code boolean} indicating whether the latitude is valid
     */
    public static boolean isValidLatDeg(double latDeg) {
        return LATITUDE_INTERVAL.contains(Angle.ofDeg(latDeg));
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
     * @return longitude in degrees
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
        return String.format(Locale.ROOT, "(lon=%.4f°, lat=%.4f°)", lonDeg(), latDeg());
    }
}
