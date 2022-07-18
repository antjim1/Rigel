package ch.epfl.rigel.internationalization;

/**
 * Text in the spanish language.
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class Spanish extends LanguageText {  // BONUS MODIFICATION: allows translation in spanish
    /**
     * Constructs a new instance of {@code Spanish} with the given text.
     *
     * @param text the spanish string to store
     */
    public Spanish(String text) {
        super(text);
    }

    /**
     * Gives the language represented by this class.
     *
     * @return the language represented by this class.
     */
    @Override
    Language language() {
        return Language.SPANISH;
    }
}
