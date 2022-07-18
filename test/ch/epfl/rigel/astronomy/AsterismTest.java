package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.internationalization.Translation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class AsterismTest {
    @Test
    void constructorThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Asterism(new ArrayList<>()));
    }

    @Test
    void asterismIsImmutable() {
        class Counter {
            private int i = 0;

            int increment() {
                return i++;
            }

            int get() {
                return i;
            }
        }

        List<Star> starList = new ArrayList<>();
        final Counter counter =
                new Counter();  // ensures that all stars are different in case Star::equals is reimplemented
        Supplier<Star> newStar =
                () -> new Star(counter.increment(), Translation.constant("dummy"), EquatorialCoordinates.of(0, 0), counter.get(), 0);
        starList.add(newStar.get());
        List<Star> initialList = List.copyOf(starList);
        Asterism asterism = new Asterism(starList);

        assertThrows(UnsupportedOperationException.class, () -> asterism.stars().add(newStar.get()));
        assertThrows(UnsupportedOperationException.class, () -> asterism.stars().set(0, newStar.get()));

        starList.add(newStar.get());
        starList.set(0, newStar.get());

        assertEquals(initialList, asterism.stars());  // WARNING will fail if the stars are copied, because Star.equals() returns true iff both stars are the same object
    }
}
