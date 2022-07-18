package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

/**
 * Coordinates in a spherical coordinate system
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
abstract class SphericalCoordinates {
    private final double longitude, latitude;

    /**
     * Constructs new {@code SphericalCoordinates}
     *
     * @param longitude longitude in radians
     * @param latitude  latitude in radians
     */
    SphericalCoordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Gives the longitude in radians
     *
     * @return longitude in radians
     */
    double lon() {
        return longitude;
    }

    /**
     * Gives the longitude in degrees
     *
     * @return longitude in degrees
     */
    double lonDeg() {
        return Angle.toDeg(longitude);
    }

    /**
     * Gives the latitude in radians
     *
     * @return latitude in radians
     */
    double lat() {
        return latitude;
    }

    /**
     * Gives the latitude in degrees
     *
     * @return latitude in degrees
     */
    double latDeg() {
        return Angle.toDeg(latitude);
    }

    /**
     * Method disabled, because of the {@code equals} method being disabled
     *
     * @throws UnsupportedOperationException will throw an exception if called
     * @see ch.epfl.rigel.math.Interval#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * Method disabled, because floating point errors make it difficult to compare different intervals.
     *
     * @throws UnsupportedOperationException will throw an exception if called
     * @see ch.epfl.rigel.math.Interval#equals(Object)
     */
    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
}
