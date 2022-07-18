package ch.epfl.rigel.gui;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Interface representing time accelerators, i.e. objects mapping real time to simulated time and vice-versa
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
@FunctionalInterface
public interface TimeAccelerator {

    /**
     * Constructs new {@code ZonedDateTime} of the simulated time
     *
     * @param initialTime     Initial simulated time at the beginning of the animation
     * @param elapsedTimeNano The real time elapsed since the beginning of the animation
     *
     * @return the time that determines the moment of observation for which the sky is drawn
     */
    ZonedDateTime adjust(ZonedDateTime initialTime, long elapsedTimeNano);

    /**
     * Constructs new {@code TimeAccelerator} of the discrete accelerator
     *
     * @param steps     the discrete step of simulated time
     * @param frequency rate of progress of the simulated time
     *
     * @return discrete accelerator for the steps and frequency given
     */
    static TimeAccelerator discrete(Duration steps, int frequency) {
        return (T0, elapsedTimeNano) -> {
            double elapsedTime = elapsedTimeNano * 1e-9;
            Duration offset = steps.multipliedBy((long) (frequency * elapsedTime));
            return T0.plusNanos(offset.toNanos());
        };
    }

    /**
     * Constructs new {@code TimeAccelerator} of the continuous accelerator
     *
     * @param acceleration The accelerating time factor
     *
     * @return continuous accelerator for the acceleration given
     */
    static TimeAccelerator continuous(int acceleration) {
        return (T0, elapsedTimeNano) -> T0.plusNanos(acceleration * elapsedTimeNano);
    }
}
