package ch.epfl.rigel.internationalization;

import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * Interface allowing a class to control the current language.
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public interface LanguageController {
    ReadOnlyObjectProperty<Language> currentLanguageProperty();
}
