package ch.epfl.rigel.coordinates;

import java.util.Locale;

/**
 * Cartesian coordinates - used to specify the position of a point in a plane
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class CartesianCoordinates {
    private final double x, y;

    private CartesianCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs new cartesian coordinates
     *
     * @param x x-coordinate
     * @param y y-coordinate
     *
     * @return new instance of {@code CartesianCoordinates}
     */
    public static CartesianCoordinates of(double x, double y) {
        return new CartesianCoordinates(x, y);
    }

    /**
     * Getter for property 'x'
     *
     * @return Value of property 'x'
     */
    public double x() {
        return x;
    }

    /**
     * Getter for property 'y'
     *
     * @return Value of property 'y'
     */
    public double y() {
        return y;
    }

    /**
     * Gives the text representation of the instance
     *
     * @return text representation of the instance
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(x=%.4f, y=%.4f)", x, y);
    }

    /**
     * Method disabled
     *
     * @throws UnsupportedOperationException will throw an exception if called
     * @see ch.epfl.rigel.math.Interval#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * Method disabled
     *
     * @throws UnsupportedOperationException will throw an exception if called
     * @see ch.epfl.rigel.math.Interval#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
}
