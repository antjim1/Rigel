package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import ch.epfl.test.TestRandomizer;
import javafx.beans.Observable;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class ViewingParametersBeanTest {
    private static final Double fieldOfView1 = Angle.ofDeg(45);
    private static final Double fieldOfView2 = Angle.ofDeg(120);
    private static final Double fieldOfView3 = Angle.ofDeg(30);
    private static final Double fieldOfView4 = Angle.ofDeg(150);

    private static final HorizontalCoordinates coordinates1 = HorizontalCoordinates.ofDeg(179, 89);
    private static final HorizontalCoordinates coordinates2 = HorizontalCoordinates.ofDeg(0.1, -45);
    private static final HorizontalCoordinates coordinates3 = HorizontalCoordinates.ofDeg(60, -89.9);
    private static final HorizontalCoordinates coordinates4 = HorizontalCoordinates.ofDeg(45, 60);

    private static final RightOpenInterval AZ_INTERVAL = RightOpenInterval.of(0.0, Angle.TAU);
    private static final ClosedInterval ALT_INTERVAL = ClosedInterval.symmetric(Angle.TAU / 2.0);

    @Test
    void fieldOfViewPropertyWorks() {
        ViewingParametersBean fieldOfView = new ViewingParametersBean();
        addModificationNotifier(fieldOfView.fieldOfViewDegProperty());

        assertThrows(ViewingParametersBeanTest.MethodCalledConfirmation.class, () -> fieldOfView.setFieldOfViewDeg(fieldOfView1));
        assertDoesNotThrow(() -> fieldOfView.setFieldOfViewDeg(fieldOfView1));
        assertEquals(fieldOfView.getFieldOfViewDeg(), fieldOfView1);
        assertThrows(ViewingParametersBeanTest.MethodCalledConfirmation.class, () -> fieldOfView.setFieldOfViewDeg(fieldOfView2));
        assertDoesNotThrow(() -> fieldOfView.setFieldOfViewDeg(fieldOfView2));
        assertEquals(fieldOfView.getFieldOfViewDeg(), fieldOfView2);
        assertThrows(ViewingParametersBeanTest.MethodCalledConfirmation.class, () -> fieldOfView.setFieldOfViewDeg(fieldOfView3));
        assertDoesNotThrow(() -> fieldOfView.setFieldOfViewDeg(fieldOfView3));
        assertEquals(fieldOfView.getFieldOfViewDeg(), fieldOfView3);
    }

    @Test
    void fieldOfViewRandomPropertyWorks() {
        ViewingParametersBean fieldOfView = new ViewingParametersBean();
        addModificationNotifier(fieldOfView.fieldOfViewDegProperty());

        SplittableRandom randGen = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double newFieldOfView = randGen.nextDouble();
            assertThrows(ViewingParametersBeanTest.MethodCalledConfirmation.class, () -> fieldOfView.setFieldOfViewDeg(newFieldOfView));
            assertDoesNotThrow(() -> fieldOfView.setFieldOfViewDeg(newFieldOfView));
            assertEquals(fieldOfView.getFieldOfViewDeg(), newFieldOfView);
        }
    }

    @Test
    void centerPropertyWorks() {
        ViewingParametersBean center = new ViewingParametersBean();
        addModificationNotifier(center.centerProperty());

        assertThrows(ViewingParametersBeanTest.MethodCalledConfirmation.class, () -> center.setCenter(coordinates1));
        assertDoesNotThrow(() -> center.setCenter(coordinates1));
        assertSame(center.getCenter(), coordinates1);
        assertThrows(ViewingParametersBeanTest.MethodCalledConfirmation.class, () -> center.setCenter(coordinates2));
        assertDoesNotThrow(() -> center.setCenter(coordinates2));
        assertSame(center.getCenter(), coordinates2);
        assertThrows(ViewingParametersBeanTest.MethodCalledConfirmation.class, () -> center.setCenter(coordinates3));
        assertDoesNotThrow(() -> center.setCenter(coordinates3));
        assertSame(center.getCenter(), coordinates3);
    }

    @Test
    void centerRandomValuesWorks() {
        ViewingParametersBean centerRandom = new ViewingParametersBean();
        addModificationNotifier(centerRandom.centerProperty());

        SplittableRandom randGen = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double azDeg = Angle.toDeg(randGen.nextDouble(AZ_INTERVAL.low(), AZ_INTERVAL.high()));
            double altDeg = Angle.toDeg(randGen.nextDouble(ALT_INTERVAL.low(), ALT_INTERVAL.high()));
            HorizontalCoordinates coordinates = HorizontalCoordinates.ofDeg(azDeg, altDeg);

            assertThrows(ViewingParametersBeanTest.MethodCalledConfirmation.class, () -> centerRandom.setCenter(coordinates));
            assertDoesNotThrow(() -> centerRandom.setCenter(coordinates));
            assertSame(centerRandom.getCenter(), coordinates);
        }

    }

    private static void addModificationNotifier(Observable property) {
        property.addListener((observable -> {
            throw new MethodCalledConfirmation();
        }));
    }

    static class MethodCalledConfirmation extends Error {

    }
}
