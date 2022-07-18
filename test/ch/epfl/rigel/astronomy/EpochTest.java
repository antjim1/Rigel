package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.*;

import static ch.epfl.rigel.astronomy.Epoch.J2000;
import static ch.epfl.rigel.astronomy.Epoch.J2010;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class EpochTest {
    @Test
    void daysUntilOnRandomValues() {
        ZonedDateTime when = ZonedDateTime.of(LocalDate.of(2000, Month.JANUARY, 3), LocalTime.of(18, 0), ZoneOffset.UTC);
        Assertions.assertEquals(2.25, J2000.daysUntil(when));
        when = ZonedDateTime.of( LocalDate.of(2000,  Month.JANUARY, 3), LocalTime.of(18,0), ZoneOffset.UTC);
        Assertions.assertEquals(2.25, J2000.daysUntil(when) );
        when = ZonedDateTime.of( LocalDate.of(2000,  Month.JANUARY, 7), LocalTime.of(6,0), ZoneOffset.UTC);
        Assertions.assertEquals(5.75, J2000.daysUntil(when) );
        when = ZonedDateTime.of( LocalDate.of(2000,  Month.JULY, 6), LocalTime.of(12,0), ZoneOffset.UTC);
        Assertions.assertEquals(187, J2000.daysUntil(when) );
        when = ZonedDateTime.of( LocalDate.of(1999,  Month.AUGUST, 13), LocalTime.of(12,0), ZoneOffset.UTC);
        Assertions.assertEquals(-141, J2000.daysUntil(when) );
        when = ZonedDateTime.of( LocalDate.of(2007,  Month.JULY, 18), LocalTime.of(6,0), ZoneOffset.UTC);
        Assertions.assertEquals(-896.75, J2010.daysUntil(when) );
        when = ZonedDateTime.of( LocalDate.of(2011,  Month.MAY, 7), LocalTime.of(12,0), ZoneOffset.UTC);
        Assertions.assertEquals(492.5, J2010.daysUntil(when) );
    }

    @Test
    void daysUnitOnEdgeCases() {
        ZonedDateTime when = ZonedDateTime.of( LocalDate.of(1999,  Month.DECEMBER, 31), LocalTime.of(18,00), ZoneOffset.UTC);
        Assertions.assertEquals(-0.75, J2000.daysUntil(when), 1e-9);
        when = ZonedDateTime.of( LocalDate.of(2000,  Month.JANUARY, 1), LocalTime.of(13,0), ZoneOffset.UTC);
        Assertions.assertEquals(0.041666666666666666666666, J2000.daysUntil(when), 1e-9);
        when = ZonedDateTime.of( LocalDate.of(2000,  Month.JANUARY, 1), LocalTime.of(11,0), ZoneOffset.UTC);
        Assertions.assertEquals(-0.04166666666666666666, J2000.daysUntil(when), 1e-9);
         when = ZonedDateTime.of( LocalDate.of(2000,  Month.JANUARY, 1), LocalTime.of(13,0), ZoneOffset.UTC);
        Assertions.assertEquals(0.041666666666666666666, J2000.daysUntil(when), 1e-9);
         when = ZonedDateTime.of( LocalDate.of(2000,  Month.JANUARY, 1), LocalTime.of(11,0), ZoneOffset.UTC);
        Assertions.assertEquals(-0.04166666666666666666666666, J2000.daysUntil(when), 1e-9);
         when = ZonedDateTime.of( LocalDate.of(2000,  Month.JANUARY, 1), LocalTime.of(12,1), ZoneOffset.UTC);
        Assertions.assertEquals(0.0006944444444444444444444, J2000.daysUntil(when), 1e-9);
         when = ZonedDateTime.of( LocalDate.of(2000,  Month.JANUARY, 1), LocalTime.of(11,59), ZoneOffset.UTC);
        Assertions.assertEquals(-0.00069444444444444444444, J2000.daysUntil(when), 1e-9);

    }

    @Test
    void julianCenturiesUntilOnRandomValues() {
        ZonedDateTime when = ZonedDateTime.of( LocalDate.of(2000,  Month.JANUARY, 3), LocalTime.of(18,0), ZoneOffset.UTC);
        Assertions.assertEquals(6.160164271047e-5, J2000.julianCenturiesUntil(when), 1e-5 );
         when = ZonedDateTime.of( LocalDate.of(2000,  Month.JANUARY, 7), LocalTime.of(6,0), ZoneOffset.UTC);
        Assertions.assertEquals(1.5742642e-4, J2000.julianCenturiesUntil(when), 1e-9 );
    }

    @Test
    void jualianCenturiesUntilOnEdgeCases() {
        ZonedDateTime when = ZonedDateTime.of( LocalDate.of(2000,  Month.JANUARY, 1), LocalTime.of(12,1), ZoneOffset.UTC);
        Assertions.assertEquals(1.901284052019e-8, J2000.julianCenturiesUntil(when), 1e-4 );
          when = ZonedDateTime.of( LocalDate.of(2000,  Month.JANUARY, 1), LocalTime.of(11,59), ZoneOffset.UTC);
         Assertions.assertEquals(-1.901284052019e-8, J2000.julianCenturiesUntil(when), 1e-9 );
    }

}
