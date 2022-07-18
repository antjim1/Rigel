package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialToHorizontalConversion;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;


/**
 * Calculates the projected position in the plane of all celestial objects with the exception of the Earth and the stars
 * in the catalogue
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class ObservedSky {
    private final EquatorialToHorizontalConversion toHorizontal;
    private final StereographicProjection projection;

    private final Sun sun;
    private final CartesianCoordinates sunPosition;

    private final Moon moon;
    private final CartesianCoordinates moonPosition;

    private final List<Planet> planets;  // excludes the earth
    private final Map<CelestialObjectIdentifier, Planet> planetMap;
    private final List<CartesianCoordinates> planetCoordinates;
    private final double[] planetPositions;  // even index for x, odd index for y

    private final StarCatalogue starCatalogue;
    private final List<CartesianCoordinates> starCoordinates;
    private final double[] starPositions;  // even index for x, odd index for y

    /**
     * Constructs a new {@code ObservedSky} with the given time, position and catalogue
     *
     * @param when          Time moment
     * @param where         Geographical Coordinates of the position
     * @param projection    Performs stereographic projections on horizontal and cartesian coordinates
     * @param starCatalogue The star and asterism database
     */
    public ObservedSky(ZonedDateTime when, GeographicCoordinates where,
                       StereographicProjection projection, StarCatalogue starCatalogue) {
        double daysSinceJ2010 = Epoch.J2010.daysUntil(when);
        EclipticToEquatorialConversion toEquatorial = new EclipticToEquatorialConversion(when);
        toHorizontal = new EquatorialToHorizontalConversion(when, where);
        this.projection = projection;

        List<Star> stars = starCatalogue.stars();
        int starCount = stars.size();
        int planetCount = PlanetModel.ALL.size() - 1;  // -1, because the earth is excluded

        // sun
        sun = SunModel.SUN.at(daysSinceJ2010, toEquatorial);
        sunPosition = computePosition(sun);

        // moon
        moon = MoonModel.MOON.at(daysSinceJ2010, toEquatorial);
        moonPosition = computePosition(moon);

        // planets
        planetPositions = new double[2 * planetCount];
        List<Planet> tmpPlanets = new ArrayList<>();
        Map<CelestialObjectIdentifier, Planet> tmpPlanetMap = new HashMap<>();
        List<CartesianCoordinates> tmpPlanetCoordinates = new ArrayList<>();
        initPlanets(tmpPlanets, tmpPlanetMap, planetPositions, tmpPlanetCoordinates,
                    daysSinceJ2010, toEquatorial, this::computePosition);
        planets = Collections.unmodifiableList(tmpPlanets);
        planetMap = Collections.unmodifiableMap(tmpPlanetMap);
        planetCoordinates = Collections.unmodifiableList(tmpPlanetCoordinates);

        // stars
        this.starCatalogue = starCatalogue;
        starPositions = new double[2 * starCount];
        List<CartesianCoordinates> tmpStarCoordinates = new ArrayList<>();
        initStars(stars, starPositions, tmpStarCoordinates, this::computePosition);
        starCoordinates = Collections.unmodifiableList(tmpStarCoordinates);
    }

    /**
     * Creates planets - earth excluded - and adds them to different lists
     *
     * @param planets          list where to store the planets (earth excluded)
     * @param planetPositions  array where to store the cartesian coordinates of the planets
     * @param daysSinceJ2010   number of days since the J2010 epoch
     * @param toEquatorial     object to use to convert ecliptic coordinates to equatorial ones
     * @param computePosition  function to use to compute the cartesian coordinates of the planets
     */
    private static void initPlanets(List<Planet> planets, Map<CelestialObjectIdentifier, Planet> planetMap,
                                    double[] planetPositions, List<CartesianCoordinates> planetCoordinates,
                                    double daysSinceJ2010, EclipticToEquatorialConversion toEquatorial,
                                    Function<CelestialObject, CartesianCoordinates> computePosition) {
        int i = 0;  // index of the planet when removing the earth
        for (PlanetModel planetModel : PlanetModel.ALL) {
            if (planetModel == PlanetModel.EARTH) continue;  // ignore the earth

            Planet planet = planetModel.at(daysSinceJ2010, toEquatorial);
            planets.add(planet);
            planetMap.put(planet.identifier(), planet);

            CartesianCoordinates planetPosition = computePosition.apply(planet);
            addCoordinates(planetPosition, i, planetPositions);
            planetCoordinates.add(planetPosition);

            ++i;  // does not increment for the earth
        }
    }

    /**
     * Adds the given coordinates at the given position in an array of the following format :<br> {@code [x0, y0, x1,
     * y1, ..., xn-1, yn-1]}
     *
     * @param coordinates     cartesian coordinates to be added
     * @param coordinateIndex index of the {@code CartesianCoordinates} object - i.e. the index it would have if it were
     *                        stored in a {@code List<CartesianCoordinates>}. The index conversion is done internally
     * @param positions       array storing all the coordinates
     */
    private static void addCoordinates(CartesianCoordinates coordinates, int coordinateIndex, double[] positions) {
        int i = 2 * coordinateIndex;
        positions[i] = coordinates.x();
        positions[i + 1] = coordinates.y();
    }

    /**
     * Calculates the projected position of the given object
     *
     * @param object the {@code CelestialObject} from which to calculate the position
     *
     * @return the projected position of the object
     */
    public CartesianCoordinates computePosition(CelestialObject object) {
        HorizontalCoordinates horizontalPosition = toHorizontal.apply(object.equatorialPos());
        return projection.apply(horizontalPosition);
    }

    /**
     * Creates stars and adds them to different lists
     *
     * @param stars            list where to store the stars
     * @param starPositions    list where to store the cartesian coordinates of the stars
     * @param computePosition  function to use to compute the cartesian coordinates of the stars
     */
    private static void initStars(List<Star> stars, double[] starPositions,
                                  List<CartesianCoordinates> starCoordinates,
                                  Function<CelestialObject, CartesianCoordinates> computePosition) {
        for (int i = 0; i < stars.size(); ++i) {
            Star star = stars.get(i);

            CartesianCoordinates starPosition = computePosition.apply(star);
            addCoordinates(starPosition, i, starPositions);
            starCoordinates.add(starPosition);
        }
    }

    /**
     * Gives the instance of Sun
     *
     * @return the Sun in the form of an instance of Sun
     */
    public Sun sun() {
        return sun;
    }

    /**
     * Gives the position of sun
     *
     * @return the cartesian coordinates of sun
     */
    public CartesianCoordinates sunPosition() {
        return sunPosition;
    }

    /**
     * Gives the instance of moon
     *
     * @return the moon in the form of an instance of moon
     */
    public Moon moon() {
        return moon;
    }

    /**
     * Gives the position of moon
     *
     * @return the cartesian coordinates of moon
     */
    public CartesianCoordinates moonPosition() {
        return moonPosition;
    }

    /**
     * Gives the list of planets
     *
     * @return a list of planets with the exception of earth
     */
    public List<Planet> planets() {
        return planets;
    }

    /**
     * Gives the planet matching the identifier.
     *
     * @param identifier the identifier of the planet
     * @return the planet matching the identifier.
     * @throws NullPointerException if the given identifier does not designate a planet or designates the earth
     */
    public Planet planet(CelestialObjectIdentifier identifier) {    // BONUS MODIFICATION: helps finding a given planet
        Preconditions.checkArgument(planetMap.containsKey(identifier));
        return planetMap.get(identifier);
    }

    /**
     * Gives the positions of planets
     *
     * @return a clone of the list with the coordinates of planets where even positions represent x-coordinates  and odd
     * ones represent y-coordinates
     */
    public double[] planetPositions() {
        return planetPositions.clone();
    }

    /**
     * Gives the list of stars
     *
     * @return a list of stars from the catalogue
     */
    public List<Star> stars() {
        return starCatalogue.stars();
    }

    /**
     * Gives the positions of stars
     *
     * @return a clone of the list with the coordinates of stars, where even positions represent x-coordinates and odd
     * ones represent y-coordinates
     */
    public double[] starPositions() {
        return starPositions.clone();
    }

    /**
     * Gives the set of asterism
     *
     * @return a set of the asterism from the catalogue
     */
    public Set<Asterism> asterisms() {
        return starCatalogue.asterisms();
    }

    /**
     * Gives the the indices of all stars
     *
     * @return a list of the indices of the stars from the catalogue
     */
    public List<Integer> starIndexes(Asterism asterism) {
        return starCatalogue.asterismIndices(asterism);
    }

    private static int findClosestObjectIndex(ToDoubleFunction<CartesianCoordinates> distanceSquared,
                                              double maxDistance,
                                              ListConcatenation<CartesianCoordinates> coordinates) {
        double maxDistanceSquared = maxDistance * maxDistance;
        // find the index of the closest object using the coordinates
        double minDistanceSquared = distanceSquared.applyAsDouble(coordinates.get(0));
        int closestObjectIndex = minDistanceSquared <= maxDistanceSquared ? 0 : -1;
        int i = 0;
        for (CartesianCoordinates coordinate : coordinates) {
            double currentDistanceSquared = distanceSquared.applyAsDouble(coordinate);

            if (currentDistanceSquared <= maxDistanceSquared && currentDistanceSquared < minDistanceSquared) {
                minDistanceSquared = currentDistanceSquared;
                closestObjectIndex = i;
            }

            ++i;
        }
        return closestObjectIndex;
    }

    // BONUS MODIFICATION: allows to efficiently merge lists when calculating the closest object
    private ListConcatenation<CartesianCoordinates>
    concatenateVisiblesCoordinates(Set<CelestialObjectType> visibleTypes) {
        List<List<? extends CartesianCoordinates>> coordinateLists = new ArrayList<>();
        for (CelestialObjectType type : visibleTypes) {
            switch (type) {
                case STAR:
                    coordinateLists.add(starCoordinates);
                    break;
                case SUN:
                    coordinateLists.add(List.of(sunPosition));
                    break;
                case MOON:
                    coordinateLists.add(List.of(moonPosition));
                    break;
                case PLANET:
                    coordinateLists.add(planetCoordinates);
                    break;
            }
        }

        return new ListConcatenation<>(coordinateLists);
    }

    // BONUS MODIFICATION: allows to efficiently merge lists when calculating the closest object
    private ListConcatenation<CelestialObject> concatenateVisibleObjects(Set<CelestialObjectType> visibleTypes) {
        List<List<? extends CelestialObject>> objectLists = new ArrayList<>();
        for (CelestialObjectType type : visibleTypes) {
            switch (type) {
                case STAR:
                    objectLists.add(stars());
                    break;
                case SUN:
                    objectLists.add(List.of(sun));
                    break;
                case MOON:
                    objectLists.add(List.of(moon));
                    break;
                case PLANET:
                    objectLists.add(planets);
                    break;
            }
        }

        return new ListConcatenation<>(objectLists);
    }

    /**
     * Gives the closet celestial object to the point given
     *
     * @param point       the coordinates of a point on the plane
     * @param maxDistance the maximum distance allowed for the closest object
     *
     * @return the closest celestial object to that point, as long as it is within the maximum distance
     */
    // BONUS MODIFICATION: takes into account the visibility of the objects
    public Optional<CelestialObject> objectClosestTo(CartesianCoordinates point, double maxDistance,
                                                     Set<CelestialObjectType> visibleTypes) {
        // since we compute the square of the distance, we must treat negative distances separately
        if (maxDistance < 0) return Optional.empty();

        // function to compute the vector going from p1 to p2
        BiFunction<CartesianCoordinates, CartesianCoordinates, CartesianCoordinates> difference =
                (p1, p2) -> CartesianCoordinates.of(p2.x() - p1.x(), p2.y() - p1.y());

        // function to compute the square of the norm of a vector
        ToDoubleFunction<CartesianCoordinates> normSquared = v -> v.x() * v.x() + v.y() * v.y();

        // function to compute the square of the distance between p and the point of interest
        ToDoubleFunction<CartesianCoordinates> distanceSquared =
                p -> normSquared.applyAsDouble(difference.apply(p, point));

        // find closest object
        ListConcatenation<CartesianCoordinates> visibleCoordinates = concatenateVisiblesCoordinates(visibleTypes);
        int i = findClosestObjectIndex(distanceSquared, maxDistance, visibleCoordinates);

        // if the distance is too big, return nothing
        if (i < 0) return Optional.empty();

        return Optional.of(concatenateVisibleObjects(visibleTypes).get(i));
    }
}
