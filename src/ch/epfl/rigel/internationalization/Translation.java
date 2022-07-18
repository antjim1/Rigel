package ch.epfl.rigel.internationalization;

import ch.epfl.rigel.Preconditions;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a translation.
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class Translation implements ObservableStringValue {
    private final Map<Language, String> translations;
    private final StringBinding currentTranslation;

    private Translation(LanguageText... translations) {
        Map<Language, String> tmpTranslations = new HashMap<>();
        Preconditions.checkArgument(
                translations.length == Language.values().length);  // check that all languages are present
        for (LanguageText translation : translations) {
            Language language = translation.language();
            Preconditions.checkArgument(!tmpTranslations.containsKey(language));  // avoid duplicating language
            tmpTranslations.put(language, translation.text());
        }
        this.translations = Collections.unmodifiableMap(tmpTranslations);
        this.currentTranslation = Bindings.createStringBinding(
                () -> this.translations.get(Translations.getCurrentLanguage()),
                Translations.currentLanguageProperty());
    }

    /**
     * Creates a new instance of {@code Translation}
     *
     * @param translations the text translated in all defined languages
     * @return a new instance of {@code Translation}
     */
    public static Translation of(LanguageText... translations) {
        return new Translation(translations);
    }

    /**
     * Creates a new instance of {@code Translation} containing the same text for all languages
     *
     * @param text the text to use for all languages
     * @return a new instance of {@code Translation} containing the same text for all languages
     */
    public static Translation constant(String text) {
        Language[] languages = Language.values();
        int count = languages.length;
        LanguageText[] translations = new LanguageText[count];
        for (int i = 0; i < count; ++i) {
            Language language = languages[i];
            translations[i] = new LanguageText(text) {
                @Override
                Language language() {
                    return language;
                }
            };
        }

        return new Translation(translations);
    }

    /**
     * Returns a new instance of {@code Translation} consisting of the concatenation of the current object with the
     * provided one.
     *
     * @param other the {@code Translation} to append after this object
     * @return a new instance of {@code Translation} consisting of the concatenation of the current object with the
     * provided one.
     */
    public Translation append(Translation other) {
        LanguageText[] newTranslations = new LanguageText[translations.size()];

        int i = 0;
        for (Map.Entry<Language, String> translation : translations.entrySet()) {
            Language language = translation.getKey();
            String text1 = translation.getValue();
            String text2 = other.translations.get(translation.getKey());
            newTranslations[i] = new LanguageText(text1 + text2) {
                @Override
                Language language() {
                    return language;
                }
            };
            ++i;
        }

        return new Translation(newTranslations);
    }

    /**
     * Returns a new instance of {@code Translation} consisting of the concatenation of the current object with the
     * provided text.
     *
     * @param text the string to append after this object
     * @return a new instance of {@code Translation} consisting of the concatenation of the current object with the
     * provided text.
     */
    public Translation append(String text) {
        return append(Translation.constant(text));
    }

    /**
     * Returns a new instance of {@code Translation} consisting of the concatenation of the current object with a
     * colon (":").
     *
     * @return a new instance of {@code Translation} consisting of the concatenation of the current object with a
     * colon (":").
     */
    public Translation withColon() {
        return append(Translations.COLON);
    }

    /**
     * Returns a new instance of {@code Translation} consisting of the concatenation of the current object with a
     * colon and a space (": ").
     *
     * @return a new instance of {@code Translation} consisting of the concatenation of the current object with a
     * colon and a space (": ").
     */
    public Translation withColonAndSpace() {
        return withColon().append(" ");
    }

    /**
     * Gives the string corresponding to the translation in the current language.
     *
     * @return the string corresponding to the translation in the current language.
     */
    @Override
    public String toString() {
        return get();
    }

    /**
     * Gives the string corresponding to the translation in the current language.
     *
     * @return the string corresponding to the translation in the current language.
     */
    @Override
    public String get() {
        return currentTranslation.get();
    }

    /**
     * Adds a listener to this translation.
     *
     * @param listener the listener to add
     */
    @Override
    public void addListener(ChangeListener<? super String> listener) {
        currentTranslation.addListener(listener);
    }

    /**
     * Removes a listener from this translation.
     *
     * @param listener the listener to remove
     */
    @Override
    public void removeListener(ChangeListener<? super String> listener) {
        currentTranslation.removeListener(listener);
    }

    /**
     * Gives the string corresponding to the translation in the current language.
     *
     * @return the string corresponding to the translation in the current language.
     */
    @Override
    public String getValue() {
        return currentTranslation.getValue();
    }


    /**
     * Adds a listener to this translation.
     *
     * @param listener the listener to add
     */
    @Override
    public void addListener(InvalidationListener listener) {
        currentTranslation.addListener(listener);
    }

    /**
     * Removes a listener from this translation.
     *
     * @param listener the listener to add
     */
    @Override
    public void removeListener(InvalidationListener listener) {
        currentTranslation.removeListener(listener);
    }
}