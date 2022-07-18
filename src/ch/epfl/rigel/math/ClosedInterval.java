package ch.epfl.rigel.math;

import java.util.Locale;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Non-trivial closed interval, i.e [a, b], with a, b real numbers, a < b
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class ClosedInterval extends Interval {
    /**
     * Constructs a new {@code ClosedInterval} with the given bounds
     *
     * @param low  lower bound of the interval
     * @param high upper bound of the interval
     */
    private ClosedInterval(double low, double high) {
        super(low, high);
    }

    /**
     * Constructs a new {@code ClosedInterval} with the given bounds
     *
     * @param low  lower bound of the interval
     * @param high upper bound of the interval
     *
     * @return new {@code ClosedInterval} with the given bounds
     */
    public static ClosedInterval of(double low, double high) {
        checkArgument(low < high);
        return new ClosedInterval(low, high);
    }

    /**
     * Constructs a new {@code ClosedInterval} of the given size and centered on 0
     *
     * @param size size of the interval to construct
     *
     * @return new {@code ClosedInterval} of the given size and centered on 0
     */
    public static ClosedInterval symmetric(double size) {
        checkArgument(size > 0);
        double bound = size / 2;
        return new ClosedInterval(-bound, bound);
    }

    /**
     * Verifies whether the interval contains a given value
     *
     * @param v value to verify
     *
     * @return boolean indicating whether the interval contains the given value
     */
    @Override
    public boolean contains(double v) {
        return low() <= v && high() >= v;
    }

    /**
     * Returns {@code v} if the given value is in the interval, else returns the closest bound.
     *
     * @param v provided value
     *
     * @return {@code v} if the given value is in the interval, else the closest bound
     */
    public double clip(double v) {
        if (v < low()) return low();
        else return Math.min(v, high());
    }

    /**
     * Gives the String representation of the interval
     *
     * @return {@code String} representing the interval
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%.2f,%.2f]", low(), high());
    }
}
