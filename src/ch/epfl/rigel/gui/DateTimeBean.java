package ch.epfl.rigel.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Class containing observable properties for a local date, time and time-zone
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class DateTimeBean {
    private final ObjectProperty<LocalDate> date;
    private final ObjectProperty<LocalTime> time;
    private final ObjectProperty<ZoneId> zone;

    public DateTimeBean() {
        date = new SimpleObjectProperty<>(null);
        time = new SimpleObjectProperty<>(null);
        zone = new SimpleObjectProperty<>(null);
    }

    //------------------------------------------------------date--------------------------------------------------------

    /**
     * Gives an observable property containing the date.
     *
     * @return property containing the date
     */
    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    /**
     * Gives the date
     *
     * @return the date
     */
    public LocalDate getDate() {
        return date.get();
    }

    /**
     * Setter for the date attribute. If the new date is different than the current one, observers of the date property
     * are notified of the change.
     *
     * @param date tbe new date
     */
    public void setDate(LocalDate date) {
        if (!date.equals(this.date.get())) {
            this.date.set(date);
        }
    }

    //-------------------------------------------------------time-------------------------------------------------------

    /**
     * Gives an observable property containing the time.
     *
     * @return the property containing the time
     */
    public ObjectProperty<LocalTime> timeProperty() {
        return time;
    }

    /**
     * Gives the time
     *
     * @return the time
     */
    public LocalTime getTime() {
        return time.get();
    }

    /**
     * Setter for the time attribute. If the new time is different than the current one, observers of the time property
     * are notified of the change.
     *
     * @param time the new time
     */
    public void setTime(LocalTime time) {
        if (!time.equals(this.time.get())) {
            this.time.set(time);
        }
    }

    //-------------------------------------------------------zone-------------------------------------------------------

    /**
     * Gives an observable property containing the time-zone.
     *
     * @return property containing the time-zone
     */
    public ObjectProperty<ZoneId> zoneProperty() {
        return zone;
    }

    /**
     * Gives the time-zone
     *
     * @return the time-zone
     */
    public ZoneId getZone() {
        return zone.get();
    }

    /**
     * Setter for the zone attribute. If the new time-zone is different than the current one, observers of the zone
     * property are notified of the change.
     *
     * @param zone the new time-zone
     */
    public void setZone(ZoneId zone) {
        if (!zone.equals(this.zone.get())) {
            this.zone.set(zone);
        }
    }

    //---------------------------------------------------zoneDateTime---------------------------------------------------

    /**
     * Gives the time-zone, date and time as a {@code ZonedDateTime} object.
     *
     * @return time-zone, date and time in a {@code ZonedDateTime} object
     */
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.of(getDate(), getTime(), getZone());
    }

    /**
     * Setter for the time-zone, date and time. Changes to any of these attributes are forwarded to their respective
     * observers.
     *
     * @param zonedDateTime object containing the new time-zone, date and time
     */
    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        setDate(zonedDateTime.toLocalDate());
        setTime(zonedDateTime.toLocalTime());
        setZone(zonedDateTime.getZone());
    }
}
