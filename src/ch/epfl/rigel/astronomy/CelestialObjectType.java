package ch.epfl.rigel.astronomy;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public enum CelestialObjectType {  // BONUS MODIFICATION: allows us to perform type-specific operations without
    STAR,                          //                     class comparisons or instance checking
    PLANET,
    SUN,
    MOON
}
