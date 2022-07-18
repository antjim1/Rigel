package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.gui.DateTimeBean;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.time.ZonedDateTime;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class SkyCanvasManagerTest {

    ZonedDateTime when = ZonedDateTime.parse("2020-02-17T20:15:00+01:00");

    DateTimeBean dateTimeBean = new DateTimeBean();

    @Test
    void objectUnderMouseWorks() {

    }


}
