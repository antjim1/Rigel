package ch.epfl.rigel.astronomy;

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
import static ch.epfl.rigel.coordinates.CoordinateAssertions.assertEquals2;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class SunModelTest {
    private final double EPSILON = 0.5e-6;

    @Test
    void signatureCheck() {
        Class<SunModel> cls = SunModel.class;
        ThrowingSupplier<Object> getMethod =
                () -> cls.getDeclaredMethod("at", double.class, EclipticToEquatorialConversion.class);
        assertDoesNotThrow(getMethod);
    }

    @Test
    void atWorksOnKnownValues() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2003, 7, 27,
                                                       0, 0, 0, 0,
                                                       ZoneId.of("UTC"));
        double daysSinceJ2010 = J2010.daysUntil(zonedDateTime);
        EclipticToEquatorialConversion eclipticToEquatorialConversion =
                new EclipticToEquatorialConversion(zonedDateTime);
        EclipticCoordinates expectedEclipticCoordinates = EclipticCoordinates.of(Angle.ofDeg(123.580601), 0.0);
        EquatorialCoordinates expectedEquatorialCoordinates =
                eclipticToEquatorialConversion.apply(expectedEclipticCoordinates);
        Sun sun = SunModel.SUN.at(daysSinceJ2010, eclipticToEquatorialConversion);
        assertEquals2(expectedEclipticCoordinates, sun.eclipticPos(), EPSILON);
        assertEquals2(expectedEquatorialCoordinates, sun.equatorialPos(), EPSILON);

        zonedDateTime = ZonedDateTime.of(1988, 7, 27,
                                         0, 0, 0, 0,
                                         ZoneId.of("UTC"));
        daysSinceJ2010 = J2010.daysUntil(zonedDateTime);
        eclipticToEquatorialConversion = new EclipticToEquatorialConversion(zonedDateTime);
        sun = SunModel.SUN.at(daysSinceJ2010, eclipticToEquatorialConversion);
        assertEquals(Angle.ofDMS(0, 31, 30), sun.angularSize(), Angle.ofDMS(0, 0, 0.5));
    }

    @Test
    void atWorksOnTestValues() {
        Sun sun = SunModel.SUN.at(
                27.0 + 31.0,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2010, Month.FEBRUARY, 27),
                                         LocalTime.of(0, 0),
                                         ZoneOffset.UTC)));
        assertEquals2(5.9325494700300885, sun.equatorialPos().ra(), 1e-12);

        sun = SunModel.SUN.at(
                -2349.0,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2003, Month.JULY, 27),
                                         LocalTime.of(0, 0, 0, 0),
                                         ZoneOffset.UTC)));
        assertEquals2(8.3926828082978, sun.equatorialPos().raHr(), 1e-12);

        sun = SunModel.SUN.at(
                -2349.0,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2003, Month.JULY, 27),
                                         LocalTime.of(0, 0, 0, 0),
                                         ZoneOffset.UTC)));
        assertEquals2(19.35288373097352, sun.equatorialPos().decDeg(), 1e-12);

        ZonedDateTime zdt = ZonedDateTime.of(LocalDate.of(1988, Month.JULY, 27),
                                             LocalTime.of(0, 0),
                                             ZoneOffset.UTC);
        sun = SunModel.SUN.at(
                Epoch.J2010.daysUntil(zdt),
                new EclipticToEquatorialConversion(zdt));
        assertEquals2(0.3353207024580374, sun.equatorialPos().dec(), 1e-12);

        ZonedDateTime zone1988 = ZonedDateTime.of(
                LocalDate.of(1988, Month.JULY, 27),
                LocalTime.of(0, 0),
                ZoneOffset.UTC);
        sun = SunModel.SUN.at(J2010.daysUntil(zone1988),
                              new EclipticToEquatorialConversion(zone1988));
        assertEquals2(0.009162353351712227, sun.angularSize(), 1e-12);
    }
}
