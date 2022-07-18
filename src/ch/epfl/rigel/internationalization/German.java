package ch.epfl.rigel.internationalization;

/**
 * Text in the german language.
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class German extends LanguageText {  // BONUS MODIFICATION: allows translation in german
    /**
     * Constructs a new instance of {@code German} with the given text.
     *
     * @param text the german string to store
     */
    public German(String text) {
        super(text);
    }

    /**
     * Gives the language represented by this class.
     *
     * @return the language represented by this class.
     */
    @Override
    Language language() {
        return Language.GERMAN;
    }
}
