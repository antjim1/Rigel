package ch.epfl.rigel.function;

/**
 * Consumer accepting two doubles as arguments
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
@FunctionalInterface
public interface DoubleBiConsumer {  // BONUS MODIFICATION: allows the use of lambdas with two arguments for a consumer
    void accept(double a, double b);
}

