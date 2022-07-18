package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.internationalization.Translations;

import java.util.Locale;

/**
 * Represents the moon at a certain point in time
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class Moon extends CelestialObject {
    private final float phase;

    /**
     * Constructs a new instance of {@code Moon}
     *
     * @param equatorialPos position of the moon in equatorial coordinates
     * @param angularSize   angular size of the moon
     * @param phase         current phase of the moon - must be in [0, 1]
     *
     * @throws IllegalArgumentException if the phase is not in [0, 1]
     */
    // BONUS MODIFICATION: removal of the magnitude
    public Moon(EquatorialCoordinates equatorialPos, float angularSize, float phase) {
        super(Translations.MOON_NAME, equatorialPos,
              FloatAttribute.angularSize(angularSize),
              FloatAttribute.phase(phase));
        this.phase = phase;
    }

    /**
     * Gives the object type
     *
     * @return the object type
     */
    @Override
    public CelestialObjectType type() {  // BONUS MODIFICATION: helps determining the type of an object
        return CelestialObjectType.MOON;
    }

    /**
     * Gives the object identifier.
     *
     * @return object identifier
     */
    @Override
    public CelestialObjectIdentifier identifier() {  // BONUS MODIFICATION: helps keeping track of objects
        return CelestialObjectIdentifier.MOON;
    }

    /**
     * Gives the name of the instance followed by the phase
     *
     * @return textual information about the instance
     */
    @Override
    public String info() {
        return String.format(Locale.ROOT, "%s (%.1f%%)", name(), phase * 100);
    }
}
