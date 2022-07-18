package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static ch.epfl.rigel.astronomy.Epoch.J2000;
import static ch.epfl.rigel.coordinates.CoordinateAssertions.assertEquals2;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class EclipticToEquatorialConversionTest {
    double epsilon(ZonedDateTime when) {
        double T = J2000.julianCenturiesUntil(when);
        Polynomial polE = Polynomial.of(Angle.ofArcsec(0.00181), Angle.ofArcsec(-0.0006),
                                        Angle.ofArcsec(-46.815), Angle.ofDMS(23, 26, 21.45));
        return polE.at(T);
    }

    EquatorialCoordinates eclipticToEquatorial(ZonedDateTime when, EclipticCoordinates ecl) {
        final double lb = ecl.lon();
        final double bt = ecl.lat();
        double e = epsilon(when);
        double alpha = Angle.normalizePositive(atan2(sin(lb)*cos(e) - tan(bt)*sin(e), cos(lb)));
        double delta = asin(sin(bt)*cos(e) + cos(bt)*sin(e)*sin(lb));
        return EquatorialCoordinates.of(alpha, delta);
    }

    @Test
    void applyWorksOnNormalCases() {
        ZonedDateTime when = ZonedDateTime.of(0, 1, 1,
                                              0, 0, 0, 0,
                                              ZoneId.of("UTC-2"));
        EclipticToEquatorialConversion converter = new EclipticToEquatorialConversion(when);
        EclipticCoordinates ecl = EclipticCoordinates.of(Angle.ofDeg(142.001247515), Angle.ofDeg(-12.586657));
        assertEquals2(eclipticToEquatorial(when, ecl), converter.apply(ecl), 1e-9);

        when = ZonedDateTime.of(2021, 7, 12,
                                1, 18, 59, 9_071_411,
                                ZoneId.of("UTC-6"));
        converter = new EclipticToEquatorialConversion(when);
        ecl = EclipticCoordinates.of(Angle.ofDeg(90.0), Angle.ofDeg(0.0));
        assertEquals2(eclipticToEquatorial(when, ecl), converter.apply(ecl), 1e-9);
    }

    @Test
    void applyWorksOnEdgeCases() {
        ZonedDateTime when = ZonedDateTime.of(1500, 12, 31,
                                              12, 0, 0, 0,
                                              ZoneId.of("UTC+1"));
        EclipticToEquatorialConversion converter = new EclipticToEquatorialConversion(when);
        EclipticCoordinates ecl = EclipticCoordinates.of(Angle.ofDeg(179.9999999), Angle.ofDeg(90.0));
        assertEquals2(eclipticToEquatorial(when, ecl), converter.apply(ecl), 1e-9);

        when = ZonedDateTime.of(2020, 2, 29,
                                0, 0, 0, 1_051_151,
                                ZoneId.of("UTC"));
        converter = new EclipticToEquatorialConversion(when);
        ecl = EclipticCoordinates.of(Angle.ofDeg(0.0), Angle.ofDeg(-90.0));
        assertEquals2(eclipticToEquatorial(when, ecl), converter.apply(ecl), 1e-9);

        when = ZonedDateTime.of(3000, 2, 28,
                                0, 0, 0, 4_071,
                                ZoneId.of("UTC+10"));
        converter = new EclipticToEquatorialConversion(when);
        ecl = EclipticCoordinates.of(Angle.ofDeg(90.0), Angle.ofDeg(0.0));
        assertEquals2(eclipticToEquatorial(when, ecl), converter.apply(ecl), 1e-9);
    }
}
