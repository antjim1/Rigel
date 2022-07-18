package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;

public class GeographicCoordinatesTest {
    private static final RightOpenInterval LON_INTERVAL = RightOpenInterval.symmetric(Angle.TAU);
    private static final ClosedInterval LAT_INTERVAL = ClosedInterval.symmetric(Angle.TAU / 2.0);

    @Test
    void ofDegThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(180.0, 0.0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(180.0, 91.0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(15.0, 91.0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(121, -91.0));
    }

    @Test
    void ofDegWorksOnEdgeCases() {
        Assertions.assertEquals(179.9999999, GeographicCoordinates.ofDeg(179.9999999, 90.0).lonDeg(), 1e-9);
        Assertions.assertEquals(-90.0, GeographicCoordinates.ofDeg(0.0, -90.0).latDeg(), 1e-9);
        Assertions.assertEquals(90.0, GeographicCoordinates.ofDeg(90.0, 0.0).lonDeg(), 1e-9);
        Assertions.assertEquals(90.0, GeographicCoordinates.ofDeg(-180.0, 90.0).latDeg(), 1e-9);
    }

    @Test
    void ofDegWorksOnRandomValues() {
        SplittableRandom randomGenerator = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double lonDeg = Angle.toDeg(randomGenerator.nextDouble(LON_INTERVAL.low(), LON_INTERVAL.high()));
            double latDeg = Angle.toDeg(randomGenerator.nextDouble(LAT_INTERVAL.low(), LAT_INTERVAL.high()));
            GeographicCoordinates coordinates = GeographicCoordinates.ofDeg(lonDeg, latDeg);
            Assertions.assertEquals(lonDeg, coordinates.lonDeg(), 1e-9);
            Assertions.assertEquals(latDeg, coordinates.latDeg(), 1e-9);
        }
    }

    @Test
    void isValidLonDegWorks() {
        Assertions.assertTrue(GeographicCoordinates.isValidLonDeg(0.0));
        Assertions.assertTrue(GeographicCoordinates.isValidLonDeg(179.9999));
        Assertions.assertTrue(GeographicCoordinates.isValidLonDeg(-180.0));
        Assertions.assertTrue(GeographicCoordinates.isValidLonDeg(30.0));
        Assertions.assertTrue(GeographicCoordinates.isValidLonDeg(18.33));
        Assertions.assertTrue(GeographicCoordinates.isValidLonDeg(-51.0));

        Assertions.assertFalse(GeographicCoordinates.isValidLonDeg(180.0));
        Assertions.assertFalse(GeographicCoordinates.isValidLonDeg(-300.0));
        Assertions.assertFalse(GeographicCoordinates.isValidLonDeg(180.000001));
        Assertions.assertFalse(GeographicCoordinates.isValidLonDeg(350.0));
    }

    @Test
    void isValidLatDegWorks() {
        Assertions.assertTrue(GeographicCoordinates.isValidLatDeg(0.0));
        Assertions.assertTrue(GeographicCoordinates.isValidLatDeg(90.0));
        Assertions.assertTrue(GeographicCoordinates.isValidLatDeg(-90.0));
        Assertions.assertTrue(GeographicCoordinates.isValidLatDeg(30.0));
        Assertions.assertTrue(GeographicCoordinates.isValidLatDeg(18.33));
        Assertions.assertTrue(GeographicCoordinates.isValidLatDeg(-51.0));

        Assertions.assertFalse(GeographicCoordinates.isValidLatDeg(-90.0001));
        Assertions.assertFalse(GeographicCoordinates.isValidLatDeg(-300.0));
        Assertions.assertFalse(GeographicCoordinates.isValidLatDeg(90.000001));
        Assertions.assertFalse(GeographicCoordinates.isValidLatDeg(100.0));
        Assertions.assertFalse(GeographicCoordinates.isValidLatDeg(-100.0));
    }

    @Test
    void toStringWorks() {
        GeographicCoordinates coordinates = GeographicCoordinates.ofDeg(10.997845, 18.15895);
        Assertions.assertEquals("(lon=10.9978°, lat=18.1590°)", coordinates.toString());
        coordinates = GeographicCoordinates.ofDeg(-1.0482, 1.0041);
        Assertions.assertEquals("(lon=-1.0482°, lat=1.0041°)", coordinates.toString());
    }
}
