package ch.epfl.rigel.gui;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.time.ZonedDateTime;

/**
 * Represent a time animator in order to modify periodically the sky
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class TimeAnimator extends AnimationTimer {
    private final DateTimeBean observationTime;
    private final SimpleBooleanProperty running;
    private TimeAccelerator accelerator;
    private ZonedDateTime initialObservationTime;
    private long initialTimeNano;
    private boolean wasJustStarted;

    /**
     * Establishes the observation time
     *
     * @param observationTime the new observation time
     */
    public TimeAnimator(DateTimeBean observationTime) {
        this.observationTime = observationTime;
        running = new SimpleBooleanProperty(false);
        accelerator = null;
        initialTimeNano = 0L;
        wasJustStarted = false;
    }

    /**
     * While {@code AnimationTimer} is active it will modify every frame the elapsed time
     *
     * @param nowNano The timestamp of the current frame given in nanoseconds
     */
    @Override
    public void handle(long nowNano) {
        if (wasJustStarted) {
            initialObservationTime = observationTime.getZonedDateTime();
            initialTimeNano = nowNano;
            wasJustStarted = false;
        }

        if (isRunning()) {
            long elapsedTimeNano = nowNano - initialTimeNano;
            ZonedDateTime newTime = accelerator.adjust(initialObservationTime, elapsedTimeNano);
            observationTime.setZonedDateTime(newTime);
        }
    }

    /**
     * Sets running to {@code true} and starts the animation
     */
    @Override
    public void start() {
        super.start();
        wasJustStarted = true;
        running.set(true);
    }

    /**
     * Sets running to {@code false} and ends the animation
     */
    @Override
    public void stop() {
        super.stop();
        running.set(false);
    }

    /**
     * Gives the current accelerator
     *
     * @return the current accelerator
     */
    public TimeAccelerator accelerator() {
        return accelerator;
    }

    /**
     * Allows to modify the accelerator
     *
     * @param accelerator the new accelerator
     */
    public void setAccelerator(TimeAccelerator accelerator) {
        this.accelerator = accelerator;
        if (isRunning()) {
            start();
        }
    }

    /**
     * Gives an observable read-only property containing the current state of the animation
     *
     * @return property containing the state of the animation
     */
    public ReadOnlyBooleanProperty runningProperty() {
        return running;
    }

    /**
     * Allows to modify the state of the animation
     *
     * @param running the new state of the animation
     */
    public void setRunning(boolean running) {
        if (running != isRunning()) {
            if (running) start();
            else stop();
        }
    }

    /**
     * Allows to see if the animation is running
     *
     * @return the current state, true if it is running, otherwise false
     */
    public boolean isRunning() {
        return running.get();
    }
}
