package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.internationalization.Translations;

import java.util.Objects;

/**
 * Represents the sun
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class Sun extends CelestialObject {
    private static final FloatAttribute MAGNITUDE = FloatAttribute.magnitude(-26.7f);
    private final EclipticCoordinates eclipticPos;
    private final float meanAnomaly;

    /**
     * Constructs a new instance of {@code Sun}
     *
     * @param eclipticPos   position of the sun in ecliptic coordinates - not {@code null}
     * @param equatorialPos position of the sun in equatorial coordinates
     * @param angularSize   angular size of the sun
     * @param meanAnomaly   mean anomaly
     *
     * @throws NullPointerException if the ecliptic position is {@code null}
     */
    public Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos,
               float angularSize, float meanAnomaly) {
        super(Translations.SUN_NAME, equatorialPos,
              FloatAttribute.angularSize(angularSize),
              MAGNITUDE);
        Objects.requireNonNull(eclipticPos);
        this.eclipticPos = eclipticPos;
        this.meanAnomaly = meanAnomaly;
    }

    /**
     * Gives the position of the instance in ecliptic coordinates
     *
     * @return position of the instance in ecliptic coordinates
     */
    public EclipticCoordinates eclipticPos() {
        return eclipticPos;
    }

    /**
     * Gives the mean anomaly
     *
     * @return mean anomaly
     */
    public double meanAnomaly() {
        return meanAnomaly;
    }

    /**
     * Gives the object type
     *
     * @return the object type
     */
    @Override
    public CelestialObjectType type() {  // BONUS MODIFICATION: helps determining the type of an object
        return CelestialObjectType.SUN;
    }

    /**
     * Gives the object identifier.
     *
     * @return object identifier
     */
    @Override
    public CelestialObjectIdentifier identifier() {  // BONUS MODIFICATION: helps keeping track of objects
        return CelestialObjectIdentifier.SUN;
    }
}
