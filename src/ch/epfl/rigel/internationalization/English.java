package ch.epfl.rigel.internationalization;

/**
 * Text in the english language.
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class English extends LanguageText {  // BONUS MODIFICATION: allows translation in english
    /**
     * Constructs a new instance of {@code English} with the given text.
     *
     * @param text the english string to store
     */
    public English(String text) {
        super(text);
    }

    /**
     * Gives the language represented by this class.
     *
     * @return the language represented by this class.
     */
    @Override
    Language language() {
        return Language.ENGLISH;
    }
}
