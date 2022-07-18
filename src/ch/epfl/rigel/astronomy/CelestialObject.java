package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.internationalization.Translation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Abstraction of an object that can be observed in the sky. This is an immutable class, it thus represents objects at a
 * certain point in time (i.e. in order to make an object state evolve, one has to create a new instance repeatedly)
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public abstract class CelestialObject {
    private final Translation name;
    private final EquatorialCoordinates equatorialPos;
    private final float angularSize;
    private final float magnitude;
    private final Map<FloatAttribute.Type, FloatAttribute> floatAttributesMap;

    /**
     * Constructs a new {@code CelestialObject}
     *
     * @param name            name of the object - not {@code null}
     * @param equatorialPos   position of the object in equatorial coordinates - not {@code null}
     * @param floatAttributes attributes describing different aspects of the celestial object - elements should not be
     *                        {@code null}
     *
     * @throws IllegalArgumentException if the angular size is negative
     * @throws NullPointerException     if the name, the equatorial position or a floatAttribute is {@code null}
     */
    // BONUS MODIFICATION: signature change to accept FloatAttributes
    CelestialObject(Translation name, EquatorialCoordinates equatorialPos, FloatAttribute... floatAttributes) {
        this.name = Objects.requireNonNull(name);
        this.equatorialPos = Objects.requireNonNull(equatorialPos);

        Map<FloatAttribute.Type, FloatAttribute> tmpFloatAttributesMap = new HashMap<>();
        for (FloatAttribute floatAttribute : floatAttributes) {
            tmpFloatAttributesMap.put(floatAttribute.type(), Objects.requireNonNull(floatAttribute));
        }
        this.floatAttributesMap = Collections.unmodifiableMap(tmpFloatAttributesMap);

        FloatAttribute angularSize = getAttribute(FloatAttribute.Type.ANGULAR_SIZE);
        if (angularSize == null) this.angularSize = 0f;
        else this.angularSize = angularSize.value();

        FloatAttribute magnitude = getAttribute(FloatAttribute.Type.MAGNITUDE);
        if (magnitude == null) this.magnitude = 0f;
        else this.magnitude = magnitude.value();
    }

    /**
     * Gives the name of the instance
     *
     * @return name of the instance
     */
    public String name() {
        return name.get();
    }

    /**
     * Gives the object type
     *
     * @return the object type
     */
    public abstract CelestialObjectType type();  // BONUS MODIFICATION: helps determining the type of an object

    /**
     * Gives the object identifier.
     *
     * @return the object identifier.
     */
    public abstract CelestialObjectIdentifier identifier();  // BONUS MODIFICATION: helps keeping track of objects

    /**
     * Gives the angular size of the instance
     *
     * @return angular size of the instance
     */
    public double angularSize() {
        return angularSize;
    }

    /**
     * Gives the apparent magnitude of the instance
     *
     * @return apparent magnitude of the instance
     */
    public double magnitude() {
        return magnitude;
    }

    /**
     * Gives the position of the instance in equatorial coordinates
     *
     * @return position of the instance in equatorial coordinates
     */
    public EquatorialCoordinates equatorialPos() {
        return equatorialPos;
    }

    /**
     * Gives textual information about the instance - equivalent to {@link CelestialObject#toString()} By default
     * returns the name of the object
     *
     * @return textual information about the instance
     */
    public String info() {
        return name();
    }

    /**
     * Gives the textual representation of the instance - equivalent to {@link CelestialObject#info()}
     *
     * @return textual representation of the instance
     */
    @Override
    public String toString() {
        return info();
    }

    /**
     * @param type the kind of attribute to return
     *
     * @return the {@code FloatAttribute} matching the given type, {@code null} if no match was found
     */
    public final FloatAttribute getAttribute(FloatAttribute.Type type) {  // BONUS MODIFICATION: helps displaying
        return floatAttributesMap.get(type);                              //                     object characteristics
    }
}
