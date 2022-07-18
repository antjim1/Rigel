package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;

public class HorizontalCoordinatesTest {
    private static final RightOpenInterval AZ_INTERVAL = RightOpenInterval.of(0.0, Angle.TAU);
    private static final ClosedInterval ALT_INTERVAL = ClosedInterval.symmetric(Angle.TAU / 2.0);

    @Test
    void ofDegThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> HorizontalCoordinates.ofDeg(360.0, 0.0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> HorizontalCoordinates.ofDeg(0.0, 90.0001));
        Assertions.assertThrows(IllegalArgumentException.class, () -> HorizontalCoordinates.ofDeg(0.0, -90.0001));
        Assertions.assertThrows(IllegalArgumentException.class, () -> HorizontalCoordinates.ofDeg(-0.0001, 0.0));

    }

    @Test
    void ofDegWorksOnEdgeCases() {
        Assertions.assertEquals(90.0, HorizontalCoordinates.ofDeg(359.9999999, 90.0).altDeg(), 1e-9);
        Assertions.assertEquals(359.9999999, HorizontalCoordinates.ofDeg(359.9999999, 90.0).azDeg(), 1e-9);
        Assertions.assertEquals(-90.0, HorizontalCoordinates.ofDeg(0.0, -90.0).altDeg(), 1e-9);
        Assertions.assertEquals(180.0, HorizontalCoordinates.ofDeg(180.0, 0.0).azDeg(), 1e-9);
    }

    @Test
    void ofDegWorksOnRandomValues() {
        SplittableRandom randGen = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double azDeg = Angle.toDeg(randGen.nextDouble(AZ_INTERVAL.low(), AZ_INTERVAL.high()));
            double altDeg = Angle.toDeg(randGen.nextDouble(ALT_INTERVAL.low(), ALT_INTERVAL.high()));
            HorizontalCoordinates coordinates = HorizontalCoordinates.ofDeg(azDeg, altDeg);
            Assertions.assertEquals(azDeg, coordinates.azDeg(), 1e-9);
            Assertions.assertEquals(altDeg, coordinates.altDeg(), 1e-9);
        }
    }

    @Test
    void ofThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> HorizontalCoordinates.of(Angle.TAU, 0.0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> HorizontalCoordinates.of(0.0, Angle.TAU / 4.0 + 0.0001));
        Assertions.assertThrows(IllegalArgumentException.class, () -> HorizontalCoordinates.of(0.0, -Angle.TAU / 4.0 - 0.0001));
        Assertions.assertThrows(IllegalArgumentException.class, () -> HorizontalCoordinates.of(-0.0001, 0.0));
    }

    @Test
    void ofWorksOnEdgeCases() {
        Assertions.assertEquals(Angle.TAU - 0.00001, HorizontalCoordinates.of(Angle.TAU - 0.00001, Angle.TAU / 4.0).az(), 1e-9);
        Assertions.assertEquals(-Angle.TAU / 4.0, HorizontalCoordinates.of(0.0, -Angle.TAU / 4.0).alt(), 1e-9);
        Assertions.assertEquals(Angle.TAU / 2.0, HorizontalCoordinates.of(Angle.TAU / 2.0, 0.0).az(), 1e-9);
    }

    @Test
    void ofWorksOnRandomValues() {
        SplittableRandom randGen = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double az = randGen.nextDouble(AZ_INTERVAL.low(), AZ_INTERVAL.high());
            double alt = randGen.nextDouble(ALT_INTERVAL.low(), ALT_INTERVAL.high());
            HorizontalCoordinates coordinates = HorizontalCoordinates.of(az, alt);
            Assertions.assertEquals(az, coordinates.az(), 1e-9);
            Assertions.assertEquals(alt, coordinates.alt(), 1e-9);
        }
    }

    @Test
    void azDegAndAltDegWorkWithRandomValues() {
        SplittableRandom randGen = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double az = randGen.nextDouble(AZ_INTERVAL.low(), AZ_INTERVAL.high());
            double alt = randGen.nextDouble(ALT_INTERVAL.low(), ALT_INTERVAL.high());
            HorizontalCoordinates coordinates = HorizontalCoordinates.of(az, alt);
            Assertions.assertEquals(Angle.toDeg(az), coordinates.azDeg(), 1e-9);
            Assertions.assertEquals(Angle.toDeg(alt), coordinates.altDeg(), 1e-9);
        }
    }

    @Test
    void azOctantNameWork() {
        String[] cardinalPoints0 = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        String[] cardinalPoints1 = {"North", "NorthEast", "East", "SouthEast", "South", "SouthWest", "West", "NorthWest"};
        HashMap<Double, Integer> values = new HashMap<>();
        values.put(22.6, 1);
        values.put(22.4, 0);
        values.put(359.0, 0);
        values.put(330.0, 7);
        values.put(90.0, 2);
        for (Map.Entry<Double, Integer> entry : values.entrySet()) {
            int i = entry.getValue();
            double azDeg = entry.getKey();
            HorizontalCoordinates coordinates = HorizontalCoordinates.ofDeg(azDeg, 0.0);
            Assertions.assertEquals(cardinalPoints0[i], coordinates.azOctantName("N", "E", "S", "W"));
            Assertions.assertEquals(cardinalPoints1[i], coordinates.azOctantName("North", "East", "South", "West"));
        }
    }

    @Test
    void angularDistanceToWorks() {
        HorizontalCoordinates angularDistance = HorizontalCoordinates.ofDeg(6.5682, 46.5183);
        HorizontalCoordinates angularDistance2 = HorizontalCoordinates.ofDeg(8.5476, 47.3763);
        Assertions.assertEquals( 0.0279, angularDistance.angularDistanceTo(angularDistance2), 1e-4);
    }

    @Test
    void toStringWorks() {
        HorizontalCoordinates coordinates = HorizontalCoordinates.ofDeg(10.997845, 18.15895);
        Assertions.assertEquals("(az=10.9978°, alt=18.1590°)", coordinates.toString());
        coordinates = HorizontalCoordinates.ofDeg(1.0482, 1.0041);
        Assertions.assertEquals("(az=1.0482°, alt=1.0041°)", coordinates.toString());
    }
}
