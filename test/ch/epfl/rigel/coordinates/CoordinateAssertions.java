package ch.epfl.rigel.coordinates;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class CoordinateAssertions {
    public static void assertEquals2(SphericalCoordinates expected, SphericalCoordinates actual, double precision) {
        assertEquals(expected.lon(), actual.lon(), precision);
        assertEquals(expected.lat(), actual.lat(), precision);
    }

    public static void assertEquals2(CartesianCoordinates expected, CartesianCoordinates actual, double precision) {
        assertEquals(expected.x(), actual.x(), precision);
        assertEquals(expected.y(), actual.y(), precision);
    }

    public static void assertEquals2(double expected, double actual, double precision) {
        assertEquals(expected, actual, precision);
    }


}
