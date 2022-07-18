package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.CoordinateAssertions;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialToHorizontalConversion;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class ObservedSkyTest {
    private static final StarCatalogue CATALOGUE = initCatalogue();
    private static final SplittableRandom RAND = TestRandomizer.newRandom();
    private static final int ITERATIONS = 10;
    private final List<Planet> planets = new ArrayList<>(Collections.nCopies(PlanetModel.ALL.size() - 1, null));
    private final List<CartesianCoordinates> planetPositions = new ArrayList<>(Collections.nCopies(
            PlanetModel.ALL.size() - 1, null));
    private ObservedSky observedSky;
    private Sun sun;
    private CartesianCoordinates sunPosition;
    private Moon moon;
    private CartesianCoordinates moonPosition;
    private List<Star> stars;
    private List<CartesianCoordinates> starPositions;
    private Set<Asterism> asterisms;
    private Map<CelestialObject, CartesianCoordinates> objectToCoordinates;

    private static StarCatalogue initCatalogue() {
        try (InputStream stream = ObservedSkyTest.class.getResourceAsStream("/hygdata_v3.csv")) {
            return new StarCatalogue.Builder()
                    .loadFrom(stream, HygDatabaseLoader.INSTANCE)
                    .build();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void repeat(Runnable runnable) {
        for (int i = 0; i < ITERATIONS; ++i) {
            runnable.run();
        }
    }

    private static void assertEquals2(List<CartesianCoordinates> expected, double[] actual) {
        assertEquals(expected.size() * 2, actual.length);
        for (int i = 0; i < expected.size(); ++i) {
            CartesianCoordinates coordinates = expected.get(i);
            double x = actual[2 * i];
            double y = actual[2 * i + 1];
            assertEquals(coordinates.x(), x, 1e-9);
            assertEquals(coordinates.y(), y, 1e-9);
        }
    }

    private static <T extends CelestialObject> void assertEquals2(List<T> expected, List<T> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); ++i) {
            assertEquals2(expected.get(i), actual.get(i));
        }
    }

    private static void assertEquals2(Collection<Asterism> expected, Collection<Asterism> actual) {
        assertEquals(expected.size(), actual.size());
        Iterator<Asterism> it1 = expected.iterator();
        Iterator<Asterism> it2 = actual.iterator();

        while (it1.hasNext()) {
            assertEquals2(it1.next(), it2.next());
        }
    }

    private static void assertEquals2(CelestialObject expected, CelestialObject actual) {
        assertEquals(expected.getClass(), actual.getClass());
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.info(), actual.info());
        assertEquals(expected.toString(), actual.toString());
        assertEquals(expected.angularSize(), actual.angularSize());
        CoordinateAssertions.assertEquals2(expected.equatorialPos(), actual.equatorialPos(), 1e-12);
        assertEquals(expected.magnitude(), actual.magnitude());
    }

    private static void assertEquals2(CartesianCoordinates expected, CartesianCoordinates actual) {
        CoordinateAssertions.assertEquals2(expected, actual, 1e-12);
    }

    private static void assertEquals2(Asterism expected, Asterism actual) {
        assertEquals2(expected.stars(), actual.stars());
    }

    private void changeVariables() {
        var when = ZonedDateTime.of(LocalDate.of(RAND.nextInt(1950, 2050),
                                                 Month.values()[RAND.nextInt(Month.values().length)], RAND.nextInt(1, 29)),
                                    LocalTime.of(0, 0),
                                    ZoneOffset.UTC);
        var where = GeographicCoordinates.ofDeg(RAND.nextDouble(-180, 180), RAND.nextDouble(-90, 90));
        var center = HorizontalCoordinates.of(RAND.nextDouble(0, Angle.TAU), Angle.ofDeg(RAND.nextDouble(-90, 90)));

        updateVariables(when, where, center);
    }

    private void updateVariables(ZonedDateTime when, GeographicCoordinates where, HorizontalCoordinates center) {
        StereographicProjection projection = new StereographicProjection(center);
        double daysSinceJ2010 = Epoch.J2010.daysUntil(when);
        EclipticToEquatorialConversion toEquatorial = new EclipticToEquatorialConversion(when);
        EquatorialToHorizontalConversion toHorizontal = new EquatorialToHorizontalConversion(when, where);
        observedSky = new ObservedSky(when, where, projection, CATALOGUE);

        objectToCoordinates = new HashMap<>();

        sun = SunModel.SUN.at(daysSinceJ2010, toEquatorial);
        sunPosition = applyProjection(projection, toHorizontal, sun);
        objectToCoordinates.put(observedSky.sun(), observedSky.sunPosition());

        moon = MoonModel.MOON.at(daysSinceJ2010, toEquatorial);
        moonPosition = applyProjection(projection, toHorizontal, moon);
        objectToCoordinates.put(observedSky.moon(), observedSky.moonPosition());

        int i = 0;
        for (PlanetModel model : PlanetModel.ALL) {
            if (model == PlanetModel.EARTH) continue;
            Planet planet = model.at(daysSinceJ2010, toEquatorial);
            planets.set(i, planet);
            planetPositions.set(i, applyProjection(projection, toHorizontal, planet));
            ++i;
        }
        for (int i1 = 0; i1 < observedSky.planets().size(); i1++) {
            Planet planet = observedSky.planets().get(i1);
            CartesianCoordinates position = CartesianCoordinates.of(observedSky.planetPositions()[2 * i1],
                                                                    observedSky.planetPositions()[2 * i1 + 1]);
            objectToCoordinates.put(planet, position);
        }

        stars = CATALOGUE.stars();
        starPositions = new ArrayList<>(stars.size());
        for (Star star : stars) {
            CartesianCoordinates starPosition = applyProjection(projection, toHorizontal, star);
            starPositions.add(starPosition);
            objectToCoordinates.put(star, starPosition);
        }
        for (int i1 = 0; i1 < observedSky.stars().size(); ++i1) {
            Star star = observedSky.stars().get(i1);
            CartesianCoordinates position = CartesianCoordinates.of(observedSky.starPositions()[2 * i1],
                                                                    observedSky.starPositions()[2 * i1 + 1]);
            objectToCoordinates.put(star, position);
        }

        asterisms = CATALOGUE.asterisms();
    }

    private CartesianCoordinates applyProjection(StereographicProjection projection,
                                                 EquatorialToHorizontalConversion toHorizontal,
                                                 CelestialObject object) {
        var hor = toHorizontal.apply(object.equatorialPos());
        return projection.apply(hor);
    }

    @Test
    void sunWorks() {
        repeat(this::checkSun);
    }

    private void checkSun() {
        changeVariables();
        assertEquals2(sun, observedSky.sun());
        assertEquals2(sunPosition, observedSky.sunPosition());
    }

    @Test
    void moonWorks() {
        repeat(this::checkMoon);
    }

    private void checkMoon() {
        changeVariables();
        assertEquals2(moon, observedSky.moon());
        assertEquals2(moonPosition, observedSky.moonPosition());
    }

    @Test
    void planetsWorks() {
        repeat(this::checkPlanets);
    }

    private void checkPlanets() {
        changeVariables();
        assertEquals2(planets, observedSky.planets());
        assertEquals2(planetPositions, observedSky.planetPositions());
    }

    @Test
    void starsWorks() {
        repeat(this::checkStars);
    }

    private void checkStars() {
        changeVariables();
        assertEquals2(stars, observedSky.stars());
        assertEquals2(starPositions, observedSky.starPositions());
    }

    @Test
    void asterismsWorks() {
        repeat(this::checkAsterisms);
    }

    private void checkAsterisms() {
        changeVariables();
        assertEquals2(asterisms, observedSky.asterisms());
    }

    @Test
    void asterismIndicesWorks() {
        repeat(this::checkAsterismIndices);
    }

    private void checkAsterismIndices() {
        changeVariables();
        for (Asterism asterism : asterisms) {
            List<Integer> indices = CATALOGUE.asterismIndices(asterism);
            List<Star> stars = new ArrayList<>();
            for (int i : indices) {
                stars.add(this.stars.get(i));
            }
            assertEquals2(stars, asterism.stars());
        }
    }

    @Test
    void closestToWorks() {
        repeat(this::checkClosestTo);
    }

    private void checkClosestTo() {
        changeVariables();

        checkClosestTo(CartesianCoordinates.of(-0.019990908489805854, -0.6597789892665983), 1);
        checkClosestTo(CartesianCoordinates.of(-1.019990908489805854, -0.6597789892665983), 1);
        checkClosestTo(CartesianCoordinates.of(-0.01999, -0.6597), 1e-6);
        checkClosestTo(CartesianCoordinates.of(-0.3940270208384285, 0.02901093830525241), 1e-6);
    }

    private void checkClosestTo(CartesianCoordinates pointer, double maxDistance) {
        Collection<CartesianCoordinates> coordinates = new ArrayList<>();
        coordinates.addAll(planetPositions);
        coordinates.addAll(starPositions);
        coordinates.add(moonPosition);
        coordinates.add(sunPosition);
        BiFunction<CartesianCoordinates, CartesianCoordinates, CartesianCoordinates> diff =
                (u, v) -> CartesianCoordinates.of(v.x() - u.x(), v.y() - u.y());
        Function<CartesianCoordinates, Double> norm2 = v -> v.x() * v.x() + v.y() * v.y();
        Function<CartesianCoordinates, Double> d2 = c -> norm2.apply(diff.apply(c, pointer));

        Optional<CelestialObject> optional =
                observedSky.objectClosestTo(pointer, maxDistance, Set.of(CelestialObjectType.values()));
        boolean isFound = optional.isPresent();
        if (maxDistance <= 0) assertFalse(isFound);
        double x = 0;
        if (isFound) {
            CartesianCoordinates p = objectToCoordinates.get(optional.get());
            x = d2.apply(p);
            assertTrue(x < maxDistance);
        }
        for (CartesianCoordinates c : coordinates) {
            double y = d2.apply(c);
            if (isFound) assertTrue(x <= y);
            else assertTrue(y >= maxDistance * maxDistance);
        }
    }
}
