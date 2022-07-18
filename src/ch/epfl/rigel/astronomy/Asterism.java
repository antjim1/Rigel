package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;

import java.util.List;

/**
 * Represents asterisms - i.e. groups of particularly bright stars
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class Asterism {
    private final List<Star> stars;

    /**
     * Constructs a new {@code Asterism}.
     *
     * @param stars List of stars composing the asterism - not empty
     *
     * @throws IllegalArgumentException if the list of stars is empty
     * @implNote Only a copy of the provided list is stored, it can therefore still be used without any risk of
     * modification.
     */
    public Asterism(List<Star> stars) {
        Preconditions.checkArgument(!stars.isEmpty());
        this.stars = List.copyOf(stars);
    }

    /**
     * Returns an unmodifiable list of stars composing the asterism
     *
     * @return list of stars composing the asterism
     */
    public List<Star> stars() {
        return stars;
    }
}
