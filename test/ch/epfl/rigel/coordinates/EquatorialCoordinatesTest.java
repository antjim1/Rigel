package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;

public class EquatorialCoordinatesTest {
    private static final RightOpenInterval RA_INTERVAL = RightOpenInterval.of(0.0, Angle.TAU);
    private static final ClosedInterval DEC_INTERVAL = ClosedInterval.symmetric(Angle.TAU / 2.0);

    @Test
    void ofThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> EquatorialCoordinates.of(180.0, 0.0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> EquatorialCoordinates.of(180.0, 91.0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> EquatorialCoordinates.of(15.0, 91.0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> EquatorialCoordinates.of(121, -91.0));
    }

    @Test
    void ofWorksOnEdgeCases() {
        Assertions.assertEquals(179.9999999,
                                EquatorialCoordinates.of(Angle.ofDeg(179.9999999),
                                                         Angle.ofDeg(90.0)).raDeg(),
                                1e-9);
        Assertions.assertEquals(-90.0,
                                EquatorialCoordinates.of(Angle.ofDeg(0.0),
                                                         Angle.ofDeg(-90.0)).decDeg(),
                                1e-9);
        Assertions.assertEquals(90.0,
                                EquatorialCoordinates.of(Angle.ofDeg(90.0),
                                                         Angle.ofDeg(0.0)).raDeg(),
                                1e-9);
    }

    @Test
    void ofWorksOnRandomValues() {
        SplittableRandom randGen = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double ra = randGen.nextDouble(RA_INTERVAL.low(), RA_INTERVAL.high());
            double dec = randGen.nextDouble(DEC_INTERVAL.low(), DEC_INTERVAL.high());
            EquatorialCoordinates coordinates = EquatorialCoordinates.of(ra, dec);
            Assertions.assertEquals(ra, coordinates.ra(), 1e-9);
            Assertions.assertEquals(dec, coordinates.dec(), 1e-9);
        }
    }

    @Test
    void toStringWorks() {
        EquatorialCoordinates coordinates = EquatorialCoordinates.of(Angle.ofHr(1.5), Angle.ofDeg(45.0));
        Assertions.assertEquals("(ra=1.5000h, dec=45.0000°)", coordinates.toString());
        coordinates = EquatorialCoordinates.of(Angle.ofHr(1.5687), Angle.ofDeg(-31.01005));
        Assertions.assertEquals("(ra=1.5687h, dec=-31.0101°)", coordinates.toString());
    }
}
