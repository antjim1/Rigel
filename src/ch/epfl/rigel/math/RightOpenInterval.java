package ch.epfl.rigel.math;

import java.util.Locale;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Right open interval, i.e [a, b[, with a, b real numbers, a < b
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class RightOpenInterval extends Interval {
    /**
     * Constructs a new {@code RightOpenInterval} with the given bounds
     *
     * @param low  lower bound of the interval
     * @param high upper bound of the interval
     */
    private RightOpenInterval(double low, double high) {
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
    public static RightOpenInterval of(double low, double high) {
        checkArgument(low < high);
        return new RightOpenInterval(low, high);
    }

    /**
     * Constructs a new {@code ClosedInterval} of the given size and centered on 0
     *
     * @param size size of the interval to construct
     *
     * @return new {@code ClosedInterval} of the given size and centered on 0
     */
    public static RightOpenInterval symmetric(double size) {
        checkArgument(size > 0);
        double bound = size / 2;
        return new RightOpenInterval(-bound, bound);
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
        return low() <= v && high() > v;
    }

    /**
     * Returns {@code a + floorMod(v - a, b - a)}, assuming the interval is {@code [a, b[} and the given value is {@code
     * v}.
     *
     * @param v provided value
     *
     * @return {@code a + floorMod(v - a, b - a)}
     */
    public double reduce(double v) {
        double x = v - low();  // v - a
        double y = size();  // b - a

        double floorMod = x - y * Math.floor(x / y);  // floorMod(x, y)

        return low() + floorMod;  // a + floorMod(x, y)
    }

    /**
     * Gives the String representation of the interval
     *
     * @return {@code String} representing the interval
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%.2f,%.2f[", low(), high());
    }
}
