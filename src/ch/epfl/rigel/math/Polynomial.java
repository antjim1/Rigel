package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

/**
 * Represents polynomials
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class Polynomial {
    private final double[] coeffs;

    /**
     * Constructs a new polynomial with the given coefficients.
     *
     * @param coeffN the first coefficient of the polynomial
     * @param coeffs table with the coefficient from N-1 to the independent term
     */
    private Polynomial(double coeffN, double[] coeffs) {
        this.coeffs = new double[coeffs.length + 1];
        this.coeffs[0] = coeffN;
        System.arraycopy(coeffs, 0, this.coeffs, 1, coeffs.length);
    }

    /**
     * If the N-coefficient is different from 0 construct a new polynomial with the given coefficients
     *
     * @param coefficientN the first coefficient of the polynomial
     * @param coefficients table with the coefficient from N-1 to the independent term.
     *
     * @return a new Polynomial with the given coefficient.
     *
     * @throws IllegalArgumentException if the highest coefficient is 0
     */
    public static Polynomial of(double coefficientN, double... coefficients) {
        Preconditions.checkArgument(coefficientN != 0);
        return new Polynomial(coefficientN, coefficients);
    }

    /**
     * Computes the value of the polynomial depending on x using Horner's method
     *
     * @param x Value of the polynomial unknown
     *
     * @return the value of the polynomial as x
     */
    public double at(double x) {
        double value = 0;
        for (double coeff : coeffs) {
            value *= x;
            value += coeff;
        }
        return value;
    }

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
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gives the String representation of the polynomial
     *
     * @return The string representing the polynomial
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean beginning = true;

        for (int i = 0; i < coeffs.length; ++i) {
            int degree = coeffs.length - i - 1;
            double coefficient = coeffs[i];
            boolean wasWritten = appendCoefficient(sb, coefficient, degree, !beginning);
            if (wasWritten) beginning = false;
        }
        return sb.toString();
    }

    /**
     * Adds a coefficient to a polynomial StringBuilder
     *
     * @param stringBuilder polynomial representation to which to add the coefficient
     * @param coefficient   numeric value of the coefficient
     * @param degree        degree of the coefficient
     * @param addSign       boolean indicating whether the sign of the coefficient must be written for positive numbers
     *
     * @return boolean indicating whether anything was appended to the StringBuilder
     */
    private boolean appendCoefficient(StringBuilder stringBuilder, double coefficient, int degree, boolean addSign) {
        if (coefficient == 0) return false;

        // append (sign (+ coefficient) (+ x) (+ ^ + degree))
        if (addSign) stringBuilder.append(coefficient > 0 ? "+" : "");  // sign

        String coefficientString = "";
        if (degree > 0 && coefficient == -1) coefficientString = "-";
        else if (coefficient != 1) coefficientString = String.valueOf(coefficient);
        stringBuilder.append(coefficientString);  // coefficient

        stringBuilder.append(degree > 0 ? "x" : "");  // x
        if (degree > 1) stringBuilder.append("^").append(degree);  // power
        return true;
    }
}

