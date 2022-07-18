package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.internationalization.Translation;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class StarCatalogueTest {
    List<Star> stars;
    List<Star> stars1;
    List<Asterism> asterisms;
    StarCatalogue catalogue;
    List<Integer> indexOfAsterims;

    @Test
    void signatureCheck() {
        Class<StarCatalogue> cls = StarCatalogue.class;
        List<ThrowingSupplier<Executable>> getMethods =
                List.of(() -> cls.getDeclaredConstructor(List.class, List.class),
                        () -> cls.getDeclaredMethod("stars"),
                        () -> cls.getDeclaredMethod("asterisms"),
                        () -> cls.getDeclaredMethod("asterismIndices",
                                                    Asterism.class),
                        () -> cls.getDeclaredMethod("asterismIndices",
                                                    Asterism.class));
        for (ThrowingSupplier<Executable> getMethod : getMethods) {
            assertDoesNotThrow(getMethod);
        }
    }

    @Test
    void builderStarsAndAsterismsAreEmptyUponConstruction() {
        StarCatalogue.Builder builder = new StarCatalogue.Builder();
        assertEquals(true, builder.stars().isEmpty());
        assertEquals(true, builder.asterisms().isEmpty());
    }

    @Test
    void RandomValues() {
        Star star = new Star(32, Translation.constant("try"),
                             EquatorialCoordinates.of(Angle.ofDeg(32), Angle.ofDeg(45)),
                             0, 2);
        stars = List.of(star);
        Star star2 = new Star(12, Translation.constant("test"),
                              EquatorialCoordinates.of(Angle.ofDeg(12), Angle.ofDeg(31)),
                              0, 3);

        asterisms = new ArrayList<>();
        asterisms.add(new Asterism(stars));

        catalogue = new StarCatalogue(stars, asterisms);

        assertEquals(stars, catalogue.stars());
        assertEquals(Set.copyOf(asterisms), catalogue.asterisms());
        assertEquals(asterisms.size(), catalogue.stars().size());
    }

    @Test
    void asterismHasAnAdditionalStar() {
        Star star = new Star(0, Translation.constant("Bes"),
                             EquatorialCoordinates.of(Angle.ofDeg(0), Angle.ofDeg(0)),
                             0, 2);
        Star star2 = new Star(0, Translation.constant("test"),
                              EquatorialCoordinates.of(Angle.ofDeg(0), Angle.ofDeg(0)),
                              0, 3);
        stars = List.of(star);
        stars1 = List.of(star, star2);
        asterisms = List.of(new Asterism(stars1));
        assertThrows(IllegalArgumentException.class, () -> new StarCatalogue(stars, asterisms));
    }

    private static final String HYG_CATALOGUE_NAME =
            "/hygdata_v3.csv";
    private static final String ASTERISM_CATALOGUE_NAME =
            "/asterisms.txt";

    @Test
    void rigelIsContain() throws IOException {
        try (InputStream hygStream = getClass().getResourceAsStream(HYG_CATALOGUE_NAME);
             InputStream asterismStream = getClass().getResourceAsStream(ASTERISM_CATALOGUE_NAME)) {
            catalogue = new StarCatalogue.Builder().loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                                                   .loadFrom(asterismStream, AsterismLoader.INSTANCE)
                                                   .build();
            Star rigel = null;
            for (int i = 0; i < catalogue.stars().size(); ++i) {
                if (catalogue.stars().get(i).name().equalsIgnoreCase("rigel"))
                    rigel = catalogue.stars().get(i);
            }
            assertNotNull(rigel);
            System.out.println("Hipparcodid of rigel: " + rigel.hipparcosId());
        }

    }
    @Test
    void readAndPrintLoadForm() throws IOException {
        try (InputStream hygStream = getClass().getResourceAsStream(HYG_CATALOGUE_NAME);
             InputStream asterismStream = getClass().getResourceAsStream(ASTERISM_CATALOGUE_NAME)) {
            catalogue = new StarCatalogue.Builder().loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                                                   .loadFrom(asterismStream, AsterismLoader.INSTANCE)
                                                   .build();
            stars = new ArrayList<Star>();
            stars.addAll(catalogue.stars());

            System.out.println("\r\n Stars: ");
            for (int i = 0; i < catalogue.stars().size(); ++i) {
                System.out.print(catalogue.stars().get(i).hipparcosId() + " ");
            }

            System.out.println("\r\n asterisms: ");
            int i;
            for(Asterism asterism : catalogue.asterisms()) {
               indexOfAsterims = catalogue.asterismIndices(asterism);
                i = 0;
                for(Star star : asterism.stars()){
              assertEquals(stars.get(indexOfAsterims.get(i)).hipparcosId(), star.hipparcosId());
                    i++;
                }
            }
            System.out.println();
            System.out.println("Number of stars: " + catalogue.stars().size());
            System.out.println("Number of asterims: " + catalogue.asterisms().size());


        }
    }

    @Test
    void sameNumberOfIndexForEveryAsterism() throws  IOException {
        try (InputStream hygStream = getClass().getResourceAsStream(HYG_CATALOGUE_NAME);
             InputStream asterismStream = getClass().getResourceAsStream(ASTERISM_CATALOGUE_NAME)) {
            catalogue = new StarCatalogue.Builder().loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                                                   .loadFrom(asterismStream, AsterismLoader.INSTANCE)
                                                   .build();
        }

        for (Asterism asterism : catalogue.asterisms()) {
            int sizeOfAsterism = catalogue.asterismIndices(asterism).size();
            int sizeOfStars = asterism.stars().size();
            assertEquals(sizeOfAsterism, sizeOfStars);
        }
    }

}
