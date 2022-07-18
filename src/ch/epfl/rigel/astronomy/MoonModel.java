package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Mathematical model able to approximate different characteristics of the moon at different moments in time.
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public enum MoonModel implements CelestialObjectModel<Moon> {
    MOON;

    private static final double AVERAGE_LONGITUDE = Angle.ofDeg(91.929336);
    private static final double AVERAGE_LONGITUDE_AT_PERIGEE = Angle.ofDeg(130.143076);
    private static final double ASCENDING_NODE_LONGITUDE = Angle.ofDeg(291.682547);
    private static final double ORBIT_INCLINATION = Angle.ofDeg(5.145396);
    private static final double ORBIT_ECCENTRICITY = 0.0549;

    private double averageOrbitalLongitude(double daysSinceJ2010) {
        return Angle.ofDeg(13.1763966) * daysSinceJ2010 + AVERAGE_LONGITUDE;
    }

    private double meanAnomaly(double daysSinceJ2010, double averageOrbitalLongitude) {
        return averageOrbitalLongitude - Angle.ofDeg(0.1114041) * daysSinceJ2010 - AVERAGE_LONGITUDE_AT_PERIGEE;
    }

    private double evection(double averageOrbitalLongitude, double meanAnomaly, double sunEclipticLongitude) {
        return Angle.ofDeg(1.2739) * sin(2 * (averageOrbitalLongitude - sunEclipticLongitude) - meanAnomaly);
    }

    private double annualEquationCorrection(double sinSunMeanAnomaly) {
        return Angle.ofDeg(0.1858) * sinSunMeanAnomaly;
    }

    private double correction3(double sinSunMeanAnomaly) {
        return Angle.ofDeg(0.37) * sinSunMeanAnomaly;
    }

    private double correctedAnomaly(double meanAnomaly, double evection,
                                    double annualEquationCorrection, double correction3) {
        return meanAnomaly + evection - annualEquationCorrection - correction3;
    }

    private double centerEquationCorrection(double correctedAnomaly) {
        return Angle.ofDeg(6.2886) * sin(correctedAnomaly);
    }

    private double correction4(double correctedAnomaly) {
        return Angle.ofDeg(0.214) * sin(2 * correctedAnomaly);
    }

    private double correctedOrbitalLongitude(double averageOrbitalLongitude, double evection,
                                             double centerEquationCorrection, double annualEquationCorrection,
                                             double correction4) {
        return averageOrbitalLongitude + evection + centerEquationCorrection - annualEquationCorrection + correction4;
    }

    private double variation(double correctedOrbitalLongitude, double sunEclipticLongitude) {
        return Angle.ofDeg(0.6583) * sin(2 * (correctedOrbitalLongitude - sunEclipticLongitude));
    }

    private double realOrbitalLongitude(double correctedOrbitalLongitude, double variation) {
        return correctedOrbitalLongitude + variation;
    }

    private double meanAscendingNodeLongitude(double daysSinceJ2010) {
        return ASCENDING_NODE_LONGITUDE - Angle.ofDeg(0.0529539) * daysSinceJ2010;
    }

    private double correctedAscendingNodeLongitude(double meanAscendingNodeLongitude, double sinSunMeanAnomaly) {
        return meanAscendingNodeLongitude - Angle.ofDeg(0.16) * sinSunMeanAnomaly;
    }

    private double eclipticLongitude(double realOrbitalLongitude, double correctedAscendingNodeLongitude, double sinX) {
        double x = realOrbitalLongitude - correctedAscendingNodeLongitude;
        double lon = atan2(sinX * cos(ORBIT_INCLINATION), cos(x)) + correctedAscendingNodeLongitude;
        return Angle.normalizePositive(lon);
    }

    private double eclipticLatitude(double sinX) {
        return asin(sinX * sin(ORBIT_INCLINATION));
    }

    private double phase(double realOrbitalLongitude, double sunEclipticLongitude) {
        return (1 - cos(realOrbitalLongitude - sunEclipticLongitude)) / 2d;
    }

    private double angularSize(double correctedAnomaly, double centerEquationCorrection) {
        double n = 1 - ORBIT_ECCENTRICITY * ORBIT_ECCENTRICITY;
        double d = 1 + ORBIT_ECCENTRICITY * cos(correctedAnomaly + centerEquationCorrection);
        double rho = n / d;

        return Angle.ofDeg(0.5181) / rho;
    }

    /**
     * Constructs a new {@code Moon} with the characteristics calculated by the model from the data given in argument
     *
     * @param daysSinceJ2010 time at which the object is observed, in days since the J2010 epoch
     * @param eclipticToEquatorialConversion conversion to use when going from ecliptic to equatorial coordinates
     *
     * @return {@code Moon} with the characteristics calculated by the model from the data given in argument
     */
    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        Sun sun = SunModel.SUN.at(daysSinceJ2010, eclipticToEquatorialConversion);
        double sunEclipticLongitude = sun.eclipticPos().lon();
        double sinSunMeanAnomaly = sin(sun.meanAnomaly());

        double averageOrbitalLongitude = averageOrbitalLongitude(daysSinceJ2010);
        double meanAnomaly = meanAnomaly(daysSinceJ2010, averageOrbitalLongitude);
        double evection = evection(averageOrbitalLongitude, meanAnomaly, sunEclipticLongitude);
        double annualEquationCorrection = annualEquationCorrection(sinSunMeanAnomaly);
        double correction3 = correction3(sinSunMeanAnomaly);
        double correctedAnomaly = correctedAnomaly(meanAnomaly, evection, annualEquationCorrection, correction3);
        double centerEquationCorrection = centerEquationCorrection(correctedAnomaly);
        double correction4 = correction4(correctedAnomaly);
        double correctedOrbitalLongitude = correctedOrbitalLongitude(averageOrbitalLongitude, evection,
                                                                     centerEquationCorrection, annualEquationCorrection,
                                                                     correction4);
        double variation = variation(correctedOrbitalLongitude, sunEclipticLongitude);
        double realOrbitalLongitude = realOrbitalLongitude(correctedOrbitalLongitude, variation);
        double meanAscendingNodeLongitude = meanAscendingNodeLongitude(daysSinceJ2010);
        double correctedAscendingNodeLongitude = correctedAscendingNodeLongitude(meanAscendingNodeLongitude,
                                                                                 sinSunMeanAnomaly);
        double angularSize = angularSize(correctedAnomaly, centerEquationCorrection);
        double phase = phase(realOrbitalLongitude, sunEclipticLongitude);

        // We avoid to calculate sin(realOrbitalLongitude - correctedAscendingNodeLongitude) twice
        double sinX = sin(realOrbitalLongitude - correctedAscendingNodeLongitude);

        double eclipticLongitude =
                eclipticLongitude(realOrbitalLongitude, correctedAscendingNodeLongitude, sinX); // first time
        double eclipticLatitude = eclipticLatitude(sinX); // second time
        EclipticCoordinates eclipticCoordinates = EclipticCoordinates.of(eclipticLongitude, eclipticLatitude);
        EquatorialCoordinates equatorialCoordinates = eclipticToEquatorialConversion.apply(eclipticCoordinates);

        return new Moon(equatorialCoordinates, (float) angularSize, (float) phase);
    }
}
