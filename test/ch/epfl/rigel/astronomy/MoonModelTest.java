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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;

import static ch.epfl.rigel.astronomy.Epoch.J2010;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class MoonModelTest {
    @Test
    void signatureCheck() {
        Class<MoonModel> cls = MoonModel.class;
        ThrowingSupplier<Object> getMethod =
                () -> cls.getDeclaredMethod("at", double.class, EclipticToEquatorialConversion.class);
        assertDoesNotThrow(getMethod);
    }

    @Test
    void atWorksWithKnownValues() {
        MoonModel model = MoonModel.MOON;
        ZonedDateTime time = ZonedDateTime.of(2003, 9, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        double days = J2010.daysUntil(time);
        EclipticToEquatorialConversion conversion = new EclipticToEquatorialConversion(time);
        Moon moon = model.at(days, conversion);

        EclipticCoordinates expectedEcl = EclipticCoordinates.of(Angle.ofDeg(214.862515), Angle.ofDeg(1.716257));
        EquatorialCoordinates expectedEqu = conversion.apply(expectedEcl);
        CoordinateAssertions.assertEquals2(expectedEqu, moon.equatorialPos(), Angle.ofDeg(1e-6));
        assertEquals(String.format(Locale.ROOT, "Lune (%.1f%%)", 22.5), moon.info());
    }

    @Test
    void atWorksWithTestValues() {
        Moon moon = MoonModel.MOON.at(
                -2313.0,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(
                                LocalDate.of(2003, Month.SEPTEMBER, 1),
                                LocalTime.of(0, 0), ZoneOffset.UTC)));
        assertEquals(14.211456457836277, moon.equatorialPos().raHr(), 1e-12);

        moon = MoonModel.MOON.at(
                -2313.0,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(
                                LocalDate.of(2003, Month.SEPTEMBER, 1),
                                LocalTime.of(0, 0),
                                ZoneOffset.UTC)));

        assertEquals(-0.20114171346019355, moon.equatorialPos().dec(), 1e-12);

        moon = MoonModel.MOON.at(
                J2010.daysUntil(
                        ZonedDateTime.of(
                                LocalDate.of(1979, 9, 1),
                                LocalTime.of(0, 0),
                                ZoneOffset.UTC)),
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(
                                LocalDate.of(1979, 9, 1),
                                LocalTime.of(0, 0),
                                ZoneOffset.UTC)));
        assertEquals(0.009225908666849136, moon.angularSize(), 1e-14);
    }


}
