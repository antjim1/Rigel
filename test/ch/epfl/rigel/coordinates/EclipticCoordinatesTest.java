package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;

public class EclipticCoordinatesTest {
    private static final RightOpenInterval LON_INTERVAL = RightOpenInterval.of(0.0, Angle.TAU);
    private static final ClosedInterval LAT_INTERVAL = ClosedInterval.symmetric(Angle.TAU / 2.0);

    @Test
    void ofThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> EclipticCoordinates.of(180.0, 0.0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> EclipticCoordinates.of(180.0, 91.0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> EclipticCoordinates.of(15.0, 91.0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> EclipticCoordinates.of(121, -91.0));
    }

    @Test
    void ofWorksOnEdgeCases() {
        Assertions.assertEquals(179.9999999,
                                EclipticCoordinates.of(Angle.ofDeg(179.9999999),
                                                       Angle.ofDeg(90.0)).lonDeg(),
                                1e-9);
        Assertions.assertEquals(-90.0,
                                EclipticCoordinates.of(Angle.ofDeg(0.0),
                                                       Angle.ofDeg(-90.0)).latDeg(),
                                1e-9);
        Assertions.assertEquals(90.0,
                                EclipticCoordinates.of(Angle.ofDeg(90.0),
                                                       Angle.ofDeg(0.0)).lonDeg(),
                                1e-9);
    }

    @Test
    void ofWorksOnRandomValues() {
        SplittableRandom randGen = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double lon = randGen.nextDouble(LON_INTERVAL.low(), LON_INTERVAL.high());
            double lat = randGen.nextDouble(LAT_INTERVAL.low(), LAT_INTERVAL.high());
            EclipticCoordinates coordinates = EclipticCoordinates.of(lon, lat);
            Assertions.assertEquals(lon, coordinates.lon(), 1e-9);
            Assertions.assertEquals(lat, coordinates.lat(), 1e-9);
        }
    }

    @Test
    void toStringWorks() {
        EclipticCoordinates coordinates = EclipticCoordinates.of(Angle.ofDeg(15.0), Angle.ofDeg(45.0));
        Assertions.assertEquals("(λ=15.0000°, β=45.0000°)", coordinates.toString());
        coordinates = EclipticCoordinates.of(Angle.ofDeg(15.6879), Angle.ofDeg(-31.01005));
        Assertions.assertEquals("(λ=15.6879°, β=-31.0101°)", coordinates.toString());
    }
}
