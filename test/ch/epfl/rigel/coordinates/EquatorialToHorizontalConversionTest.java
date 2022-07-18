package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static ch.epfl.rigel.coordinates.CoordinateAssertions.assertEquals2;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;


/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class EquatorialToHorizontalConversionTest {
    HorizontalCoordinates equatorialToHorizontal(ZonedDateTime when,
                                                 GeographicCoordinates where,
                                                 EquatorialCoordinates eqc) {
        double Sl = SiderealTime.local(when, where);
        double alpha = eqc.ra();
        double H = Sl - alpha;
        double phi = where.lat();
        double dl = eqc.dec();
        double h = asin(sin(dl) * sin(phi) + cos(dl) * cos(phi) * cos(H));
        double A = Angle.normalizePositive(atan2(-cos(dl) * cos(phi) * sin(H),
                                                 sin(dl) - sin(phi) * sin(h)));

        return HorizontalCoordinates.of(A, h);
    }

    @Test
    void applyWorksOnRandomCases() {
        ZonedDateTime when = ZonedDateTime.of(1500, 12, 31,
                                              12, 0, 0, 0,
                                              ZoneId.of("UTC+1"));
        GeographicCoordinates where = GeographicCoordinates.ofDeg(179.9999, 90.0);
        EquatorialToHorizontalConversion converter = new EquatorialToHorizontalConversion(when, where);
        EquatorialCoordinates eqc = EquatorialCoordinates.of(Angle.ofDeg(179.9999999), Angle.ofDeg(90.0));
        assertEquals2(equatorialToHorizontal(when, where, eqc), converter.apply(eqc), 1e-9);

        when = ZonedDateTime.of(2020, 2, 29,
                                0, 0, 0, 1_051_151,
                                ZoneId.of("UTC"));
        where = GeographicCoordinates.ofDeg(179.9999, 90.0);
        converter = new EquatorialToHorizontalConversion(when, where);
        eqc = EquatorialCoordinates.of(Angle.ofDeg(0.0), Angle.ofDeg(-90.0));
        assertEquals2(equatorialToHorizontal(when, where, eqc), converter.apply(eqc), 1e-9);

        when = ZonedDateTime.of(3000, 2, 28,
                                0, 0, 0, 4_071,
                                ZoneId.of("UTC+10"));
        where = GeographicCoordinates.ofDeg(179.9999, 90.0);
        converter = new EquatorialToHorizontalConversion(when, where);
        eqc = EquatorialCoordinates.of(Angle.ofDeg(90.0), Angle.ofDeg(0.0));
        assertEquals2(equatorialToHorizontal(when, where, eqc), converter.apply(eqc), 1e-9);
    }
}
