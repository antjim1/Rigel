package ch.epfl.rigel.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class NamedTimeAcceleratorTest {
    @Test
    void toStringReturnsName() {
        for (NamedTimeAccelerator namedTimeAccelerator : NamedTimeAccelerator.values()) {
            assertEquals(namedTimeAccelerator.getName(), namedTimeAccelerator.toString());
        }
    }
}