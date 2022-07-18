package ch.epfl.rigel.internationalization;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */

public enum Language {
    FRENCH("Français"), ENGLISH("English"),
    GERMAN("Deutsch"), SPANISH("Español");

    private final String name;

    Language(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}


