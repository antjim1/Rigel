package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

/**
 * Interface representing mathematical models that can approximately calculate different characteristics (e.g. the
 * position) of a celestial object.
 *
 * @param <O> type of the celestial object to model
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public interface CelestialObjectModel<O extends CelestialObject> {
    /**
     * Returns a celestial object of the given type at the given time
     *
     * @param daysSinceJ2010                 time at which the object is observed, in days since the J2010 epoch
     * @param eclipticToEquatorialConversion conversion to use when going from ecliptic to equatorial coordinates
     *
     * @return celestial object
     */
    O at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion);
}
