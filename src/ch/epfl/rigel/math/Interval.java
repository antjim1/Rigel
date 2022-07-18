package ch.epfl.rigel.math;

/**
 * Interval as a parent class for closed interval and semi-open interval on the right
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public abstract class Interval {
    private final double low;  // lower bound
    private final double high;  // upper bound
    private final double size;  // since the interval is immutable, its size can be cached

    /**
     * Constructs a new interval
     *
     * @param low  lower bound of the interval
     * @param high upper bound of the interval
     */
    protected Interval(double low, double high) {
        this.low = low;
        this.high = high;
        this.size = high - low;
    }

    /**
     * Getter for the lower bound
     *
     * @return lower bound of the interval
     */
    public double low() {
        return low;
    }

    /**
     * Getter for the upper bound
     *
     * @return upper bound of the interval
     */
    public double high() {
        return high;
    }

    /**
     * Getter for the interval size
     *
     * @return size of the interval
     */
    public double size() {
        return size;
    }

    /**
     * Indicates if the interval contains a certain value
     *
     * @param v value to verify
     *
     * @return boolean indicating whether the given value is in the interval
     */
    public abstract boolean contains(double v);

    /**
     * Method disabled, because of the {@code equals} method being disabled
     *
     * @throws UnsupportedOperationException will throw an exception if called
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * Method disabled, because floating point errors make it difficult to compare different intervals.
     *
     * @throws UnsupportedOperationException will throw an exception if called
     */
    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
}
