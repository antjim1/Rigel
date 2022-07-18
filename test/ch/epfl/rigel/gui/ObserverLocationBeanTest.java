package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import ch.epfl.test.TestRandomizer;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class ObserverLocationBeanTest {

    private static final double lonDeg1 = 60;
    private static final double lonDeg2 = 179;
    private static final double lonDeg3 = -124;

    private static final double latDeg1 = 60;
    private static final double latDeg2 = 45;
    private static final double latDeg3 = 90;

    private static final GeographicCoordinates coordinates2 = GeographicCoordinates.ofDeg(-176, -89);
    private static final GeographicCoordinates coordinates3 = GeographicCoordinates.ofDeg(65, 87);
    private static final GeographicCoordinates coordinates4 = GeographicCoordinates.ofDeg(5, 67);

    private static final RightOpenInterval LONGITUDE_INTERVAL = RightOpenInterval.symmetric(Angle.TAU);
    private static final ClosedInterval LATITUDE_INTERVAL = ClosedInterval.symmetric(Angle.TAU / 2.0);

    @Test
    void lonDegPropertyWorks() {
        ObserverLocationBean lonDeg = new ObserverLocationBean();
        addModificationNotifier(lonDeg.lonDegProperty());

        assertThrows(ObserverLocationBeanTest.MethodCalledConfirmation.class, () -> lonDeg.setLonDeg(lonDeg1));
        assertDoesNotThrow(() -> lonDeg.setLonDeg(lonDeg1));
        assertEquals(lonDeg.getLonDeg(), lonDeg1);
        assertThrows(ObserverLocationBeanTest.MethodCalledConfirmation.class, () -> lonDeg.setLonDeg(lonDeg2));
        assertDoesNotThrow(() -> lonDeg.setLonDeg(lonDeg2));
        assertEquals(lonDeg.getLonDeg(), lonDeg2);
        assertThrows(ObserverLocationBeanTest.MethodCalledConfirmation.class, () -> lonDeg.setLonDeg(lonDeg3));
        assertDoesNotThrow(() -> lonDeg.setLonDeg(lonDeg3));
        assertEquals(lonDeg.getLonDeg(), lonDeg3);
    }

    @Test
    void lonDegPropertyOnRandomValuesWorks() {
        ObserverLocationBean lonDeg = new ObserverLocationBean();
        addModificationNotifier(lonDeg.lonDegProperty());

        SplittableRandom randGen = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double randomLonDeg = randGen.nextDouble();

            assertThrows(ObserverLocationBeanTest.MethodCalledConfirmation.class, () -> lonDeg.setLonDeg(randomLonDeg));
            assertDoesNotThrow(() -> lonDeg.setLonDeg(randomLonDeg));
            Assertions.assertEquals(lonDeg.getLonDeg(), randomLonDeg);
        }
    }

    @Test
    void latDegPropertyWorks() {
        ObserverLocationBean latDeg = new ObserverLocationBean();
        addModificationNotifier(latDeg.latDegProperty());

        assertThrows(ObserverLocationBeanTest.MethodCalledConfirmation.class, () -> latDeg.setLatDeg(latDeg1));
        assertDoesNotThrow(() -> latDeg.setLatDeg(latDeg1));
        assertEquals(latDeg.getLatDeg(), latDeg1);
        assertThrows(ObserverLocationBeanTest.MethodCalledConfirmation.class, () -> latDeg.setLatDeg(latDeg2));
        assertDoesNotThrow(() -> latDeg.setLatDeg(latDeg2));
        assertEquals(latDeg.getLatDeg(), latDeg2);
        assertThrows(ObserverLocationBeanTest.MethodCalledConfirmation.class, () -> latDeg.setLatDeg(latDeg3));
        assertDoesNotThrow(() -> latDeg.setLatDeg(latDeg3));
        assertEquals(latDeg.getLatDeg(), latDeg3);
    }

    @Test
    void latDegPropertyOnRandomValuesWorks() {
        ObserverLocationBean latDeg = new ObserverLocationBean();
        addModificationNotifier(latDeg.latDegProperty());

        SplittableRandom randGen = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double randomLatDeg = randGen.nextDouble();

            assertThrows(ObserverLocationBeanTest.MethodCalledConfirmation.class, () -> latDeg.setLatDeg(randomLatDeg));
            assertDoesNotThrow(() -> latDeg.setLatDeg(randomLatDeg));
            Assertions.assertEquals(latDeg.getLatDeg(), randomLatDeg);
        }
    }

    private static void addModificationNotifier(Property<?> property) {
        property.addListener(modificationNotifier());
    }

    @Test
    void coordinatesBindingOnRandomValuesWorks() {
        ObserverLocationBean geographicCoordinates = new ObserverLocationBean();
        addModificationNotifier(geographicCoordinates.lonDegProperty());

        SplittableRandom randGen = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double lonDeg = Angle.toDeg(randGen.nextDouble(LONGITUDE_INTERVAL.low(), LONGITUDE_INTERVAL.high()));
            double latDeg = Angle.toDeg(randGen.nextDouble(LATITUDE_INTERVAL.low(), LATITUDE_INTERVAL.high()));
            GeographicCoordinates coordinates = GeographicCoordinates.ofDeg(lonDeg, latDeg);

            assertThrows(ObserverLocationBeanTest.MethodCalledConfirmation.class, () -> geographicCoordinates.setCoordinates(coordinates));
            assertDoesNotThrow(() -> geographicCoordinates.setCoordinates(coordinates));
            Assertions.assertEquals(geographicCoordinates.getCoordinates().toString(), coordinates.toString());
        }
    }

    @Test
    void coordinatesBindingWorks() {
        ObserverLocationBean coordinates = new ObserverLocationBean();
        addModificationNotifier(coordinates.lonDegProperty());

        assertThrows(ObserverLocationBeanTest.MethodCalledConfirmation.class, () -> coordinates.setCoordinates(coordinates2));
        System.out.println(coordinates.getCoordinates());
        assertDoesNotThrow(() -> coordinates.setLatDeg(coordinates2.latDeg()));
        System.out.println(coordinates.getCoordinates());
        assertDoesNotThrow(() -> coordinates.setLonDeg(coordinates2.lonDeg()));
        Assertions.assertEquals(coordinates.getCoordinates().toString(), coordinates2.toString());

        assertThrows(ObserverLocationBeanTest.MethodCalledConfirmation.class, () -> coordinates.setCoordinates(coordinates3));
        assertDoesNotThrow(() -> coordinates.setCoordinates(coordinates3));
        assertDoesNotThrow(() -> coordinates.setLonDeg(coordinates3.lonDeg()));
        assertDoesNotThrow(() -> coordinates.setLatDeg(coordinates3.latDeg()));
        Assertions.assertEquals(coordinates.getCoordinates().toString(), coordinates3.toString());

        assertThrows(ObserverLocationBeanTest.MethodCalledConfirmation.class, () -> coordinates.setCoordinates(coordinates4));
        assertDoesNotThrow(() -> coordinates.setCoordinates(coordinates4));
        Assertions.assertEquals(coordinates.getCoordinates().toString(), coordinates4.toString());
    }

    static class MethodCalledConfirmation extends Error {
    }

    private static <T> ChangeListener<T> modificationNotifier() {
        return (p, o, n) -> {
            throw new ObserverLocationBeanTest.MethodCalledConfirmation();
        };
    }

    private static void addModificationNotifier(ObjectBinding<?> binding) {
        binding.addListener(modificationNotifier());
    }


}
