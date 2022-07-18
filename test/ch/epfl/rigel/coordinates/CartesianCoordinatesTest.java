package ch.epfl.rigel.coordinates;

import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class CartesianCoordinatesTest {
    @Test
    void coordinatesWork() {
        SplittableRandom r = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; ++i) {
            double x = r.nextDouble(-1e6, 1e6);
            double y = r.nextDouble(-1e6, 1e6);
            CartesianCoordinates c = CartesianCoordinates.of(x, y);
            Assertions.assertEquals(c.x(), x, 1e-9);
            Assertions.assertEquals(c.y(), y, 1e-9);
        }
    }

    @Test
    void throwsUnsupportedOperationException() {
        CartesianCoordinates coordinates = CartesianCoordinates.of(0, 0);
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> coordinates.equals(CartesianCoordinates.of(0, 0)));
        Assertions.assertThrows(UnsupportedOperationException.class, coordinates::hashCode);
    }
}
