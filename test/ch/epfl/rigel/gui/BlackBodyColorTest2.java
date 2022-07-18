package ch.epfl.rigel.gui;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BlackBodyColorTest2 {

    @Test
    void colorForTemperatureWorks() {
        assertEquals(Color.rgb(0xb3, 0xcc, 0xff), BlackBodyColor.colorForTemperature(14849.149));
        assertEquals(Color.web("#ffcc99"), BlackBodyColor.colorForTemperature(3798.1409));
        assertEquals(Color.web("#9bbcff"), BlackBodyColor.colorForTemperature(39988.149));
        assertEquals(Color.web("#ff3800"), BlackBodyColor.colorForTemperature(1001.000149));
        assertThrows(IllegalArgumentException.class, () -> {
            BlackBodyColor.colorForTemperature(40_000.00000000001d);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            BlackBodyColor.colorForTemperature(1000d - 0.00000000001d);
        });
    }
}
