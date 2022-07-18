package ch.epfl.rigel.astronomy;

/**
 * Assigns a unique identifier to each celestial object. Stars all have the same identifier, because they are
 * constructed only once, meaning that they can be identified by object reference.
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public enum CelestialObjectIdentifier {  // BONUS MODIFICATION: helps keeping track of the selected object
    STAR(CelestialObjectType.STAR),
    MERCURY(CelestialObjectType.PLANET),
    VENUS(CelestialObjectType.PLANET),
    EARTH(CelestialObjectType.PLANET),
    MARS(CelestialObjectType.PLANET),
    JUPITER(CelestialObjectType.PLANET),
    SATURN(CelestialObjectType.PLANET),
    URANUS(CelestialObjectType.PLANET),
    NEPTUNE(CelestialObjectType.PLANET),
    SUN(CelestialObjectType.SUN),
    MOON(CelestialObjectType.MOON);

    private final CelestialObjectType type;

    CelestialObjectIdentifier(CelestialObjectType type) {
        this.type = type;
    }

    /**
     * Gives the type associated to the celestial object.
     *
     * @return the type of the celestial object.
     */
    public CelestialObjectType type() {
        return type;
    }
}
