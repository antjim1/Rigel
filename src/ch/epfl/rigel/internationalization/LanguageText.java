package ch.epfl.rigel.internationalization;

/**
 * Text in a given language.
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public abstract class LanguageText {
    private final String text;

    /**
     * Creates an instance of {@code LanguageText} with the given text.
     *
     * @param text the text translated in the language
     */
    public LanguageText(String text) {
        this.text = text;
    }

    /**
     * Gives the text.
     *
     * @return the text.
     */
    public String text() {
        return text;
    }

    /**
     * Gives the language represented by this class.
     *
     * @return the language represented by this class.
     */
    abstract Language language();
}
