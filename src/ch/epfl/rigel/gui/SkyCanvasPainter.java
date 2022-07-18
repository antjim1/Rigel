package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.Moon;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.Planet;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.Sun;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.internationalization.Translations;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Transform;

import java.util.List;

import static java.lang.Math.tan;

/**
 * Draws the sky on a canvas
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class SkyCanvasPainter {
    private static final double ASTERISM_WIDTH = 1.0;
    private static final Color PLANET_COLOR = Color.LIGHTGRAY;

    private static final Color INNER_SUN_COLOR = Color.WHITE;
    private static final Color OUTER_SUN_COLOR = Color.YELLOW;
    private static final Color OBJECT_UNDER_MOUSE_HIGHLIGHT_COLOR = colorWithAlpha(Color.WHITE, 0.15);
    private static final Color MOON_COLOR = Color.WHITE;
    private static final double HORIZON_WIDTH = 2.0;
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color SUN_HALO_COLOR = colorWithAlpha(Color.YELLOW, 0.25);

    private final ObjectProperty<Color> asterismColor = new SimpleObjectProperty<>(Color.BLUE);
    private final ObjectProperty<Color> horizonColor = new SimpleObjectProperty<>(Color.RED);

    private final Canvas canvas;
    private final GraphicsContext ctx;

    /**
     * Constructs a new {@code SkyCanvasPainter} with the given observed sky, projection and transformation
     *
     * @param canvas canvas to draw on
     */
    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        ctx = canvas.getGraphicsContext2D();
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Gives the property holding the color of the horizon
     *
     * @return the property holding the color of the horizon
     */
    // BONUS MODIFICATION: the color of the horizon can be changed by the user
    public ObjectProperty<Color> horizonColorProperty() {
        return horizonColor;
    }


    /**
     * Gives the property holding the color of the asterisms
     *
     * @return the property holding the color of the asterisms
     */
    // BONUS MODIFICATION: the color of the asterisms can be changed by the user
    public ObjectProperty<Color> asterismColorProperty() {
        return asterismColor;
    }

    /**
     * Erases the canvas
     */
    public void clear() {
        ctx.setFill(BACKGROUND_COLOR);
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    //-----------------------------------------------Stars-----------------------------------------------

    /**
     * Draws the asterisms and stars on the canvas
     *
     * @param sky           the sky for drawing
     * @param planeToCanvas linear transform to convert to the canvas coordinate system
     * @param drawStars boolean specifying if the stars must be drawn
     * @param drawAsterisms boolean specifying if the asterisms must be drawn
     */
    // BONUS MODIFICATION: booleans to control the visibility of stars and asterisms
    public void drawStarsAndAsterisms(ObservedSky sky, Transform planeToCanvas,
                                      boolean drawStars, boolean drawAsterisms) {
        if (!drawStars && !drawAsterisms) return;

        List<Star> stars = sky.stars();
        double[] positions = transform(sky.starPositions(), planeToCanvas);
        Bounds bounds = canvas.getBoundsInLocal();

        if (drawAsterisms) {
            for (Asterism asterism : sky.asterisms()) {
                drawAsterism(sky.starIndexes(asterism), positions, bounds);
            }
        }

        if (drawStars) {
            for (int i = 0; i < stars.size(); ++i) {
                Star star = stars.get(i);
                double x = x(positions, i);
                double y = y(positions, i);
                drawStar(star, x, y, planeToCanvas);
            }
        }
    }

    private void drawStar(Star star, double x, double y, Transform planeToCanvas) {
        Color color = BlackBodyColor.colorForTemperature(star.colorTemperature());
        double d = diameter(star, planeToCanvas);
        fillCircle(x, y, d, color);
    }

    private static Color colorWithAlpha(Color color, double alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    private double diameter(Star star, Transform planeToCanvas) {
        return planetsAndStarsDiameter(star, planeToCanvas);
    }

    private double planetsAndStarsDiameter(CelestialObject object, Transform planeToCanvas) {
        double f = sizeFactor(object);
        double d = f * 2 * tan(Angle.ofDeg(0.5) / 4);
        return deltaTransform(d, planeToCanvas);
    }
    //-----------------------------------------------Planet-----------------------------------------------

    /**
     * Draws the planets
     *
     * @param sky           the sky for drawing
     * @param planeToCanvas linear transform to convert to the canvas coordinate system
     */
    public void drawPlanets(ObservedSky sky, Transform planeToCanvas) {
        List<Planet> planets = sky.planets();
        double[] planetPositions = transform(sky.planetPositions(), planeToCanvas);

        for (int i = 0; i < planets.size(); ++i) {
            Planet planet = planets.get(i);
            double x = x(planetPositions, i);
            double y = y(planetPositions, i);
            drawPlanet(planet, x, y, planeToCanvas);
        }
    }

    private void drawPlanet(Planet planet, double x, double y, Transform planeToCanvas) {
        double d = diameter(planet, planeToCanvas);
        fillCircle(x, y, d, PLANET_COLOR);
    }

    private double diameter(Planet planet, Transform planeToCanvas) {
        return planetsAndStarsDiameter(planet, planeToCanvas);
    }
    //-----------------------------------------------Sun and moon-----------------------------------------------

    private void drawAsterism(List<Integer> starIndexes, double[] starPositions, Bounds bounds) {
        ctx.setStroke(asterismColor.get());
        Point2D previousPosition = null;
        ctx.beginPath();
        for (int index : starIndexes) {
            double x = x(starPositions, index);
            double y = y(starPositions, index);
            boolean previousPositionIsContained = previousPosition == null || bounds.contains(previousPosition);
            if (previousPositionIsContained || bounds.contains(x, y)) lineTo(x, y, ASTERISM_WIDTH);
            else ctx.moveTo(x, y);
            previousPosition = new Point2D(x, y);
        }
        ctx.stroke();
    }

    private double diameter(Sun sun, StereographicProjection projection, Transform planeToCanvas) {
        return sunAndMoonDiameter(sun, projection, planeToCanvas);
    }

    /**
     * Draws the moon
     *
     * @param sky           the sky for drawing
     * @param projection    projection to use in order to convert {@code HorizontalCoordinates} to {@code
     *                      CartesianCoordinates}
     * @param planeToCanvas linear transform to convert to the canvas coordinate system
     */
    public void drawMoon(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        Moon moon = sky.moon();
        Point2D moonPosition = transform(sky.moonPosition(), planeToCanvas);
        double diameter = diameter(moon, projection, planeToCanvas);
        fillCircle(moonPosition, diameter, MOON_COLOR);
    }

    private double diameter(Moon moon, StereographicProjection projection, Transform planeToCanvas) {
        return sunAndMoonDiameter(moon, projection, planeToCanvas);
    }

    private double sunAndMoonDiameter(CelestialObject object, StereographicProjection projection,
                                      Transform planeToCanvas) {
        return deltaTransform(projection.applyToAngle(object.angularSize()), planeToCanvas);
    }

    /**
     * Draws the Sun
     *
     * @param sky           the sky for drawing
     * @param projection    projection to use in order to convert {@code HorizontalCoordinate} to {@code
     *                      CartesianCoordinate}
     * @param planeToCanvas linear transform to convert to the canvas coordinate system
     */
    public void drawSun(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        Sun sun = sky.sun();
        Point2D position = transform(sky.sunPosition(), planeToCanvas);

        double r0 = diameter(sun, projection, planeToCanvas);
        double r1 = r0 + 2;
        double r2 = r0 * 2.2;

        fillCircle(position, r2, SUN_HALO_COLOR);
        fillCircle(position, r1, OUTER_SUN_COLOR);
        fillCircle(position, r0, INNER_SUN_COLOR);
    }

    //-----------------------------------------------Horizon-----------------------------------------------

    /**
     * Draws the horizon and the cardinal and intercardinal points
     *
     * @param projection    projection to use in order to convert {@code HorizontalCoordinates} to {@code
     *                      CartesianCoordinates}
     * @param planeToCanvas linear transform to convert to the canvas coordinate system
     */
    public void drawHorizon(StereographicProjection projection, Transform planeToCanvas) {
        HorizontalCoordinates parallel = HorizontalCoordinates.of(0, 0);
        CartesianCoordinates initialCenterCartesian = projection.circleCenterForParallel(parallel);
        Point2D center = transform(initialCenterCartesian, planeToCanvas);

        double radius = parallelRadius(parallel, projection, planeToCanvas);

        strokeCircle(center, 2 * radius, horizonColor.get(), HORIZON_WIDTH);
        drawCardinalPoints(projection, planeToCanvas);
    }

    /**
     * Draws a circle over the object closest to the mouse pointer.
     *
     * @param position      position of the object in the projection plane
     * @param planeToCanvas linear transformation from the coordinate system of the projection plane to the one of the
     *                      canvas.
     * @param radius        radius of the circle to draw
     */
    public void highlightObjectUnderMouse(CartesianCoordinates position, Transform planeToCanvas, double radius) {
        if (position == null) return;
        double diameter = 2 * radius;
        Point2D transformedPosition = planeToCanvas.transform(position.x(), position.y());
        fillCircle(transformedPosition, diameter, OBJECT_UNDER_MOUSE_HIGHLIGHT_COLOR);
    }
    //-----------------------------------------------Auxiliary-----------------------------------------------


    private double parallelRadius(HorizontalCoordinates parallel, StereographicProjection projection, Transform T) {
        double radius = projection.circleRadiusForParallel(parallel);
        return deltaTransform(radius, T);
    }

    private static Point2D transform(CartesianCoordinates coordinates, Transform transform) {
        return transform.transform(coordinates.x(), coordinates.y());
    }

    private static double[] transform(double[] points, Transform transform) {
        double[] transformedPoints = new double[points.length];
        transform.transform2DPoints(points, 0, transformedPoints, 0, points.length / 2);
        return transformedPoints;
    }

    // BONUS MODIFICATION: cardinal points are translated
    private void drawCardinalPoints(StereographicProjection projection, Transform T) {
        ctx.setTextBaseline(VPos.TOP);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setFill(horizonColor.get());
        final int cardinalPointsCount = 8;
        final double radPerCardinalPoint = Angle.TAU / cardinalPointsCount;
        final double alt = Angle.ofDeg(-0.5);
        for (int i = 0; i < cardinalPointsCount; ++i) {
            HorizontalCoordinates coordinates = HorizontalCoordinates.of(radPerCardinalPoint * i, alt);
            String cardinalPointName = coordinates.azOctantName(Translations.NORTH.get(),
                                                                Translations.EAST.get(),
                                                                Translations.SOUTH.get(),
                                                                Translations.WEST.get());
            Point2D position = transform(projection.apply(coordinates), T);
            ctx.fillText(cardinalPointName, position.getX(), position.getY());
        }
    }

    private void fillCircle(Point2D center, double d, Color color) {
        fillCircle(center.getX(), center.getY(), d, color);
    }

    private void fillCircle(double x, double y, double d, Color color) {
        ctx.setFill(color);
        double r = d / 2.0;
        double x1 = x - r;
        double y1 = y - r;
        ctx.fillOval(x1, y1, d, d);
    }

    private void strokeCircle(Point2D center, double d, Color color, double width) {
        ctx.setStroke(color);
        ctx.setLineWidth(width);
        double r = d / 2.0;
        double x = center.getX() - r;
        double y = center.getY() - r;
        ctx.strokeOval(x, y, d, d);
    }

    private void lineTo(double x, double y, double width) {
        ctx.setLineWidth(width);
        ctx.lineTo(x, y);
    }

    private double x(double[] coordinates, int i) {
        return coordinates[2 * i];
    }

    private double y(double[] coordinates, int i) {
        return coordinates[2 * i + 1];
    }


    private double deltaTransform(double d, Transform planeToCanvas) {
        return planeToCanvas.deltaTransform(d, 0).distance(0, 0);
    }

    private double sizeFactor(CelestialObject object) {
        double mPrime = sizeOnMagnitude(object);
        return (99 - 17 * mPrime) / 140;
    }

    private double sizeOnMagnitude(CelestialObject object) {
        double magnitude = object.magnitude();
        ClosedInterval magnitudeInterval = ClosedInterval.of(-2, 5);
        return magnitudeInterval.clip(magnitude);
    }
}
