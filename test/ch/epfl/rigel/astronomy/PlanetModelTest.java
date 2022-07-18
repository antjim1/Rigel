package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.CoordinateAssertions;
import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static ch.epfl.rigel.astronomy.Epoch.J2010;
import static ch.epfl.rigel.astronomy.PlanetModel.EARTH;
import static ch.epfl.rigel.astronomy.PlanetModel.JUPITER;
import static ch.epfl.rigel.astronomy.PlanetModel.MARS;
import static ch.epfl.rigel.astronomy.PlanetModel.MERCURY;
import static ch.epfl.rigel.astronomy.PlanetModel.NEPTUNE;
import static ch.epfl.rigel.astronomy.PlanetModel.SATURN;
import static ch.epfl.rigel.astronomy.PlanetModel.URANUS;
import static ch.epfl.rigel.astronomy.PlanetModel.VENUS;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class PlanetModelTest {
    private static final double EPSILON = 1e-6;

    @Test
    void signatureCheck() {
        Class<PlanetModel> cls = PlanetModel.class;
        ThrowingSupplier<Object> getMethod =
                () -> cls.getDeclaredMethod("at", double.class, EclipticToEquatorialConversion.class);
        assertDoesNotThrow(getMethod);
    }

    @Test
    void atWorksOnKnownValues() {
        PlanetModel planetModel = PlanetModel.JUPITER;
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2003, 11, 22, 0, 0, 0, 0, ZoneId.of("UTC"));
        double daysSinceJ2010 = J2010.daysUntil(zonedDateTime);
        EclipticToEquatorialConversion eclipticToEquatorialConversion =
                new EclipticToEquatorialConversion(zonedDateTime);
        Planet planet = planetModel.at(daysSinceJ2010, eclipticToEquatorialConversion);
        EclipticCoordinates expectedEclipticCoordinates =
                EclipticCoordinates.of(Angle.ofDeg(166.310510), Angle.ofDeg(1.036466));
        EquatorialCoordinates expectedEquatorialCoordinates =
                eclipticToEquatorialConversion.apply(expectedEclipticCoordinates);
        EquatorialCoordinates test = planet.equatorialPos();
        CoordinateAssertions.assertEquals2(test.raDeg(), expectedEquatorialCoordinates.raDeg(), EPSILON);
        CoordinateAssertions.assertEquals2(planet.equatorialPos(), expectedEquatorialCoordinates, EPSILON);

        assertEquals(Angle.ofArcsec(35.1), planet.angularSize(), EPSILON);
        assertEquals(-1.9885659217834473, planet.magnitude(), EPSILON);

        planetModel = PlanetModel.MERCURY;
        zonedDateTime = ZonedDateTime.of(2003, 11, 22, 0, 0, 0, 0, ZoneId.of("UTC"));
        daysSinceJ2010 = J2010.daysUntil(zonedDateTime);
        eclipticToEquatorialConversion = new EclipticToEquatorialConversion(zonedDateTime);
        planet = planetModel.at(daysSinceJ2010, eclipticToEquatorialConversion);
        expectedEclipticCoordinates = EclipticCoordinates.of(Angle.ofDeg(253.929758), Angle.ofDeg(-2.044057));
        expectedEquatorialCoordinates = eclipticToEquatorialConversion.apply(expectedEclipticCoordinates);

        CoordinateAssertions.assertEquals2(planet.equatorialPos(), expectedEquatorialCoordinates, EPSILON);
    }

    @Test
    void allIsImmutable() {
        assertThrows(UnsupportedOperationException.class, () -> PlanetModel.ALL.set(0, SATURN));
        assertEquals(PlanetModel.MERCURY, PlanetModel.ALL.get(0));
        assertArrayEquals(PlanetModel.values(), PlanetModel.ALL.toArray());
    }

    @Test
    void declarationOrderIsCorrect() {
        assertArrayEquals(new PlanetModel[] {MERCURY, VENUS, EARTH, MARS, JUPITER, SATURN, URANUS,
                                             NEPTUNE}, PlanetModel.values());
    }

    @Test
    void planetAt() {
        Planet jupiter = JUPITER.at(-2231.0, new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22), LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC)));
        assertEquals(11.18715493470968 , jupiter.equatorialPos().raHr(), 1e-10);
        assertEquals(6.35663550668575 , jupiter.equatorialPos().decDeg(), 1e-10);

        assertEquals(35.11141185362771 , Angle.toDeg(PlanetModel.JUPITER.at(-2231.0,new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22), LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC))).angularSize())*3600, 1e-10);
        assertEquals(-1.9885659217834473 , PlanetModel.JUPITER.at(-2231.0,new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22), LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC))).magnitude(), 1e-10);

        Planet mercury = MERCURY.at(-2231.0,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22),
                                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC)));
        assertEquals(16.8200745658971 , mercury.equatorialPos().raHr(), 1e-10);
        assertEquals(-24.500872462861 , mercury.equatorialPos().decDeg(), 1e-10);
    }
}
