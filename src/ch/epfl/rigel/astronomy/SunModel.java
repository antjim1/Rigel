package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Mathematical model able to approximate different characteristics of the sun at different moments in time.
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public enum SunModel implements CelestialObjectModel<Sun> {
    SUN;

    private static final double EPSILON_G = Angle.ofDeg(279.557208);
    private static final double OMEGA_G = Angle.ofDeg(283.112438);
    private static final double E = 0.016705;
    private static final double E_SQUARED = E * E;
    private static final double THETA_0 = Angle.ofDeg(0.533128);
    private static final double MEAN_ANGULAR_VELOCITY = Angle.TAU / 365.242191;


    private double meanAnomaly(double daysSinceJ2010) {
        return MEAN_ANGULAR_VELOCITY * daysSinceJ2010 + EPSILON_G - OMEGA_G;
    }

    private double realAnomaly(double meanAnomaly) {
        return meanAnomaly + 2 * E * sin(meanAnomaly);
    }

    private double angularSize(double realAnomaly) {
        return THETA_0 * (1 + E * cos(realAnomaly)) / (1 - E_SQUARED);
    }

    private double eclipticLongitude(double realAnomaly) {
        return realAnomaly + OMEGA_G;
    }

    /**
     * Creates a new {@code Sun} with the characteristics computed by the model at the given moment in time.
     *
     * @param daysSinceJ2010                 time at which the object is observed, in days since the J2010 epoch
     * @param eclipticToEquatorialConversion conversion to use when going from ecliptic to equatorial coordinates
     *
     * @return new {@code Sun} with the computed characteristics
     */
    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        double meanAnomaly = meanAnomaly(daysSinceJ2010);
        double realAnomaly = realAnomaly(meanAnomaly);

        double eclipticLongitude = eclipticLongitude(realAnomaly);
        eclipticLongitude = Angle.normalizePositive(eclipticLongitude);

        EclipticCoordinates eclipticCoordinates = EclipticCoordinates.of(eclipticLongitude, 0);
        EquatorialCoordinates equatorialCoordinates = eclipticToEquatorialConversion.apply(eclipticCoordinates);

        double angularSize = angularSize(realAnomaly);
        return new Sun(eclipticCoordinates, equatorialCoordinates, (float) angularSize, (float) meanAnomaly);
    }
}
