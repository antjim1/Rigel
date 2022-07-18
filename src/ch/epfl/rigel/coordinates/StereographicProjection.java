package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

import java.util.Locale;
import java.util.function.Function;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;

/**
 * Performs stereographic projections on horizontal and cartesian coordinates
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates> {
    private final HorizontalCoordinates center;
    private final double sinCenterLatitude, cosCenterLatitude;

    /**
     * Constructs a new instance of {@code StereographicProjection}
     *
     * @param center center of the projection
     */
    public StereographicProjection(HorizontalCoordinates center) {
        this.center = center;
        sinCenterLatitude = sin(center.lat());
        cosCenterLatitude = cos(center.lat());
    }

    /**
     * Computes the center of the circle the provided parallel is mapped to
     *
     * @param hor horizontal coordinates of any point of the parallel
     *
     * @return cartesian coordinates of the center of the circle the parallel is mapped to
     */
    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates hor) {
        double x = 0;
        double y = cosCenterLatitude / (sin(hor.alt()) + sinCenterLatitude);
        return CartesianCoordinates.of(x, y);
    }

    /**
     * Computes the radius of the circle the provided parallel is mapped to
     *
     * @param parallel horizontal coordinates of any point of the parallel
     *
     * @return radius of the circle the provided parallel is mapped to
     */
    public double circleRadiusForParallel(HorizontalCoordinates parallel) {
        double phi = parallel.lat();
        return cos(phi) / (sin(phi) + sinCenterLatitude);
    }

    /**
     * Computes the projected diameter of a sphere of the provider angular size
     *
     * @param rad angular size of the sphere
     *
     * @return projected diameter of a sphere with the given angular size
     */
    public double applyToAngle(double rad) {
        return 2 * tan(rad / 4.0);
    }

    /**
     * Maps horizontal coordinates to cartesian coordinates using a stereographic projection
     *
     * @param azAlt horizontal coordinates of the point to project
     *
     * @return cartesian coordinates of the projection of the point
     */
    @Override
    public CartesianCoordinates apply(HorizontalCoordinates azAlt) {
        final double phi = azAlt.alt();
        final double sinPhi = sin(phi);
        final double cosPhi = cos(phi);
        final double lambdaD = azAlt.az() - center.az();
        final double cosLambdaD = cos(lambdaD);

        final double d = 1.0 / (1 + sinPhi * sinCenterLatitude + cosPhi * cosCenterLatitude * cosLambdaD);

        double x = d * cosPhi * sin(lambdaD);
        double y = d * (sinPhi * cosCenterLatitude - cosPhi * sinCenterLatitude * cosLambdaD);

        return CartesianCoordinates.of(x, y);
    }

    /**
     * Computes the original horizontal coordinates of a point using the cartesian coordinates of its stereographic
     * projection
     *
     * @param xy cartesian coordinates of the projection
     *
     * @return horizontal coordinates of the point
     */
    public HorizontalCoordinates inverseApply(CartesianCoordinates xy) {
        final double x = xy.x();
        final double y = xy.y();
        final double normSquared = x * x + y * y;  // the norms cancel out, avoiding computing a square root
        final double sinCOverNorm = 2.0 / (normSquared + 1);  // sine of c, divided by the norm
        final double cosC = (1 - normSquared) / (normSquared + 1);
        double azimuth;
        double altitude;

        if (x == 0 && y == 0) {
            azimuth = center.az();
            altitude = center.lat();
        } else {
            // [1] we got rid of rho by dividing it out on each level of the fraction
            azimuth = atan2(x * sinCOverNorm, cosCenterLatitude * cosC - y * sinCenterLatitude * sinCOverNorm)
                      + center.az();
            altitude = asin(cosC * sinCenterLatitude + y * sinCOverNorm * cosCenterLatitude);  // same as [1]
        }
        return HorizontalCoordinates.of(Angle.normalizePositive(azimuth), altitude);
    }

    /**
     * Gives the text representation of the instance
     *
     * @return text representation of the instance
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "StereographicProjection: c=(%.4f°, %.4f°)", center.azDeg(), center.altDeg());
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
