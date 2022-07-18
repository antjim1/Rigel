package ch.epfl.rigel.gui;

import ch.epfl.rigel.internationalization.Translation;
import ch.epfl.rigel.internationalization.Translations;
import javafx.beans.value.ObservableStringValue;

import java.time.Duration;

/**
 * Enumeration containing possible time accelerations in a name-accelerator pair
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public enum NamedTimeAccelerator {

    TIMES_1("1x", TimeAccelerator.continuous(1)),
    TIMES_30("30x", TimeAccelerator.continuous(30)),
    TIMES_300("300x", TimeAccelerator.continuous(300)),
    TIMES_3000("3000x", TimeAccelerator.continuous(3000)),
    DAY(Translations.DAY, TimeAccelerator.discrete(Duration.ofHours(24), 60)),
    SIDEREAL_DAY(Translations.SIDEREAL_DAY,
            TimeAccelerator.discrete(Duration.ofHours(23).plusMinutes(56).plusSeconds(4), 60));

    private final Translation name;
    private final TimeAccelerator accelerator;

    /**
     * Constructs a new {@code NamedTimeAccelerator} with the given name and underlying accelerator
     *
     * @param name        translated name of the accelerator
     * @param accelerator accelerator to use
     */
    // BONUS MODIFICATION: translated name
    NamedTimeAccelerator(Translation name, TimeAccelerator accelerator) {
        this.name = name;
        this.accelerator = accelerator;
    }

    /**
     * Constructs a new {@code NamedTimeAccelerator} with the given name and underlying accelerator
     *
     * @param name        name of the accelerator
     * @param accelerator accelerator to use
     */
    NamedTimeAccelerator(String name, TimeAccelerator accelerator) {
        this(Translation.constant(name), accelerator);
    }

    /**
     * Gives the name of the accelerator
     *
     * @return the name of the accelerator
     */
    public String getName() {
        return name.get();
    }

    /**
     * Gives a property containing the name attribute
     *
     * @return property containing the name attribute
     */
    public ObservableStringValue nameProperty() {
        return name;
    }

    /**
     * Gives the accelerator of the accelerator
     *
     * @return the accelerator of the accelerator
     */
    public TimeAccelerator getAccelerator() {
        return accelerator;
    }

    /**
     * Gives the name of the accelerator
     *
     * @return the name of the accelerator
     */
    @Override
    public String toString() {
        return name.get();
    }
}
