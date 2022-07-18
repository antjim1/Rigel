package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.internationalization.Translation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * various tests on StarCatalogue and StarCatalogue.Builder
 *
 * @author Léo Larigauderie (311045)
 * @author Saged Bounekhel (315803)
 */
class StarCatalogueTest3 {

    @Test
    void constructorFailsOnAsterismStarNotInStars() {
        Star s1 = new Star(1, Translation.constant("s1"), EquatorialCoordinates.of(0, 0), 0.2f, -0.3f);
        Star s2 = new Star(2, Translation.constant("s2"), EquatorialCoordinates.of(0, 0), 0.2f, -0.3f);
        Star s3 = new Star(3, Translation.constant("s3"), EquatorialCoordinates.of(0, 0), 0.2f, -0.3f);

        Asterism a1 = new Asterism(List.of(s1));
        Asterism a2 = new Asterism(List.of(s1, s3));

        List<Star> stars = List.of(s1, s2);

        assertThrows(IllegalArgumentException.class, () -> {
            new StarCatalogue(stars, List.of(a1, a2));
        });
    }

    @Test
    void builderStarsAndAsterismsAreEmptyUponConstruction() {
        StarCatalogue.Builder builder = new StarCatalogue.Builder();
        assertTrue(builder.stars().isEmpty());
        assertTrue(builder.asterisms().isEmpty());
    }

    @Test
    void builderAddStarAndAddAsterismWork() {
        Star s1 = new Star(1, Translation.constant("s1"), EquatorialCoordinates.of(0, 0), 0.2f, -0.3f);
        Asterism a1 = new Asterism(List.of(s1));
        StarCatalogue.Builder builder = new StarCatalogue.Builder();
        assertEquals(builder.addStar(s1), builder);
        assertEquals(builder.addAsterism(a1), builder);
        assertTrue(builder.stars().contains(s1));
        assertTrue(builder.asterisms().contains(a1));
    }

    @Test
    void buildWorks() {
        StarCatalogue.Builder builder = new StarCatalogue.Builder();

        Star s1 = new Star(1, Translation.constant("s1"), EquatorialCoordinates.of(0, 0), 0.2f, -0.3f);
        Star s2 = new Star(2, Translation.constant("s2"), EquatorialCoordinates.of(0, 0), 0.2f, -0.3f);
        Star s3 = new Star(3, Translation.constant("s3"), EquatorialCoordinates.of(0, 0), 0.2f, -0.3f);
        Star s4 = new Star(4, Translation.constant("s4"), EquatorialCoordinates.of(0, 0), 0.2f, -0.3f);
        builder.addStar(s1);
        builder.addStar(s2);
        builder.addStar(s3);
        builder.addStar(s4);

        Asterism a1 = new Asterism(List.of(s1, s2, s3));
        Asterism a2 = new Asterism(List.of(s1, s3));
        Asterism a3 = new Asterism(List.of(s2, s3));
        Asterism a4 = new Asterism(List.of(s3, s4));
        builder.addAsterism(a1);
        builder.addAsterism(a2);
        builder.addAsterism(a3);
        builder.addAsterism(a4);

        StarCatalogue catalogue = builder.build();

        assertTrue(catalogue.stars().contains(s1)
                   && catalogue.stars().contains(s2)
                   && catalogue.stars().contains(s3)
                   && catalogue.stars().contains(s4)
                   && catalogue.asterisms().contains(a1)
                   && catalogue.asterisms().contains(a2)
                   && catalogue.asterisms().contains(a3)
                   && catalogue.asterisms().contains(a4));
    }

