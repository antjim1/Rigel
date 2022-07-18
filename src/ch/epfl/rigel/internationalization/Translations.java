package ch.epfl.rigel.internationalization;

import javafx.beans.property.*;

/**
 * Contains the translation of all strings displayed to the user.
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class Translations {
    private static final ObjectProperty<Language> CURRENT_LANGUAGE = new SimpleObjectProperty<>(Language.FRENCH);
    private static LanguageController languageController = null;

    /**
     * Sets the language controller. Note that this method can only be called once.
     *
     * @param controller the object controlling the current language.
     * @throws IllegalAccessException when a controller is already set.
     */
    public static void setController(LanguageController controller) throws IllegalAccessException {
        if (languageController != null) throw new IllegalAccessException("Only one language controller is allowed");
        languageController = controller;
        CURRENT_LANGUAGE.bind(controller.currentLanguageProperty());
    }

    /**
     * Gives the property holding the current language.
     *
     * @return the property holding the current language.
     */
    public static ReadOnlyObjectProperty<Language> currentLanguageProperty() {
        return CURRENT_LANGUAGE;
    }

    /**
     * Gives the current language.
     *
     * @return the current language.
     */
    public static Language getCurrentLanguage() {
        return CURRENT_LANGUAGE.get();
    }

    // NOTE : the following public attributes are not documented, since their name and value fully describe them

    public static final Translation COLON = Translation.of(
            new French(" :"),
            new English(" :"),
            new German(" :"),
            new Spanish(" :"));


    public static final Translation PLANETS = Translation.of(
            new French("Planètes"),
            new English("Planets"),
            new German("Planeten"),
            new Spanish("Planetas"));

    public static final Translation STARS = Translation.of(
            new French("Étoiles"),
            new English("Stars"),
            new German("Sterne"),
            new Spanish("Estrellas"));

    public static final Translation HORIZON = Translation.of(
            new French("Horizon"),
            new English("Horizon"),
            new German("Horizont"),
            new Spanish("Horizonte"));

    public static final Translation ASTERISMS = Translation.of(
            new French("Astérismes"),
            new English("Asterisms"),
            new German("Sterngruppen"),
            new Spanish("Asterismos"));


    public static final Translation MERCURY_NAME = Translation.of(
            new French("Mercure"),
            new English("Mercury"),
            new German("Merkur"),
            new Spanish("Mercurio"));

    public static final Translation VENUS_NAME = Translation.of(
            new French("Vénus"),
            new English("Venus"),
            new German("Venus"),
            new Spanish("Venus"));

    public static final Translation EARTH_NAME = Translation.of(
            new French("Terre"),
            new English("Earth"),
            new German("Erde"),
            new Spanish("Tierra"));

    public static final Translation MARS_NAME = Translation.of(
            new French("Mars"),
            new English("Mars"),
            new German("Mars"),
            new Spanish("Marte"));

    public static final Translation JUPITER_NAME = Translation.of(
            new French("Jupiter"),
            new English("Jupiter"),
            new German("Jupiter"),
            new Spanish("Júpiter"));

    public static final Translation SATURN_NAME = Translation.of(
            new French("Saturne"),
            new English("Saturn"),
            new German("Saturn"),
            new Spanish("Saturno"));

    public static final Translation URANUS_NAME = Translation.of(
            new French("Uranus"),
            new English("Uranus"),
            new German("Uranus"),
            new Spanish("Urano"));

    public static final Translation NEPTUNE_NAME = Translation.of(
            new French("Neptune"),
            new English("Neptune"),
            new German("Neptun"),
            new Spanish("Neptuno"));

    public static final Translation MOON_NAME = Translation.of(
            new French("Lune"),
            new English("Moon"),
            new German("Mond"),
            new Spanish("Luna"));

    public static final Translation SUN_NAME = Translation.of(
            new French("Soleil"),
            new English("Sun"),
            new German("Sonne"),
            new Spanish("Sol"));


    public static final Translation NORTH = Translation.of(
            new French("N"),
            new English("N"),
            new German("N"),
            new Spanish("N"));

    public static final Translation EAST = Translation.of(
            new French("E"),
            new English("E"),
            new German("O"),
            new Spanish("E"));

    public static final Translation SOUTH = Translation.of(
            new French("S"),
            new English("S"),
            new German("S"),
            new Spanish("S"));

    public static final Translation WEST = Translation.of(
            new French("O"),
            new English("W"),
            new German("W"),
            new Spanish("O"));


    public static final Translation LONGITUDE_LABEL_TEXT = Translation.of(
            new French("Longitude (°)"),
            new English("Longitude (°)"),
            new German("Länge (°)"),
            new Spanish("Longitud (°)")).withColonAndSpace();

    public static final Translation LATITUDE_LABEL_TEXT = Translation.of(
            new French("Latitude (°)"),
            new English("Latitude (°)"),
            new German("Geographische Breite (°)"),
            new Spanish("Latitud (°)")).withColonAndSpace();

    public static final Translation DATE_LABEL_TEXT = Translation.of(
            new French("Date"),
            new English("Date"),
            new German("Datum"),
            new Spanish("Fecha")).withColonAndSpace();

    public static final Translation TIME_LABEL_TEXT = Translation.of(
            new French("Heure"),
            new English("Time"),
            new German("Zeit"),
            new Spanish("Hora")).withColonAndSpace();


    public static final Translation FOV_TEXT = Translation.of(
            new French("Champ de vue"),
            new English("Field of view"),
            new German("Sichtfeld"),
            new Spanish("Campo de visión")).withColonAndSpace();


    public static final Translation AZIMUT = Translation.of(
            new French("azimut"),
            new English("azimut"),
            new German("azimut"),
            new Spanish("azimut")).withColon();

    public static final Translation ALTITUDE = Translation.of(
            new French("hauteur"),
            new English("altitude"),
            new German("Höhe"),
            new Spanish("Altitud")).withColon();


    public static final Translation DAY = Translation.of(
            new French("jour"),
            new English("day"),
            new German("Tag"),
            new Spanish("Día"));

    public static final Translation SIDEREAL_DAY = Translation.of(
            new French("jour sidéral"),
            new English("sidereal day"),
            new German("siderischer Tag"),
            new Spanish("Día sideral"));

    public static final Translation NAME = Translation.of(
            new French("Nom"),
            new English("Name"),
            new German("Name"),
            new Spanish("Nombre")).withColon();


    public static final Translation MAGNITUDE = Translation.of(
            new French("Magnitude"),
            new English("Magnitude"),
            new German("Größenordnung"),
            new Spanish("Magnitud"));

    public static final Translation ANGULAR_SIZE = Translation.of(
            new French("Taille angulaire"),
            new English("Angular size"),
            new German("Scheinbare Größe"),
            new Spanish("Diámetro angular"));

    public static final Translation PHASE = Translation.of(
            new French("Phase"),
            new English("Phase"),
            new German("Phase"),
            new Spanish("Fase"));

    public static final Translation ASTERISM_COLOR = Translation.of(
            new French("Couleur des astérismes"),
            new English("Asterisms color"),
            new German("Sterngruppen Farbe"),
            new Spanish("Color de los asterismos")).withColon();

    public static final Translation HORIZON_COLOR = Translation.of(
            new French("Couleur de l'horizon"),
            new English("Horizon color"),
            new German("Farbe des Horizonts"),
            new Spanish("Color del horizonte")).withColon();

    public static final Translation PAINTING = Translation.of(
            new French("DESSIN"),
            new English("PAINTING"),
            new German("MALEREI"),
            new Spanish("DIBUJO"));

    public static final Translation GENERAL = Translation.of(
            new French("GÉNÉRAL"),
            new English("GENERAL"),
            new German("GENERAL"),
            new Spanish("GENERAL"));

    public static final Translation NAVIGATION = Translation.of(
            new French("NAVIGATION"),
            new English("NAVIGATION"),
            new German("NAVIGATION"),
            new Spanish("NAVEGACIÓN"));

    public static final Translation MOUSE_SENSITIVITY = Translation.of(
            new French("Sensibilité de la souris"),
            new English("Mouse sensitivity"),
            new German("Empfindlichkeit der Maus"),
            new Spanish("Sensibilidad del ratón")).withColon();

    public static final Translation VOLUME = Translation.of(
            new French("VOLUME"),
            new English("VOLUME"),
            new German("BAND"),
            new Spanish("VOLUMEN"));

    public static final Translation BACKGROUND_MUSIC = Translation.of(
            new French("Musique de fond"),
            new English("Background music"),
            new German("Hintergrundmusik"),
            new Spanish("Música de ambiente"));

    public static final Translation FX_SOUNDS = Translation.of(
            new French("Sons FX"),
            new English("FX sounds"),
            new German("FX-Klänge"),
            new Spanish("Sonidos FX"));

    public static final Translation INFO = Translation.of(
            new French("INFORMATIONS"),
            new English("INFORMATION"),
            new German("INFORMATIONEN"),
            new Spanish("INFORMACIÓN"));

    public static final Translation SCREENSHOT = Translation.of(
            new French("Capture d'écran"),
            new English("Screenshot"),
            new German("Bildschirmfoto"),
            new Spanish("Captura de pantalla")
    );

    private Translations() {}
}
