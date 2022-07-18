package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.internationalization.Translation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Function;

/**
 * Class loading stars from a stream
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public enum HygDatabaseLoader implements StarCatalogue.Loader {
    INSTANCE;

    /**
     * Creates new stars with the data from the given stream and adds them to the given builder
     *
     * @param inputStream stream providing the data to construct the stars
     * @param builder     builder to which to add the stars
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            reader.readLine(); // skip the first line
            while ((line = reader.readLine()) != null) {  // read lines one by one
                String[] elements = line.split(",");

                // extract ID (default = 0)
                int hipparcosId = extractValue(elements, Column.HIP, Integer::parseInt, 0);

                String constellation = elements[Column.CON.ordinal()];

                // function used to generate the default name for the star in case the `name column` is empty
                // returns `bayer column` ("?" if absent) + " " + `constellation column`
                Function<String[], String> defaultNameConstructor =
                        (values) -> extractValue(values, Column.BAYER, "?") + " " + constellation;

                String name = extractValue(elements, Column.PROPER, defaultNameConstructor);

                double ra = Double.parseDouble(elements[Column.RARAD.ordinal()]);
                double dec = Double.parseDouble(elements[Column.DECRAD.ordinal()]);

                // extract magnitude (default = 0)
                float magnitude = extractValue(elements, Column.MAG, Float::parseFloat, 0.f);

                // extract B-V color index (default = 0)
                float colorIndex = extractValue(elements, Column.CI, Float::parseFloat, 0.f);

                EquatorialCoordinates coordinates = EquatorialCoordinates.of(ra, dec);
                Star star = new Star(hipparcosId, Translation.constant(name), coordinates, magnitude, colorIndex);

                builder.addStar(star);
            }
        }
    }

    /**
     * Extracts a value of the given type located at the given column in the given list. The provided list must
     * represent the entries of the star catalogue's associated .csv file.
     *
     * @param elements              list storing the entries of a line of the .csv file containing the stars
     * @param column                enum value representing the column in which the entry of interest is located
     * @param converter             function converting the entry from {@code String} to the given type
     * @param defaultValueGenerator function constructing the default value in case the entry is empty
     * @param <T>                   type of the value to extract
     *
     * @return data stored at the given column converted to the given type or a default value constructed using the
     * given function
     */
    private <T> T extractValue(String[] elements, Column column, Function<String, T> converter,
                               Function<String[], T> defaultValueGenerator) {
        String element = elements[column.ordinal()];
        if (element.isEmpty()) return defaultValueGenerator.apply(elements);
        return converter.apply(element);
    }

    /**
     * Extracts a value of the given type located at the given column in the given list. The provided list must
     * represent the entries of the star catalogue's associated .csv file.
     *
     * @param elements     list storing the entries of a line of the .csv file containing the stars
     * @param column       enum value representing the column in which the entry of interest is located
     * @param converter    function converting the entry from {@code String} to the given type
     * @param defaultValue value to use in case the entry is empty
     * @param <T>          type of the value to extract
     *
     * @return data stored at the given column converted to the given type or the given default value
     */
    private <T> T extractValue(String[] elements, Column column, Function<String, T> converter, T defaultValue) {
        return extractValue(elements, column, converter, (Function<String[], T>) foo -> defaultValue);
    }

    /**
     * Extracts a string located at the given column in the given list. The provided list must represent the entries of
     * the star catalogue's associated .csv file.
     *
     * @param elements              list storing the entries of a line of the .csv file containing the stars
     * @param column                enum value representing the column in which the entry of interest is located
     * @param defaultValueGenerator function constructing the default value in case the entry is empty
     *
     * @return data stored at the given column or a default value constructed using the given function
     */
    private String extractValue(String[] elements, Column column, Function<String[], String> defaultValueGenerator) {
        return extractValue(elements, column, Function.identity(), defaultValueGenerator);
    }

    /**
     * Extracts a string located at the given column in the given list. The provided list must represent the entries of
     * the star catalogue's associated .csv file.
     *
     * @param elements     list storing the entries of a line of the .csv file containing the stars
     * @param column       enum value representing the column in which the entry of interest is located
     * @param defaultValue value to use in case the entry is empty
     *
     * @return data stored at the given column or the given default value
     */
    private String extractValue(String[] elements, Column column, String defaultValue) {
        return extractValue(elements, column, Function.identity(), defaultValue);
    }

    private enum Column {
        ID, HIP, HD, HR, GL, BF, PROPER, RA, DEC, DIST, PMRA, PMDEC,
        RV, MAG, ABSMAG, SPECT, CI, X, Y, Z, VX, VY, VZ,
        RARAD, DECRAD, PMRARAD, PMDECRAD, BAYER, FLAM, CON,
        COMP, COMP_PRIMARY, BASE, LUM, VAR, VAR_MIN, VAR_MAX
    }
}
