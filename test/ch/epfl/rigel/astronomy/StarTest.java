package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.internationalization.Translation;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class StarTest {
    @Test
    void constructorThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                     () -> new Star(-1, Translation.constant("rigel"),
                                    EquatorialCoordinates.of(0, 0),
                                    0, 0));

        assertThrows(IllegalArgumentException.class,
                     () -> new Star(1, Translation.constant("rigel"),
                                    EquatorialCoordinates.of(0, 0),
                                    0, 5.6f));

        assertDoesNotThrow(() -> new Star(1, Translation.constant("rigel"),
                                          EquatorialCoordinates.of(0, 0),
                                          0, 5.5f));
    }

    @Test
    void angularSizeIs0() {
        assertEquals(0, new Star(1, Translation.constant("rigel"),
                                 EquatorialCoordinates.of(0, 0),
                                 0, 0).angularSize());
    }

    @Test
    void colorTemperatureWorksOnKnownValues() {
        Function<Double, Star> newStar = (colorIndex) -> new Star(1, Translation.constant("dummy"),
                                                                  EquatorialCoordinates.of(0, 0),
                                                                  0, colorIndex.floatValue());
        Map<Double, Integer> testMap = new HashMap<>();
        testMap.put(-0.03, 10_515);
        testMap.put(1.5, 3_793);
        testMap.put(-0.02, 10_381);
        testMap.put(0.30, 7_461);
        testMap.put(0.58, 6_046);
        testMap.put(0.81, 5_250);
        testMap.put(1.40, 3_950);
        for (Map.Entry<Double, Integer> entry : testMap.entrySet()) {
            assertEquals(entry.getValue(), newStar.apply(entry.getKey()).colorTemperature());
        }
    }

    @Test
    void colorTemperatureWorksOnTestValues() {
        assertEquals(10515, new Star(24436, Translation.constant("Rigel"),
                                     EquatorialCoordinates.of(0, 0),
                                     0, -0.03f).colorTemperature());

        assertEquals(3793, new Star(27989, Translation.constant("Betelgeuse"),
                                    EquatorialCoordinates.of(0, 0),
                                    0, 1.50f).colorTemperature());
    }
}
