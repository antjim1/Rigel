package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a star and asterism database
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class StarCatalogue {
    private final List<Star> stars;
    private final Map<Asterism, List<Integer>> asterismIndices;

    /**
     * Constructs a new {@code StarCatalogue} with the given stars and asterisms
     *
     * @param stars     list of the stars to store in the catalogue
     * @param asterisms list of the asterisms to store in the catalogue
     *
     * @throws IllegalArgumentException if a star of an asterism is not in the given list of stars
     */
    public StarCatalogue(List<Star> stars, List<Asterism> asterisms) {
        this.stars = List.copyOf(stars);

        Map<Star, Integer> starIndexes = new HashMap<>();
        for (int i = 0; i < stars.size(); ++i) starIndexes.put(stars.get(i), i);

        Map<Asterism, List<Integer>> asterismIndices = new HashMap<>();

        for (Asterism asterism : asterisms) {
            List<Integer> indices = new ArrayList<>();
            for (Star star : asterism.stars()) {
                Integer i = starIndexes.get(star);  // null if not found
                Preconditions.checkArgument(i != null);
                indices.add(i);
            }

            asterismIndices.put(asterism, Collections.unmodifiableList(indices));
        }

        this.asterismIndices = Collections.unmodifiableMap(asterismIndices);
    }

    /**
     * Gives a list of the stars contained in the catalogue
     *
     * @return list of the stars contained in the catalogue
     */
    public List<Star> stars() {
        return stars;
    }

    /**
     * Gives the asterisms contained in the catalogue
     *
     * @return set of the asterisms contained in the catalogue
     */
    public Set<Asterism> asterisms() {
        return asterismIndices.keySet();
    }

    /**
     * Gives the index in the catalogue of each star contained in the given asterism.
     *
     * @param asterism asterism from which to search the index of the stars
     *
     * @return list of the indices of the stars in the given asterism
     *
     * @throws IllegalArgumentException if the given asterism is not in the catalogue
     */
    public List<Integer> asterismIndices(Asterism asterism) {
        Preconditions.checkArgument(asterismIndices.containsKey(asterism));
        return asterismIndices.get(asterism);
    }

    /**
     * Represents objects that can use a stream of data to build a catalogue
     */
    public interface Loader {
        void load(InputStream inputStream, Builder builder) throws IOException;
    }

    /**
     * Builds a catalogue incrementally
     */
    public static final class Builder {
        private final List<Star> stars;
        private final List<Asterism> asterisms;

        /**
         * Constructs a new {StarCatalogue.Builder}
         */
        public Builder() {
            stars = new ArrayList<>();
            asterisms = new ArrayList<>();
        }

        /**
         * Adds a star to the catalogue in construction
         *
         * @param star star to add to the catalogue
         *
         * @return a reference to this object
         */
        public Builder addStar(Star star) {
            stars.add(star);
            return this;
        }

        /**
         * Gives the stars of the catalogue in construction
         *
         * @return unmodifiable view of the list of stars in the builder
         */
        public List<Star> stars() {
            return Collections.unmodifiableList(stars);
        }

        /**
         * Adds an asterism to the catalogue in construction
         *
         * @param asterism asterism to add to the catalogue
         *
         * @return a reference to this object
         */
        public Builder addAsterism(Asterism asterism) {
            asterisms.add(asterism);
            return this;
        }

        /**
         * Gives the asterisms in the catalogue in construction
         *
         * @return unmodifiable view of the list of asterisms in the builder
         */
        public List<Asterism> asterisms() {
            return Collections.unmodifiableList(asterisms);
        }

        /**
         * Adds data from the given stream to the catalogue in construction using the given loader
         *
         * @param inputStream stream of data to add to the catalogue
         * @param loader      object adding the data to the catalogue
         *
         * @return a reference to this object
         *
         * @throws IOException if an I/O error occurs
         */
        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
            loader.load(inputStream, this);
            return this;
        }

        /**
         * Builds a catalogue from the data stored in the builder
         *
         * @return new catalogue with the stars and asterisms stored in the builder
         */
        public StarCatalogue build() {
            return new StarCatalogue(stars, asterisms);
        }
    }
}