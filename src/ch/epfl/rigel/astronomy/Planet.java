package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.internationalization.Translation;

import java.util.Objects;

/**
 * Represents a planet at a certain point in time
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class Planet extends CelestialObject {
    private final CelestialObjectIdentifier identifier;

    /**
     * Constructs a new planet
     *
     * @param name          name of the planet
     * @param identifier    unique identifier
     * @param equatorialPos position of the planet in equatorial coordinates
     * @param angularSize   angular size of the planet
     * @param magnitude     apparent magnitude of the planet
     *
     * @throws NullPointerException if {@code type} is {@code null}
     */
    // BONUS MODIFICATION: addition of the name and identifier
    public Planet(Translation name, CelestialObjectIdentifier identifier,
            EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        super(name, equatorialPos,
              FloatAttribute.angularSize(angularSize),
              FloatAttribute.magnitude(magnitude));
        this.identifier = Objects.requireNonNull(identifier);
    }

    /**
     * Gives the object type
     *
     * @return the object type
     */
    @Override
    public CelestialObjectType type() {  // BONUS MODIFICATION: helps determining the type of an object
        return CelestialObjectType.PLANET;
    }

    /**
     * Gives the object identifier.
     *
     * @return object identifier
     */
    @Override
    public CelestialObjectIdentifier identifier() {  // BONUS MODIFICATION: helps keeping track of objects
        return identifier;
    }
}
