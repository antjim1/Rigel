package ch.epfl.rigel.gui;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.Interval;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads a temperature and assigns the corresponding color
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class BlackBodyColor {
    private static final int TEMP_BEGINNING = 1;
    private static final int TEMP_END = 6;
    private static final int DEG_BEGINNING = 10;
    private static final int COLOR_BEGINNING = 80;
    private static final int COLOR_END = 87;
    private static final Interval TEMPERATURE_INTERVAL = ClosedInterval.of(1000, 40000);
    private static final int STEP_SIZE = 100;
    private static final Map<Integer, Color> TEMPERATURE_TO_COLOR = Collections.unmodifiableMap(extractData());

    private BlackBodyColor() {
    }

    private static Map<Integer, Color> extractData() {
        final int DATA_COUNT = 391;
        Map<Integer, Color> temperatureToColor = new HashMap<>(DATA_COUNT);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                BlackBodyColor.class.getResourceAsStream("/bbr_color.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {  // read lines one by one
                if (line.isEmpty() || line.charAt(0) == '#') continue;  // ignore empty lines and comments

                if (line.startsWith("10deg", DEG_BEGINNING)) {  // take lines with "10deg"
                    String temperatureString = line.substring(TEMP_BEGINNING, TEMP_END);
                    int temperature = Integer.parseInt(temperatureString.replace(" ", ""));
                    String htmlColorCode = line.substring(COLOR_BEGINNING, COLOR_END);
                    temperatureToColor.put(temperature, Color.web(htmlColorCode));
                }
            }
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }

        return temperatureToColor;
    }

    private static int round(double temperature) {
        return (int) Math.round(temperature / STEP_SIZE) * STEP_SIZE;
    }

    /**
     * Gives the corresponding color to the temperature
     *
     * @param temperature temperature given in Kelvin
     *
     * @return the corresponding color
     *
     * @throws IllegalArgumentException if the temperature is not within the range
     */
    public static Color colorForTemperature(double temperature) {
        Preconditions.checkInInterval(TEMPERATURE_INTERVAL, temperature);
        return TEMPERATURE_TO_COLOR.get(round(temperature));
    }
}
