package ch.epfl.rigel.math;


import ch.epfl.rigel.Preconditions;

import static java.lang.Math.PI;

/**
 * Helper class to perform operations on angles (e.g. conversion, normalization)
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class Angle {
    /**
     * Full circle in radians
     */
    public static final double TAU = 2 * PI;
    private static final RightOpenInterval FULL_TURN = RightOpenInterval.of(0, TAU);

    private final static double DEG_RAD = TAU / 360;
    private final static double RAD_DEG = 360 / TAU;
    private final static double HR_RAD = TAU / 24;
    private final static double RAD_HR = 24 / TAU;
    private final static double MIN_RAD = TAU / (360 * 60);
    private final static double SEC_RAD = TAU / (360 * 3600);
    private static final RightOpenInterval _0_TO_60_INTERVAL = RightOpenInterval.of(0, 60); /* underscore
                                                                                        because of naming limitations */

    /**
     * This class is not meant to be instantiated
     */
    private Angle() {
    }

    /**
     * Removes extra turns from an angle
     *
     * @param rad angle to normalize
     *
     * @return provided angle without the unnecessary turns
     */
    public static double normalizePositive(double rad) {
        return FULL_TURN.reduce(rad);
    }

    /**
     * Converts the given angle from seconds to radians
     *
     * @param sec angle in seconds
     *
     * @return given angle in radians
     */
    public static double ofArcsec(double sec) {
        return sec * SEC_RAD;
    }


    /**
     * Converts the given angle from degree, minutes, seconds to radians
     *
     * @param deg integer number of degrees
     * @param min integer number of minutes
     * @param sec floating point number of seconds
     *
     * @return given angle in radians
     *
     * @throws IllegalArgumentException if the seconds or the minutes are not in [0, 60[ or the degrees are negative
     */
    public static double ofDMS(int deg, int min, double sec) {
        Preconditions.checkInInterval(_0_TO_60_INTERVAL, min);
        Preconditions.checkInInterval(_0_TO_60_INTERVAL, sec);
        Preconditions.checkArgument(deg >= 0);
        return deg * DEG_RAD + min * MIN_RAD + sec * SEC_RAD;
    }

    /**
     * Converts the given angle from degrees to radians
     *
     * @param deg angle in degrees
     *
     * @return given angle in radians
     */
    public static double ofDeg(double deg) {
        return deg * DEG_RAD;
    }

    /**
     * Converts the given angle from radians to degrees
     *
     * @param rad angle in radians
     *
     * @return given angle in degrees
     */
    public static double toDeg(double rad) {
        return rad * RAD_DEG;
    }

    /**
     * Converts the given angle from hours to radians
     *
     * @param hr angle in hours
     *
     * @return given angle in radians
     */
    public static double ofHr(double hr) {
        return hr * HR_RAD;
    }

    /**
     * Converts the given angle from radians to hours
     *
     * @param rad angle in radians
     *
     * @return given angle in hours
     */
    public static double toHr(double rad) {
        return rad * RAD_HR;
    }
}
