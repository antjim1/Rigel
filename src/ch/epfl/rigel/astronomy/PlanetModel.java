package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.internationalization.Translation;
import ch.epfl.rigel.internationalization.Translations;
import ch.epfl.rigel.math.Angle;

import java.util.List;

import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.log10;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

/**
 * Enumeration containing mathematical models approximating different characteristics (position, magnitude, angular
 * size, ...),  of the planets of the solar system
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
// BONUS MODIFICATION: name translation and identifier
public enum PlanetModel implements CelestialObjectModel<Planet> {
    MERCURY(Translations.MERCURY_NAME, CelestialObjectIdentifier.MERCURY, 0.24085, 75.5671, 77.612, 0.205627,
            0.387098, 7.0051, 48.449, 6.74, -0.42),

    VENUS(Translations.VENUS_NAME, CelestialObjectIdentifier.VENUS, 0.615207, 272.30044, 131.54, 0.006812,
          0.723329, 3.3947, 76.769, 16.92, -4.40),

    EARTH(Translations.EARTH_NAME, CelestialObjectIdentifier.EARTH, 0.999996, 99.556772, 103.2055, 0.016671,
          0.999985, 0, 0, 0, 0),

    MARS(Translations.MARS_NAME, CelestialObjectIdentifier.MARS, 1.880765, 109.09646, 336.217, 0.093348,
         1.523689, 1.8497, 49.632, 9.36, -1.52),

    JUPITER(Translations.JUPITER_NAME, CelestialObjectIdentifier.JUPITER, 11.857911, 337.917132, 14.6633, 0.048907,
            5.20278, 1.3035, 100.595, 196.74, -9.40),

    SATURN(Translations.SATURN_NAME, CelestialObjectIdentifier.SATURN, 29.310579, 172.398316, 89.567, 0.053853,
           9.51134, 2.4873, 113.752, 165.60, -8.88),

    URANUS(Translations.URANUS_NAME, CelestialObjectIdentifier.URANUS, 84.039492, 356.135400, 172.884833, 0.046321,
           19.21814, 0.773059, 73.926961, 65.80, -7.19),

    NEPTUNE(Translations.NEPTUNE_NAME, CelestialObjectIdentifier.NEPTUNE, 165.84539, 326.895127, 23.07, 0.010483,
            30.1985, 1.7673, 131.879, 62.20, -6.87);

    /**
     * Unmodifiable list of all the values of this enum
     */
    public static final List<PlanetModel> ALL = List.of(PlanetModel.values());

    private static final double EARTH_MEAN_ANGULAR_VELOCITY = Angle.TAU / 365.242191;
    private final double longitudeAtJ2010, longitudeAtPerigee, revolutionPeriod, semiMajorAxis, orbitEccentricity,
            ascendingNodeLongitude, magnitudeAt1AU, angularSizeAt1AU;
    private final Translation name;
    private final CelestialObjectIdentifier identifier;
    private final double sinOrbitInclination;
    private final double cosOrbitInclination;

    /**
     * Constructs a new planet model for a planet with the given name in french, revolution period, longitude at J2010,
     * longitude at the perigee, orbit eccentricity, semi-major axis, orbit inclination at the ecliptic, longitude of
     * the ascending node and angular size and magnitude at a distance of 1 AU.
     *
     * @param name                   name of the planet
     * @param revolutionPeriod       revolution period of the planet
     * @param longitudeAtJ2010Deg    longitude of the planet at the epoch J2010
     * @param longitudeAtPerigeeDeg  longitude of the planet at the perigee
     * @param orbitEccentricity      eccentricity of the planet orbit
     * @param semiMajorAxis          semi-major axis of the planet orbit
     * @param orbitInclinationDeg    orbit inclination at the ecliptic
     * @param ascendingNodeLongitude longitude of the ascending node
     * @param angularSizeAt1AUArcsec angular size of the planet at a distance of 1 AU
     * @param magnitudeAt1AU         magnitude of the planet at a distance of 1 AU
     */
    PlanetModel(Translation name, CelestialObjectIdentifier identifier,
                double revolutionPeriod, double longitudeAtJ2010Deg,
                double longitudeAtPerigeeDeg, double orbitEccentricity,
                double semiMajorAxis, double orbitInclinationDeg,
                double ascendingNodeLongitude, double angularSizeAt1AUArcsec,
                double magnitudeAt1AU) {
        this.name = name;
        this.identifier = identifier;
        this.longitudeAtJ2010 = Angle.ofDeg(longitudeAtJ2010Deg);
        this.revolutionPeriod = revolutionPeriod;
        this.longitudeAtPerigee = Angle.ofDeg(longitudeAtPerigeeDeg);
        this.ascendingNodeLongitude = Angle.ofDeg(ascendingNodeLongitude);
        this.semiMajorAxis = semiMajorAxis;
        this.orbitEccentricity = orbitEccentricity;
        this.magnitudeAt1AU = magnitudeAt1AU;
        this.angularSizeAt1AU = Angle.ofArcsec(angularSizeAt1AUArcsec);

        double orbitInclination = Angle.ofDeg(orbitInclinationDeg);
        sinOrbitInclination = sin(orbitInclination);
        cosOrbitInclination = cos(orbitInclination);
    }

    private double meanAnomaly(double daysSinceJ2010) {
        return EARTH_MEAN_ANGULAR_VELOCITY * daysSinceJ2010 / revolutionPeriod + longitudeAtJ2010 - longitudeAtPerigee;
    }

    private double realAnomaly(double daysSinceJ2010) {
        double M = meanAnomaly(daysSinceJ2010);
        return M + 2 * orbitEccentricity * sin(M);
    }

    private double distanceToSun(double realAnomaly) {
        double n = semiMajorAxis * (1 - orbitEccentricity * orbitEccentricity);
        double d = 1 + orbitEccentricity * cos(realAnomaly);
        return n / d;
    }

    private double heliocentricLongitude(double realAnomaly) {
        return realAnomaly + longitudeAtPerigee;
    }

    private double heliocentricEclipticLatitude(double heliocentricLongitude) {
        return asin(sin(heliocentricLongitude - ascendingNodeLongitude) * sinOrbitInclination);
    }

    private double projectionOfTheRayOnTheEcliptic(double distanceToSun, double heliocentricEclipticLatitude) {
        return distanceToSun * cos(heliocentricEclipticLatitude);
    }

    private double heliocentricEclipticLongitude(double heliocentricLongitude) {
        double heliocentricMinusAscendingNodeLongitude = heliocentricLongitude - ascendingNodeLongitude;
        return atan2(sin(heliocentricMinusAscendingNodeLongitude) * cosOrbitInclination,
                     cos(heliocentricMinusAscendingNodeLongitude)) + ascendingNodeLongitude;
    }

    private double geocentricEclipticLongitudeLowerPlanets(double earthDistanceToSun,
                                                           double projectionOfTheRayOnTheEcliptic,
                                                           double earthHeliocentricLongitude,
                                                           double sinHeliocentricEclipticLongitudeDifference,
                                                           double cosHeliocentricEclipticLongitudeDifference) {
        double n = projectionOfTheRayOnTheEcliptic * sinHeliocentricEclipticLongitudeDifference;
        double d = earthDistanceToSun - projectionOfTheRayOnTheEcliptic * cosHeliocentricEclipticLongitudeDifference;
        return Angle.normalizePositive(PI + earthHeliocentricLongitude + atan2(n, d));
    }

    private double geocentricEclipticLongitudeHigherPlanets(double earthDistanceToSun,
                                                            double heliocentricEclipticLongitude,
                                                            double projectionOfTheRayOnTheEcliptic,
                                                            double sinHeliocentricEclipticLongitudeDifference,
                                                            double cosHeliocentricEclipticLongitudeDifference) {
        double n = earthDistanceToSun * (-sinHeliocentricEclipticLongitudeDifference);
        double d = projectionOfTheRayOnTheEcliptic - earthDistanceToSun * cosHeliocentricEclipticLongitudeDifference;
        return Angle.normalizePositive(heliocentricEclipticLongitude + atan2(n, d));
    }

    private double geocentricEclipticLatitude(double earthDistanceToSun, double heliocentricEclipticLongitude,
                                              double projectionOfTheRayOnTheEcliptic,
                                              double heliocentricEclipticLatitude,
                                              double longitude, double sinHeliocentricEclipticLongitudeDifference) {
        final double n = projectionOfTheRayOnTheEcliptic
                         * tan(heliocentricEclipticLatitude)
                         * sin(longitude - heliocentricEclipticLongitude);
        final double d = earthDistanceToSun * (-sinHeliocentricEclipticLongitudeDifference);
        return atan(n / d);
    }

    private double angularSize(double distanceToEarth) {
        return angularSizeAt1AU / distanceToEarth;
    }

    private double magnitude(double distanceToEarth, double heliocentricLongitude,
                             double distanceToSun, double longitude) {
        double phase = (1 + cos(longitude - heliocentricLongitude)) / 2;
        return magnitudeAt1AU + 5 * log10(distanceToSun * distanceToEarth / sqrt(phase));
    }

    private double distanceToEarth(double earthHeliocentricLongitude, double earthDistanceToSun,
                                   double heliocentricLongitude, double distanceToSun,
                                   double heliocentricEclipticLatitude) {
        double earthDistanceToSunSquared = earthDistanceToSun * earthDistanceToSun;
        double distanceToSunSquared = distanceToSun * distanceToSun;
        return sqrt(earthDistanceToSunSquared + distanceToSunSquared
                    - 2 * earthDistanceToSun * distanceToSun
                      * cos(heliocentricLongitude - earthHeliocentricLongitude)
                      * cos(heliocentricEclipticLatitude));
    }

    /**
     * Creates a new planet with the attributes computed by the model for the given moment in time.
     *
     * @param daysSinceJ2010                 time at which the object is observed, in days since the J2010 epoch
     * @param eclipticToEquatorialConversion conversion to use when going from ecliptic to equatorial coordinates
     *
     * @return new planet with the attributes computed by the model for the given moment in time
     *
     * @implNote To avoid the repetitive calculation of certain functions, we have stored them in a variable and given
     * them to the next functions
     */
    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        double longitude, latitude;
        double realAnomaly = realAnomaly(daysSinceJ2010);
        double heliocentricLongitude = heliocentricLongitude(realAnomaly);

        double earthRealAnomaly = EARTH.realAnomaly(daysSinceJ2010);
        double earthHeliocentricLongitude = EARTH.heliocentricLongitude(earthRealAnomaly);
        double earthDistanceToSun = EARTH.distanceToSun(earthRealAnomaly);

        double heliocentricEclipticLatitude = heliocentricEclipticLatitude(heliocentricLongitude);
        double distanceToSun = distanceToSun(realAnomaly);
        double heliocentricEclipticLongitude = heliocentricEclipticLongitude(heliocentricLongitude);
        double projectionOfTheRayOnTheEcliptic =
                projectionOfTheRayOnTheEcliptic(distanceToSun, heliocentricEclipticLatitude);

        final double heliocentricEclipticLongitudeDifference =
                earthHeliocentricLongitude - heliocentricEclipticLongitude;
        // We calculate sin(earthHeliocentricLongitude - heliocentricEclipticLongitude) just once,
        // and we apply sin(-a) = -sin(a)
        double sinHeliocentricEclipticLongitudeDifference = sin(heliocentricEclipticLongitudeDifference);
        // We calculate cos(earthHeliocentricLongitude - heliocentricEclipticLongitude) just once,
        // and we apply cos(-a) = cos(a)
        double cosHeliocentricEclipticLongitudeDifference = cos(heliocentricEclipticLongitudeDifference);

        if (earthDistanceToSun < distanceToSun) {  // if higher planet
            longitude = geocentricEclipticLongitudeHigherPlanets(earthDistanceToSun, heliocentricEclipticLongitude,
                                                                 projectionOfTheRayOnTheEcliptic,
                                                                 sinHeliocentricEclipticLongitudeDifference,
                                                                 cosHeliocentricEclipticLongitudeDifference);
        } else {
            longitude = geocentricEclipticLongitudeLowerPlanets(earthDistanceToSun, projectionOfTheRayOnTheEcliptic,
                                                                earthHeliocentricLongitude,
                                                                sinHeliocentricEclipticLongitudeDifference,
                                                                cosHeliocentricEclipticLongitudeDifference);
        }
        latitude = geocentricEclipticLatitude(earthDistanceToSun, heliocentricEclipticLongitude,
                                              projectionOfTheRayOnTheEcliptic, heliocentricEclipticLatitude, longitude,
                                              sinHeliocentricEclipticLongitudeDifference);

        EclipticCoordinates eclipticCoordinates = EclipticCoordinates.of(longitude, latitude);
        EquatorialCoordinates equatorialCoordinates = eclipticToEquatorialConversion.apply(eclipticCoordinates);

        double distanceToEarth = distanceToEarth(earthHeliocentricLongitude, earthDistanceToSun, heliocentricLongitude,
                                                 distanceToSun, heliocentricEclipticLatitude);
        double angularSize = angularSize(distanceToEarth);
        double magnitude = magnitude(distanceToEarth, heliocentricLongitude,
                                     distanceToSun, longitude);

        return new Planet(name, identifier, equatorialCoordinates, (float) angularSize, (float) magnitude);
    }
}
