package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class loading asterisms from a stream
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public enum AsterismLoader implements StarCatalogue.Loader {
    INSTANCE;

    /**
     * Maps stars to their respective hipparcos ID if the latter is non-zero
     *
     * @param stars collection of stars
     *
     * @return unmodifiable map between the provided stars and their ID
     */
    private static Map<Integer, Star> mapHipparcosIds(Collection<Star> stars) {
        Map<Integer, Star> map = new HashMap<>();
        for (Star star : stars) {
            int id = star.hipparcosId();
            if (id != 0) {  // ignore stars with an ID of 0
                map.put(id, star);
            }
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * Creates new asterisms from the data in the given stream and adds them to the given {@code StarCatalogue.Builder}
     * All the stars from the catalogue must be loaded before calling this method.
     *
     * @param inputStream stream containing the hipparcos IDs of the stars in each asterism
     * @param builder     builder to which to add the created asterisms
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        Map<Integer, Star> idMap = mapHipparcosIds(builder.stars());  // assume the stars were loaded before
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII))) {
            String line;
            while ((line = reader.readLine()) != null) {  // read lines one by one
                String[] columns = line.split(",");
                // read the hipparcos IDs from the line
                List<Star> asterismStars = new ArrayList<>(columns.length);
                for (String column : columns) {
                    int id = Integer.parseInt(column);
                    asterismStars.add(idMap.get(id));
                }

                builder.addAsterism(new Asterism(asterismStars));
            }
        }
    }
}
