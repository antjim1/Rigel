package ch.epfl.rigel.gui;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class TimeAcceleratorTest {
    @Test
    void continuousWorks() {
        TimeAccelerator accelerator = TimeAccelerator.continuous(300);
        ZonedDateTime T0 = ZonedDateTime.of(2020, 4, 20, 21, 0, 0, 0, ZoneId.of("UTC+1"));
        ZonedDateTime T = T0.withMinute(11).withSecond(42);
        long dtNano = (long) 2.34e9;
        assertEquals(T, accelerator.adjust(T0, dtNano));
    }

    @Test
    void discreteWorks() {
        TimeAccelerator accelerator = TimeAccelerator.discrete(Duration.ofHours(23).plusMinutes(56).plusSeconds(4), 10);
        ZonedDateTime T0 = ZonedDateTime.of(2020, 4, 20, 21, 0, 0, 0, ZoneId.of("UTC+1"));
        ZonedDateTime T = T0.plusDays(23).withHour(19).withMinute(29).withSecond(32);
        long dtNano = (long) 2.34e9;
        assertEquals(T, accelerator.adjust(T0, dtNano));
    }
}
