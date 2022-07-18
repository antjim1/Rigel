package ch.epfl.rigel.gui;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class BlackBodyColorTest {
    @Test
    void colorForTemperatureThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> BlackBodyColor.colorForTemperature(999.99999));
        assertThrows(IllegalArgumentException.class, () -> BlackBodyColor.colorForTemperature(40000.0000001));
        assertThrows(IllegalArgumentException.class, () -> BlackBodyColor.colorForTemperature(0.0));
        assertThrows(IllegalArgumentException.class, () -> BlackBodyColor.colorForTemperature(-1000.0));
        assertThrows(IllegalArgumentException.class, () -> BlackBodyColor.colorForTemperature(-40000.0));
        assertThrows(IllegalArgumentException.class, () -> BlackBodyColor.colorForTemperature(-20000.0));
    }

    @Test
    void colorForTemperatureWorksForSomeValues() {
        double[] values = {1000, 40000, 1001, 20000.041, 1825.5, 32550, 21950.000001, 11749.999999};
        String[] expectedColors = {"#ff3800", "#9bbcff", "#ff3800", "#a8c5ff",
                                   "#ff7e00", "#9ebeff", "#a6c3ff", "#c0d4ff"};

        assert values.length == expectedColors.length;  // to make sure we didn't mess up when writing the test
        for (int i = 0; i < values.length; ++i) {
            double value = values[i];
            Color expectedColor = Color.web(expectedColors[i]);
            assertEquals(expectedColor, BlackBodyColor.colorForTemperature(value));
        }
    }
}
