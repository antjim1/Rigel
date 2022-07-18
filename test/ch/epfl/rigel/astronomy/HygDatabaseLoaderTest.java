package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.internationalization.Translation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

import static ch.epfl.rigel.coordinates.CoordinateAssertions.assertEquals2;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
class HygDatabaseLoaderTest {
    private static final String HYG_CATALOGUE_NAME = "/hygdata_v3.csv";
    private static final String TESTER_CATALOGUE_NAME = "/test.txt";

    @Test
    void signatureCheck() {
        Class<HygDatabaseLoader> cls = HygDatabaseLoader.class;
        ThrowingSupplier<Object> getMethod =
                () -> cls.getDeclaredMethod("load", InputStream.class, StarCatalogue.Builder.class);
        assertDoesNotThrow(getMethod);
    }

    @Test
    void hygDatabaseIsCorrectlyInstalled() throws IOException {
        try (InputStream hygStream = getClass().getResourceAsStream(HYG_CATALOGUE_NAME)) {
            assertNotNull(hygStream);
        }
    }

    @Test
    void hygDatabaseContainsRigel() throws IOException {
        StarCatalogue catalogue;
        try (InputStream hygStream = getClass().getResourceAsStream(HYG_CATALOGUE_NAME)) {
            catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .build();
        }
        Star rigel = null;
        for (Star s : catalogue.stars()) {
            if (s.name().equalsIgnoreCase("rigel"))
                rigel = s;
        }
        assertNotNull(rigel);
    }

    @Test
    void StarWithoutName() throws IOException {
        try (InputStream hygStream = getClass().getResourceAsStream(HYG_CATALOGUE_NAME)) {
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .build();
            int i = 0;
            StarCatalogue.Builder builder = new StarCatalogue.Builder();
            for (Star star : builder.stars()) {
                if (star.name().charAt(0) == '?') {
                    i = 1;
                }
                assertEquals(' ', star.name().charAt(1)); // after ? (char 0) the next one is a space (char 1)
                assertEquals(1,i);
            }
        }
    }

    @Test
    void hygDataBaseContainsRandomLine() throws IOException {
        StarCatalogue catalogue;
        try (InputStream hygStream = getClass().getResourceAsStream(HYG_CATALOGUE_NAME)) {
            catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .build();
        }

        Star[] stars = {
                // first line
                new Star(88, Translation.constant("Tau Phe"),
                         EquatorialCoordinates.of(0.004696959812148889, -0.8518930353430763),
                         5.710f, 0.911f),

                new Star(5346, Translation.constant("? Psc"),
                         EquatorialCoordinates.of(0.2983203305746531, 0.09860794471610874),
                         5.510f, 0.334f),

                new Star(32349, Translation.constant("Sirius"),
                         EquatorialCoordinates.of(1.7677953696021995, -0.291751258517685),
                         -1.440f, 0.009f),

                new Star(0, Translation.constant("Gam CrA"),
                         EquatorialCoordinates.of(5.002160830314707, -0.6469017522138001),
                         5.000f, 0f),

                // last line
                new Star(0, Translation.constant("? Aqr"),
                         EquatorialCoordinates.of(6.064662769813043, -0.3919549465551),
                         5.900f, 0f)};

        for (Star star : stars) {
            Predicate<Star> filter = s -> s.name().equals(star.name())
                                          && s.hipparcosId() == star.hipparcosId();
            assertEquals(1, catalogue.stars()
                                     .stream()
                                     .filter(filter)
                                     .count());
            for (Star s : catalogue.stars()) {
                if (filter.test(s)) {
                    assertEquals(star.colorTemperature(), s.colorTemperature());
                    assertEquals(star.angularSize(), s.angularSize(), 1e-12);
                    assertEquals(star.info(), s.info());
                    assertEquals(star.name(), s.name());
                    assertEquals(star.magnitude(), s.magnitude(), 1e-12);
                    assertEquals2(star.equatorialPos(), s.equatorialPos(), 1e-12);
                }
            }
        }
    }


// //   void dataBaseWorksWithAnyFile() throws IOException {
//        String[] names = {"/stars_test2.csv", "/stars_test3.csv", "/stars_test4.csv"};
//        List<List<Star>> starsList =
//                List.of(List.of(new Star(88, "Tau Phe",
//                                         EquatorialCoordinates.of(0.004696959812148889, -0.8518930353430763),
//                                         5.710f, 0.911f)),
//                        List.of(),
//                        List.of(new Star(88, "Tau Phe",
//                                         EquatorialCoordinates.of(0.004696959812148889, -0.8518930353430763),
//                                         0, 0.911f)));
//
//        for (int i = 0; i < names.length; i++) {
//            String name = names[i];
//            List<Star> stars = starsList.get(i);
//
//            StarCatalogue catalogue;
//            try (InputStream hygStream = getClass().getResourceAsStream(name)) {
//                catalogue = new StarCatalogue.Builder()
//                        .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
//                        .build();
//            }
//
//            assertEquals(stars.size(), catalogue.stars().size());
//
//            List<Star> starsInCatalogue = catalogue.stars();
//            for (int j = 0; j < starsInCatalogue.size(); j++) {
//                Star star = starsInCatalogue.get(j);
//                Star expectedStar = stars.get(j);
//                assertEquals(expectedStar.colorTemperature(), star.colorTemperature());
//                assertEquals(expectedStar.angularSize(), star.angularSize(), 1e-12);
//                assertEquals(expectedStar.info(), star.info());
//                assertEquals(expectedStar.name(), star.name());
//                assertEquals(expectedStar.magnitude(), star.magnitude(), 1e-12);
//                assertEquals2(expectedStar.equatorialPos(), star.equatorialPos(), 1e-12);
//            }
//        }
//    }
}
