package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

import java.util.Locale;

/**
 * Facilitates argument validation
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class Preconditions {
    /**
     * This class is not meant to be instantiated
     */
    private Preconditions() {
    }

    /**
     * Does nothing if the given requirement is met, else throws an {@code IllegalArgumentException}.
     *
     * @param isTrue requirement that must be met
     *
     * @throws IllegalArgumentException if the argument is {@code false}
     */
    public static void checkArgument(boolean isTrue) {
        if (!isTrue) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Verifies if the given value is in the given interval. If it is, returns the provided value, else throws an {@code
     * IllegalArgumentException}.
     *
     * @param interval interval in which the value should be contained
     * @param value    value that should be contained in the interval
     *
     * @return the provided value if it is contained in the interval
     *
     * @throws IllegalArgumentException if the given value is not in the given interval
     */
    public static double checkInInterval(Interval interval, double value) {
        if (interval.contains(value)) return value;
        throw new IllegalArgumentException(String.format(Locale.ROOT, "Value %f not in %s", value, interval));
    }
}
