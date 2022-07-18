package ch.epfl.rigel.internationalization;

/**
 * Text in the french language.
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class French extends LanguageText {  // BONUS MODIFICATION: allows translation in french
    /**
     * Constructs a new instance of {@code French} with the given text.
     *
     * @param text the french string to store
     */
    public French(String text) {
        super(text);
    }

    /**
     * Gives the language represented by this class.
     *
     * @return the language represented by this class.
     */
    @Override
    Language language() {
        return Language.FRENCH;
    }
}
