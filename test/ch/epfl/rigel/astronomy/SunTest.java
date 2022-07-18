package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class SunTest {
    @Test
    void randomValuesTest() {
        Sun sun609 = new Sun(EclipticCoordinates.of((Angle.ofDeg(43.2222)), Angle.ofDeg(67.8989889)),
                             EquatorialCoordinates.of(Angle.ofDeg(56.321), Angle.ofDeg(24.87)),
                             43.5f, 21.32f);
        assertEquals(43.5, sun609.angularSize(), 1e-9);
        assertEquals(21.32f, sun609.meanAnomaly(), 1e-9);
        assertEquals(Angle.ofDeg(56.321), sun609.equatorialPos().ra(), 1e-9);
        assertEquals(Angle.ofDeg(24.87), sun609.equatorialPos().dec(), 1e-9);
        assertEquals("Soleil", sun609.info());
        assertEquals(-26.7f, sun609.magnitude());

        sun609 = new Sun(EclipticCoordinates.of((Angle.ofDeg(43.2222)), Angle.ofDeg(67.8989889)),
                         EquatorialCoordinates.of(Angle.ofDeg(56.321), Angle.ofDeg(24.87)),
                         0f, 0f);
        assertEquals(0f, sun609.angularSize(), 1e-9);
        assertEquals(0f, sun609.meanAnomaly(), 1e-9);

        CelestialObject d;
        d = sun609;
        assertEquals(0f, d.angularSize(), 1e-9);
        assertEquals(Angle.ofDeg(56.321), sun609.equatorialPos().ra(), 1e-9);
        assertEquals(-26.7f, sun609.magnitude());
        assertEquals("Soleil", sun609.info());

        EclipticToEquatorialConversion ecliToEqu =
                new EclipticToEquatorialConversion(ZonedDateTime.of(2003, 7, 27,
                                                                    0, 0, 0, 0,
                                                                    ZoneOffset.UTC));
        SunModel.SUN.at(-2349.0, ecliToEqu);
    }

    @Test
    void ofThrowsIllegalArgumentException() {
        assertThrows(NullPointerException.class,
                     () -> new Sun(null, EquatorialCoordinates.of(Angle.ofDeg(56.321), Angle.ofDeg(24.87)),
                                   43.5f, 21.32f));
        assertThrows(NullPointerException.class,
                     () -> new Sun(EclipticCoordinates.of((Angle.ofDeg(43.2222)), Angle.ofDeg(67.8989889)), null,
                                   43.5f, 21.32f));
    }


    @Test
    void sunTest(){
        //tests variés pour sun et moon
        Sun sun = new Sun(EclipticCoordinates.of(Angle.ofDeg(53), Angle.ofDeg(38)),
                EquatorialCoordinates.of(Angle.ofDeg(55.8),Angle.ofDeg(24)),
                0.4f, 5.f);
        assertEquals("Soleil", sun.info());
        assertEquals(EquatorialCoordinates.of(Angle.ofDeg(55.8),
                Angle.ofDeg(24)).dec(), sun.equatorialPos().dec());
        assertEquals(EquatorialCoordinates.of(Angle.ofDeg(55.8),
                Angle.ofDeg(19.7)).ra(), sun.equatorialPos().ra()); //checking equatorial position
        assertEquals(5.f, sun.meanAnomaly());
        assertEquals(-26.7f, sun.magnitude());

        //test pour eclipticPos throws un null
        assertThrows(NullPointerException.class, () -> { new Sun(null,
                EquatorialCoordinates.of(Angle.ofDeg(55.8),Angle.ofDeg(24)),
                0.4f, 5.f); });
    }
}