    @Test
    void immutabilityOfStarsAndAsterisms() {
        Star s1 = new Star(1, Translation.constant("s1"), EquatorialCoordinates.of(0, 0), 0.2f, -0.3f);
        Star s2 = new Star(2, Translation.constant("s2"), EquatorialCoordinates.of(0, 0), 0.2f, -0.3f);
        Star s3 = new Star(3, Translation.constant("s3"), EquatorialCoordinates.of(0, 0), 0.2f, -0.3f);
        Star s4 = new Star(4, Translation.constant("s4"), EquatorialCoordinates.of(0, 0), 0.2f, -0.3f);

        List<Star> stars = new ArrayList<>();
        stars.add(s1);
        stars.add(s2);
        stars.add(s3);

        Asterism a1 = new Asterism(List.of(s1, s2, s3));
        Asterism a2 = new Asterism(List.of(s1, s3));
        Asterism a3 = new Asterism(List.of(s2, s3));
        Asterism a4 = new Asterism(List.of(s3));

        List<Asterism> asterisms = new ArrayList<>();
        asterisms.add(a1);
        asterisms.add(a2);
        asterisms.add(a3);

        StarCatalogue catalogue = new StarCatalogue(stars, asterisms);

        //tests stars
        //tests d'impossibilité de modification
        assertThrows(UnsupportedOperationException.class, () -> catalogue.stars().add(s4));
        assertThrows(UnsupportedOperationException.class, () -> catalogue.stars().remove(s1));
        assertThrows(UnsupportedOperationException.class, () -> catalogue.stars().clear());

        //tests de référence différente entre l'argument et l'attribut
        stars.remove(s1);
        assertEquals(3, catalogue.stars().size());
        stars.clear();
        assertFalse(catalogue.stars().isEmpty());
        stars.add(s4);
        assertFalse(catalogue.stars().contains(s4));


        //Tests asterisms
        //tests d'impossibilité de modification
        assertThrows(UnsupportedOperationException.class, () -> catalogue.asterisms().add(a4));
        assertThrows(UnsupportedOperationException.class, () -> catalogue.asterisms().remove(a2));
        assertThrows(UnsupportedOperationException.class, () -> catalogue.asterisms().clear());

        //tests de référence différente entre l'argument et l'attribut
        asterisms.remove(a1);
        assertEquals(3, catalogue.asterisms().size());
        asterisms.clear();
        assertFalse(catalogue.asterisms().isEmpty());
        asterisms.add(a4);
        assertFalse(catalogue.asterisms().contains(a4));
    }

    @Test
    void builderStarsAndAsterismsAreUnmodifiableButNotImmutable() {
        Star s1 = new Star(1, Translation.constant("s1"), EquatorialCoordinates.of(0, 0), 0.2f, -0.3f);
        Star s2 = new Star(2, Translation.constant("s2"), EquatorialCoordinates.of(0, 0), 0.2f, -0.3f);
        Star s3 = new Star(3, Translation.constant("s3"), EquatorialCoordinates.of(0, 0), 0.2f, -0.3f);
        Star s4 = new Star(4, Translation.constant("s4"), EquatorialCoordinates.of(0, 0), 0.2f, -0.3f);

        Asterism a1 = new Asterism(List.of(s1, s2, s3));
        Asterism a2 = new Asterism(List.of(s1, s3));
        Asterism a3 = new Asterism(List.of(s2, s3));
        Asterism a4 = new Asterism(List.of(s3));

        StarCatalogue.Builder builder = new StarCatalogue.Builder();
        builder.addStar(s1);
        builder.addStar(s2);
        builder.addAsterism(a1);
        builder.addAsterism(a2);

        List<Star> starsInBuilder = builder.stars();
        List<Asterism> asterismsInBuilder = builder.asterisms();

        //tests stars()
        //tests d'impossibilité de modification
        assertThrows(UnsupportedOperationException.class, () -> starsInBuilder.add(s4));
        assertThrows(UnsupportedOperationException.class, () -> starsInBuilder.remove(s1));
        assertThrows(UnsupportedOperationException.class, starsInBuilder::clear);

        //tests de même référence
        builder.addStar(s3);
        assertTrue(starsInBuilder.contains(s3));
        assertEquals(builder.stars().size(), starsInBuilder.size());


        //tests asterisms()
        //tests d'impossibilité de modification
        assertThrows(UnsupportedOperationException.class, () -> asterismsInBuilder.add(a4));
        assertThrows(UnsupportedOperationException.class, () -> asterismsInBuilder.remove(a1));
        assertThrows(UnsupportedOperationException.class, asterismsInBuilder::clear);

        //tests de même référence
        builder.addAsterism(a3);
        assertTrue(asterismsInBuilder.contains(a3));
        assertEquals(builder.asterisms().size(), asterismsInBuilder.size());
    }

}