package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class SiderealTimeTest {
    private static final double HOURS_PER_MILLISECOND = 1d / (60d * 60d * 1000d);

    double greenwich2(ZonedDateTime when) {
        ZonedDateTime gwTime = when.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime gwTimeTruncatedToDays = gwTime.truncatedTo(ChronoUnit.DAYS);
        double T = Epoch.J2000.julianCenturiesUntil(gwTimeTruncatedToDays);
        double t = gwTimeTruncatedToDays.until(gwTime, ChronoUnit.MILLIS) * HOURS_PER_MILLISECOND;
        double S0 = Polynomial.of(0.000025862,
                                  2400.051336,
                                  6.697374558).at(T);
        double S1 = 1.002737909 * t;
        return Angle.normalizePositive(Angle.ofHr(S0 + S1));
    }

    double local2(ZonedDateTime when, GeographicCoordinates where) {
        return Angle.normalizePositive(greenwich2(when) + where.lon());
    }

    @Test
    void greenwichWorksOnEdgeCases() {
        ZonedDateTime when = ZonedDateTime.of(1500, 12, 31,
                                              12, 0, 0, 0,
                                              ZoneId.of("UTC+1"));
        Assertions.assertEquals(greenwich2(when), SiderealTime.greenwich(when), 1e-9);

        when = ZonedDateTime.of(2020, 2, 29, 0,
                                0, 0, 1_051_151,
                                ZoneId.of("UTC"));
        Assertions.assertEquals(greenwich2(when), SiderealTime.greenwich(when), 1e-9);

        when = ZonedDateTime.of(3000, 2, 28,
                                0, 0, 0, 4_071,
                                ZoneId.of("UTC+10"));
        Assertions.assertEquals(greenwich2(when), SiderealTime.greenwich(when), 1e-9);

        when = ZonedDateTime.of(0, 1, 1,
                                0, 0, 0, 0,
                                ZoneId.of("UTC-2"));
        Assertions.assertEquals(greenwich2(when), SiderealTime.greenwich(when), 1e-9);
    }

    @Test
    void localWorksOnEdgeCases() {
        ZonedDateTime when = ZonedDateTime.of(1500, 12, 31,
                                              12, 0, 0, 0,
                                              ZoneId.of("UTC+1"));
        GeographicCoordinates where = GeographicCoordinates.ofDeg(179.999, 90.0);
        Assertions.assertEquals(local2(when, where), SiderealTime.local(when, where), 1e-9);

        when = ZonedDateTime.of(2020, 2, 29,
                                0, 0, 0, 1_051_151,
                                ZoneId.of("UTC"));
        where = GeographicCoordinates.ofDeg(42.01, -90.0);
        Assertions.assertEquals(local2(when, where), SiderealTime.local(when, where), 1e-9);

        when = ZonedDateTime.of(3000, 2, 28,
                                0, 0, 0, 4_071,
                                ZoneId.of("UTC+10"));
        where = GeographicCoordinates.ofDeg(-180.0, -51.001);
        Assertions.assertEquals(local2(when, where), SiderealTime.local(when, where), 1e-9);

        when = ZonedDateTime.of(0, 1, 1,
                                0, 0, 0, 0,
                                ZoneId.of("UTC-2"));
        where = GeographicCoordinates.ofDeg(50.14589, 62.0155265);
        Assertions.assertEquals(local2(when, where), SiderealTime.local(when, where), 1e-9);
    }
}
