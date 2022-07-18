package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.function.DoubleBiConsumer;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;

/**
 * Moves the sky according to the position of the mouse. This movement is done by updating the horizontal coordinates
 * of the projection center. The rate of change of those coordinates is maximal when the mouse reaches an edge of the
 * canvas and is equal to 0 when the mouse is centered relative to the canvas.
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
// BONUS MODIFICATION: allows mouse navigation
public final class MouseNavigator extends AnimationTimer {
    /**
     * Interval specifying the values the {@code maxSpeed} property can take
     * This property defines the rate of change in radians per second of the horizontal coordinates of the projection
     * center when the mouse is positioned at the edge of the canvas.
     */
    public static final ClosedInterval MAX_SPEED_INTERVAL = ClosedInterval.of(0.2, 10d);
    private static final double NANOSECOND = 1e-9;
    private final DoubleProperty maxSpeed = new SimpleDoubleProperty(3d);
    private final DoubleBinding maxAzSpeed;
    private final DoubleBinding maxAltSpeed;
    private final ReadOnlyObjectProperty<Point2D> mousePosition;
    private final ReadOnlyDoubleProperty canvasWidth;
    private final ReadOnlyDoubleProperty canvasHeight;
    private final ReadOnlyObjectProperty<HorizontalCoordinates> projectionCenter;
    private final DoubleBiConsumer setCenter;
    private long previousTimeNano;
    private boolean wasJustStarted;
    private boolean isActive;

    /**
     * Creates a new {@code MouseNavigator}.
     *
     * @param mousePosition    property to the position of the mouse on the canvas
     * @param canvasWidth      property to the width of the canvas
     * @param canvasHeight     property to the height of the canvas
     * @param projectionCenter property to the projection center
     * @param setCenter        method allowing the modification of the projection center
     */
    public MouseNavigator(ReadOnlyObjectProperty<Point2D> mousePosition,
                          ReadOnlyDoubleProperty canvasWidth,
                          ReadOnlyDoubleProperty canvasHeight,
                          ReadOnlyObjectProperty<HorizontalCoordinates> projectionCenter,
                          DoubleBiConsumer setCenter) {
        previousTimeNano = System.nanoTime();
        wasJustStarted = true;
        isActive = false;

        this.maxAzSpeed = Bindings.createDoubleBinding(maxSpeed::get, maxSpeed);
        this.maxAltSpeed = Bindings.createDoubleBinding(maxSpeed::get, maxSpeed);

        this.mousePosition = mousePosition;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.projectionCenter = projectionCenter;
        this.setCenter = setCenter;
    }

    private static double computeDelta(double maxSpeed, double elapsedTime,
                                       double mouseCoordinate, double canvasDimension) {
        double distanceToCenter = mouseCoordinate - canvasDimension / 2d;
        double speed = maxSpeed * (distanceToCenter / canvasDimension);
        return speed * elapsedTime;
    }

    /**
     * Updates the position of the sky according to the mouse position. This method is called automatically by JavaFX
     * while the {@code MouseNavigator} is active.
     *
     * @param nowNano the current time in nanoseconds
     */
    @Override
    public void handle(long nowNano) {
        if (wasJustStarted) {
            previousTimeNano = nowNano;
            wasJustStarted = false;
        }

        double elapsedTime = (nowNano - previousTimeNano) * NANOSECOND;
        Point2D xy = mousePosition.get();

        double dAz = computeDelta(maxAzSpeed.get(), elapsedTime, xy.getX(), canvasWidth.get());
        double dAlt = -computeDelta(maxAltSpeed.get(), elapsedTime, xy.getY(), canvasHeight.get());
        HorizontalCoordinates center = projectionCenter.get();
        double newAz = center.az() + dAz;
        double newAlt = center.alt() + dAlt;
        setCenter.accept(newAz, newAlt);

        previousTimeNano = nowNano;
    }

    /**
     * Starts the mouse navigation.
     */
    @Override
    public void start() {
        if (!isActive) {
            super.start();
            isActive = true;
            wasJustStarted = true;
        }
    }

    /**
     * Stops the mouse navigation.
     */
    @Override
    public void stop() {
        if (isActive) {
            super.stop();
            isActive = false;
        }
    }

    /**
     * Gives the {@code maxSpeed} property. This property defines the rate of change in radians per second of the
     * horizontal coordinates of the projection center when the mouse is positioned at the edge of the canvas.
     *
     * @return the {@code maxSpeed} property
     */
    public DoubleProperty maxSpeedProperty() {
        return maxSpeed;
    }
}
