package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.internationalization.Translation;
import ch.epfl.rigel.internationalization.Translations;
import ch.epfl.rigel.math.ClosedInterval;

import java.util.Locale;
import java.util.function.DoubleConsumer;

/**
 * Immutable class representing an attribute storing a {@code float}. The type determines what kind of value the
 * attribute is storing.
 *
 * @implNote This class uses memoization for performance and memory usage reasons, but is nonetheless immutable when
 * observed from the outside.
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class FloatAttribute {  // BONUS MODIFICATION: allows us to display object characteristics without showing
                                     //                     default values
    private static final ClosedInterval PHASE_INTERVAL = ClosedInterval.of(0, 1);
    private static final DoubleConsumer NO_PRECONDITIONS = value -> {};
    private final Type type;
    private final float value;
    private Translation text;

    private FloatAttribute(Type type, float value) {
        this.type = type;
        this.value = type.checkValue(value);
        text = null;  // will be initialized when needed
    }

    /**
     * Creates a new instance of {@code FloatAttribute} storing the phase of an object.
     *
     * @param value value stored in the attribute
     * @return a new instance of {@code FloatAttribute} storing the phase of an object.
     * @throws IllegalArgumentException if the provided value is invalid
     */
    public static FloatAttribute phase(float value) {
        return new FloatAttribute(Type.PHASE, value);
    }

    /**
     * Creates a new instance of {@code FloatAttribute} storing the angular size of an object.
     *
     * @param value value stored in the attribute
     * @return a new instance of {@code FloatAttribute} storing the angular size of an object.
     * @throws IllegalArgumentException if the provided value is invalid
     */
    public static FloatAttribute angularSize(float value) {
        return new FloatAttribute(Type.ANGULAR_SIZE, value);
    }

    /**
     * Creates a new instance of {@code FloatAttribute} storing the magnitude of an object.
     *
     * @param value value stored in the attribute
     * @return a new instance of {@code FloatAttribute} storing the magnitude of an object.
     */
    public static FloatAttribute magnitude(float value) {
        return new FloatAttribute(Type.MAGNITUDE, value);
    }

    /**
     * Gives the translated text-representation of the attribute. This consists of the name of its type, followed by
     * a colon, a space, and its rounded value.
     *
     * @return the translated text-representation of the attribute.
     */
    public Translation text() {
        if (text == null) {  // NOTE: the text is not computed in the constructor for performance reasons
            text = type.text(value);  // guaranteed not to be null
        }
        return text;  // from now-on the text is effectively final
    }

    /**
     * Gives the value stored by the attribute.
     *
     * @return the value stored by the attribute.
     */
    public float value() {
        return value;
    }

    /**
     * Gives the type of the attribute. This defines what kind of value the attribute is storing.
     *
     * @return the type of the attribute.
     */
    public Type type() {
        return type;
    }

    /**
     * Describes the kind of value a {@code FloatAttribute} is storing.
     */
    public enum Type {
        PHASE(Translations.PHASE, (value) -> Preconditions.checkInInterval(PHASE_INTERVAL, value), 2),
        ANGULAR_SIZE(Translations.ANGULAR_SIZE, (value) -> Preconditions.checkArgument(value >= 0d), 5),
        MAGNITUDE(Translations.MAGNITUDE, NO_PRECONDITIONS, 1);

        private final Translation formattedName;
        private final DoubleConsumer preconditions;
        private final String valueFormat;

        Type(Translation name, DoubleConsumer preconditions, int precision) {
            this.formattedName = name.withColonAndSpace();
            this.preconditions = preconditions;
            Preconditions.checkArgument(precision >= 0);
            this.valueFormat = "%." + precision + "f";
        }

        /**
         * Verifies that the provided value is valid.
         *
         * @param value the value to be checked
         * @return the provided value
         * @throws IllegalArgumentException if the value is invalid
         */
        public float checkValue(float value) {
            preconditions.accept(value);
            return value;
        }

        /**
         * Gives the translated text-representation of the type with the given value.
         * This consists of the name of the type, followed by a colon, a space, and the value rounded to the predefined
         * precision.
         *
         * @param value the value to add to the text-representation
         * @return the translated text-representation of the type with the given value.
         */
        public Translation text(float value) {
            return formattedName.append(String.format(Locale.ROOT, valueFormat, value));
        }
    }
}
