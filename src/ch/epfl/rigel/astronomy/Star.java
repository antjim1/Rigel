package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.internationalization.Translation;
import ch.epfl.rigel.math.ClosedInterval;

/**
 * Represents a star
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class Star extends CelestialObject {
    private static final ClosedInterval COLOR_INDEX_INTERVAL = ClosedInterval.of(-0.5, 5.5);

    private final int hipparcosId;
    private final int colorTemperature;

    /**
     * Constructs a new {@code Star} with the given hipparcos identification number, name, equatorial position,
     * magnitude and color index;
     *
     * @param hipparcosId   hipparcos identification number of the star - non negative
     * @param name          name of the star
     * @param equatorialPos equatorial position of the star
     * @param magnitude     apparent magnitude of the star
     * @param colorIndex    color index of the star, number ranging from -0.5 to 5.5 (both included) - a negative number
     *                      represents blueish stars, a number close to 0  is for white stars and a number above 0 is
     *                      for yellow to red stars
     *
     * @throws IllegalArgumentException if the Hipparcos ID is negative or the color index is not in [-0.5, 5.5]
     */
    // BONUS MODIFICATION: change of the type of name
    public Star(int hipparcosId, Translation name, EquatorialCoordinates equatorialPos, float magnitude,
                float colorIndex) {
        super(name, equatorialPos, FloatAttribute.magnitude(magnitude));

        Preconditions.checkArgument(hipparcosId >= 0);
        Preconditions.checkInInterval(COLOR_INDEX_INTERVAL, colorIndex);

        this.hipparcosId = hipparcosId;
        colorTemperature = (int) (4600 * (1.0 / (0.92 * colorIndex + 1.7)        // since Kelvins are non-negative,
                                          + 1.0 / (0.92 * colorIndex + 0.62)));  // truncating is equivalent to flooring
    }

    /**
     * Gives the hipparcos identification number of the star
     *
     * @return hipparcos identification number of the star
     */
    public int hipparcosId() {
        return hipparcosId;
    }

    /**
     * Computes the approximate color temperature of the star according to its color index
     *
     * @return approximate color temperature of the star
     */
    public int colorTemperature() {
        return colorTemperature;
    }

    /**
     * Gives the object type
     *
     * @return the object type
     */
    @Override
    public CelestialObjectType type() {  // BONUS MODIFICATION: helps determining the type of an object
        return CelestialObjectType.STAR;
    }

    /**
     * Gives the object identifier.
     *
     * @return object identifier
     */
    @Override
    public CelestialObjectIdentifier identifier() {  // BONUS MODIFICATION: helps keeping track of objects
        return CelestialObjectIdentifier.STAR;
    }
}
