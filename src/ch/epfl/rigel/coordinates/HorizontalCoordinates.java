package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;

/**
 * Coordinates in the horizontal (a.k.a. topocentric) coordinate system
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class HorizontalCoordinates extends SphericalCoordinates {
    private static final RightOpenInterval AZIMUTH_INTERVAL = RightOpenInterval.of(0.0, Angle.TAU);
    private static final ClosedInterval ALTITUDE_INTERVAL = ClosedInterval.symmetric(Angle.TAU / 2.0);

    /**
     * Constructs new {@code HorizontalCoordinates}
     *
     * @param longitude longitude in radians
     * @param latitude  latitude in radians
     */
    HorizontalCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }

    /**
     * Constructs new {@code HorizontalCoordinates}
     *
     * @param az  azimuth in radians
     * @param alt altitude in radians
     *
     * @return new {@code HorizontalCoordinates}
     *
     * @throws IllegalArgumentException if the azimuth (resp. the altitude) is not in [0, TAU[ (resp. [-PI/2, PI/2])
     */
    public static HorizontalCoordinates of(double az, double alt) {
        Preconditions.checkInInterval(AZIMUTH_INTERVAL, az);
        Preconditions.checkInInterval(ALTITUDE_INTERVAL, alt);
        return new HorizontalCoordinates(az, alt);
    }

    /**
     * Constructs new {@code HorizontalCoordinates}
     *
     * @param azDeg  azimuth in degrees
     * @param altDeg altitude in degrees
     *
     * @return new {@code HorizontalCoordinates}
     *
     * @throws IllegalArgumentException if the azimuth or the altitude is not in [0°, 360°[, resp. [-90°, 90°]
     */
    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {
        double az = Angle.ofDeg(azDeg);
        double alt = Angle.ofDeg(altDeg);
        Preconditions.checkInInterval(AZIMUTH_INTERVAL, az);
        Preconditions.checkInInterval(ALTITUDE_INTERVAL, alt);
        return new HorizontalCoordinates(az, alt);
    }

    /**
     * Gives the azimuth in radians
     *
     * @return azimuth in radians
     */
    public double az() {
        return super.lon();
    }

    /**
     * Gives the azimuth in degrees
     *
     * @return azimuth in degrees
     */
    public double azDeg() {
        return super.lonDeg();
    }

    /**
     * Gives the name of the octant best approximating the azimuth
     *
     * @param n text representation for the north
     * @param e text representation for the east
     * @param s text representation for the south
     * @param w text representation for the west
     *
     * @return approximation of the azimuth as an octant name
     */
    public String azOctantName(String n, String e, String s, String w) {
        String[] cardinalPoints = {n, n + e, e, s + e, s, s + w, w, n + w};
        int l = cardinalPoints.length;
        double radPerOctant = Angle.TAU / l;
        int i = (int) round(az() / radPerOctant) % l;
        return cardinalPoints[i];
    }

    /**
     * Gives the altitude in radians
     *
     * @return altitude in radians
     */
    public double alt() {
        return super.lat();
    }

    /**
     * Gives the altitude in degrees
     *
     * @return altitude in degrees
     */
    public double altDeg() {
        return super.latDeg();
    }

    /**
     * Computes the angular distance between the provided coordinates and the current ones
     *
     * @param that coordinates of the other object
     *
     * @return angular distance between the provided coordinates and the current ones
     */
    public double angularDistanceTo(HorizontalCoordinates that) {
        // this
        double alt1 = alt();
        double az1 = az();

        // that
        double alt2 = that.alt();
        double az2 = that.az();

        return acos(sin(alt1) * sin(alt2) + cos(alt1) * cos(alt2) * cos(az1 - az2));
    }

    /**
     * Gives the text representation of the coordinates
     *
     * @return {@code String} representing the coordinates
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", azDeg(), altDeg());
    }
}
