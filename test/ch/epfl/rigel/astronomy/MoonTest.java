package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class MoonTest {

    @Test
    void randomValuesTest() {
        Moon moon402 = new Moon(EquatorialCoordinates.of(Angle.ofDeg(87.444), Angle.ofDeg(41.8999)), 31, 0.31f);
        Assertions.assertEquals(31, moon402.angularSize(), 1e-9);
        Assertions.assertEquals(Angle.ofDeg(87.444), moon402.equatorialPos().ra(), 1e-9);
        Assertions.assertEquals(Angle.ofDeg(41.8999), moon402.equatorialPos().dec(), 1e-9);

        moon402 = new Moon(EquatorialCoordinates.of(Angle.ofDeg(87.444), Angle.ofDeg(41.8999)), 0f, 0f);
        Assertions.assertEquals(0f, moon402.angularSize(), 1e-9);
        Assertions.assertEquals("Lune (0.0%)", moon402.info());

    }

    @Test
    void ofThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Moon(EquatorialCoordinates.of(Angle.ofDeg(87.444), Angle.ofDeg(41.8999)), 31, -0.001f));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Moon(EquatorialCoordinates.of(Angle.ofDeg(87.444), Angle.ofDeg(41.8999)), -32, 0.32f));
        Assertions.assertThrows(NullPointerException.class, () -> new Moon(null, 31, 0.32f));

    }

    @Test
    void toStringEdgyTest() {
        Moon moon402 = new Moon(EquatorialCoordinates.of(Angle.ofDeg(87.444), Angle.ofDeg(41.8999)), 31, 0.9999f);
        Assertions.assertEquals("Lune (100.0%)", moon402.info());
        moon402 = new Moon(EquatorialCoordinates.of(Angle.ofDeg(87.444), Angle.ofDeg(41.8999)), 31, 0.000000001f);
        Assertions.assertEquals("Lune (0.0%)", moon402.info());

    }

    @Test
    void toStringRandomTest() {
        Moon moon402 = new Moon(EquatorialCoordinates.of(Angle.ofDeg(34.0), Angle.ofDeg(39.32)), 31, 0.31654f);
        Assertions.assertEquals("Lune (31.7%)", moon402.info());
        moon402 = new Moon(EquatorialCoordinates.of(Angle.ofDeg(34.0), Angle.ofDeg(39.32)), 31, 0.3698f);
        Assertions.assertEquals("Lune (37.0%)", moon402.info());
        moon402 = new Moon(EquatorialCoordinates.of(Angle.ofDeg(34.0), Angle.ofDeg(39.32)), 31, 0.3752f);
        Assertions.assertEquals("Lune (37.5%)", moon402.info());
    }
}
