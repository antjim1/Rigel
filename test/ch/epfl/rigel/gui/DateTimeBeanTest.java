package ch.epfl.rigel.gui;

import javafx.beans.property.ObjectProperty;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class DateTimeBeanTest {
    private static final LocalDate dummyDate1 = LocalDate.of(2000, 1, 2);
    private static final LocalTime dummyTime1 = LocalTime.of(0, 0, 0, 0);
    private static final ZoneId dummyZone1 = ZoneId.of("UTC+1");

    private static final LocalDate dummyDate2 = LocalDate.of(2111, 4, 12);
    private static final LocalTime dummyTime2 = LocalTime.of(4, 3, 2, 1);
    private static final ZoneId dummyZone2 = ZoneId.of("UTC+1");

    private static void addModificationNotifier(ObjectProperty<?> property) {
        property.addListener((p, o, n) -> {
            throw new MethodCalledConfirmation();
        });
    }

    @Test
    void datePropertyWorks() {
        DateTimeBean bean = new DateTimeBean();
        addModificationNotifier(bean.dateProperty());

        assertThrows(MethodCalledConfirmation.class, () -> bean.setDate(LocalDate.of(2000, 1, 1)));
        assertThrows(MethodCalledConfirmation.class, () -> bean.setZonedDateTime(
                ZonedDateTime.of(LocalDate.of(2000, 1, 2), dummyTime1, dummyZone1)));
        assertDoesNotThrow(() -> bean.setDate(LocalDate.of(2000, 1, 2)));
        assertDoesNotThrow(() -> bean.setZonedDateTime(ZonedDateTime.of(LocalDate.of(2000, 1, 2), dummyTime2, dummyZone2)));
    }

    @Test
    void timePropertyWorks() {
        DateTimeBean bean = new DateTimeBean();
        addModificationNotifier(bean.timeProperty());

        assertThrows(MethodCalledConfirmation.class, () -> bean.setTime(LocalTime.of(12, 0)));
        assertThrows(MethodCalledConfirmation.class, () -> bean.setZonedDateTime(
                ZonedDateTime.of(dummyDate1, LocalTime.of(12, 0, 0, 1), dummyZone1)));
        assertDoesNotThrow(() -> bean.setTime(LocalTime.of(12, 0, 0, 1)));
        assertDoesNotThrow(() -> bean.setZonedDateTime(ZonedDateTime.of(dummyDate2, LocalTime.of(12, 0, 0, 1), dummyZone2)));
    }

    @Test
    void zonePropertyWorks() {
        DateTimeBean bean = new DateTimeBean();
        addModificationNotifier(bean.zoneProperty());

        assertThrows(MethodCalledConfirmation.class, () -> bean.setZone(ZoneId.of("UTC+7")));
        assertThrows(MethodCalledConfirmation.class, () -> bean.setZonedDateTime(
                ZonedDateTime.of(dummyDate1, dummyTime1, ZoneId.of("UTC+2"))));
        assertDoesNotThrow(() -> bean.setZone(ZoneId.of("UTC+2")));
        assertDoesNotThrow(() -> bean.setZonedDateTime(ZonedDateTime.of(dummyDate2, dummyTime2, ZoneId.of("UTC+2"))));

    }

    static class MethodCalledConfirmation extends Error {
    }
}
