package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListConcatenationTest {
    @Test
    void iterationWorks() {
        List<Integer> l1 = List.of(1, 2, 3, 4, 5, 6);
        List<Integer> l2 = List.of(7, 8, 9);
        List<Integer> l3 = List.of(10, 11, 12, 13, 14, 15, 16, 17, 18);

        ListConcatenation<Integer> concatenation = new ListConcatenation<>(List.of(l1, l2, l3));

        Iterator<Integer> it = concatenation.iterator();
        for (int i = 1; i <= 18; ++i) {
            assertEquals(i, it.next());
        }

        int i = 1;
        for (Integer j : concatenation) {
            assertEquals(i++, j);
        }
    }

    @Test
    void getWorks() {
        List<Integer> l1 = List.of(1, 2, 3, 4, 5, 6);
        List<Integer> l2 = List.of(7, 8, 9);
        List<Integer> l3 = List.of(10, 11, 12, 13, 14, 15, 16, 17, 18);

        ListConcatenation<Integer> concatenation = new ListConcatenation<>(List.of(l1, l2, l3));

        for (int i = 0; i < 18; ++i) {
            assertEquals(i + 1, concatenation.get(i));
        }
        assertThrows(IndexOutOfBoundsException.class, () -> concatenation.get(18));
    }
}