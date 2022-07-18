package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
class AsterismLoaderTest {
    private static final String ASTERISM_CATALOGUE_NAME = "/asterisms.txt";
    private static final String HYG_DATABASE_CATALOGUE_NAME = "/hygdata_v3.csv";

    @Test
    void signatureCheck() {
        Class<AsterismLoader> cls = AsterismLoader.class;
        ThrowingSupplier<Object> getMethod =
                () -> cls.getDeclaredMethod("load", InputStream.class, StarCatalogue.Builder.class);
        assertDoesNotThrow(getMethod);
    }

    @Test
    void asterismsIsCorrectlyInstalled() throws IOException {
        try (InputStream asterismsStream = getClass().getResourceAsStream(ASTERISM_CATALOGUE_NAME)) {
            assertNotNull(asterismsStream);
        }
    }

    @Test
    void asterismContainsRandomLine() throws IOException {
        StarCatalogue catalogue;
        try (InputStream hygStream = getClass().getResourceAsStream(HYG_DATABASE_CATALOGUE_NAME);
             InputStream asterismStream = getClass().getResourceAsStream(ASTERISM_CATALOGUE_NAME)) {
            catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .loadFrom(asterismStream, AsterismLoader.INSTANCE)
                    .build();
        }

        Set<Set<Integer>> idSets = Set.of(
                Set.of(7607, 4436, 2912, 677, 3092, 5447, 9640),  // first line
                Set.of(23972, 23875, 22701, 21444, 19587, 18543, 17593, 17378, 16537, 13701,
                       12843, 14146, 15474, 16611, 17651, 18216, 18673, 21248, 21393, 20535,
                       20042, 17874, 17797, 15510, 13847, 12486, 11407, 10602, 9007, 7588),
                Set.of(97886, 95771, 94703)); // last line

        for (Set<Integer> idSet : idSets) {
            Predicate<Asterism> checkIds = asterism -> asterism.stars()
                                                               .stream()
                                                               .map(Star::hipparcosId)
                                                               .collect(Collectors.toSet())
                                                               .equals(idSet);
            assertTrue(catalogue.asterisms().stream().anyMatch(checkIds));
        }
    }

    @Test
    void asterismLoader() throws IOException {
        try (InputStream hygStream = getClass().getResourceAsStream(HYG_DATABASE_CATALOGUE_NAME);
             InputStream asterismStream = getClass().getResourceAsStream(ASTERISM_CATALOGUE_NAME)) {
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .loadFrom(asterismStream, AsterismLoader.INSTANCE)
                    .build();

            Queue<Asterism> a = new ArrayDeque<>();
            Star beltegeuse = null;
            for (Asterism ast : catalogue.asterisms()) {
                for (Star s : ast.stars()) {
                    if (s.name().equalsIgnoreCase("Rigel")) {
                        a.add(ast);
                    }
                }
            }
            int astCount = 0;
            for (Asterism ast : a) {
                ++astCount;
                for (Star s : ast.stars()) {
                    if (s.name().equalsIgnoreCase("Betelgeuse")) {
                        beltegeuse = s;
                    }
                }
            }
            assertNotNull(beltegeuse);
            assertEquals(2, astCount);

        }


    }
}
