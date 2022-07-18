package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.CelestialObjectIdentifier;
import ch.epfl.rigel.astronomy.CelestialObjectType;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.internationalization.Translations;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static java.lang.Math.tan;

/**
 * Manages the canvas on which the sky is drawn
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class SkyCanvasManager {
    /**
     * Interval specifying the values the {@code fovDeg} property can take. This property stores the value of the field
     * of view in degrees.
     */
    public static final ClosedInterval FOV_INTERVAL_DEG = ClosedInterval.of(30, 150);
    private static final RightOpenInterval AZIMUTH_INTERVAL = RightOpenInterval.of(0, Angle.ofDeg(360));
    private static final ClosedInterval ALTITUDE_INTERVAL = ClosedInterval.of(Angle.ofDeg(5), Angle.ofDeg(90));
    private final Canvas canvas;
    private final SkyCanvasPainter painter;

    //  Bindings and Properties
    private final ObjectBinding<StereographicProjection> projection;
    private final ObjectBinding<Transform> planeToCanvas;
    private final ObjectBinding<ObservedSky> observedSky;
    private final ObjectProperty<Point2D> mousePosition;

    private final ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition;
    private final ObjectBinding<CartesianCoordinates> mouseProjectedPosition;
    private static final double KEYBOARD_AZ_DELTA = Angle.ofDeg(10);
    private static final double KEYBOARD_ALT_DELTA = Angle.ofDeg(5);
    private final ViewingParametersBean viewingParameter;
    private final MouseNavigator mouseNavigator;

    private final ObjectBinding<CelestialObject> objectUnderMouse;
    private final Supplier<CartesianCoordinates> objectUnderMousePosition;
    private final ObjectProperty<CelestialObject> selectedObject;
    private static final double CLOSEST_TO_MAX_DISTANCE_PX = 10;  // max distance in pixels
    private final DoubleBinding closestToProjectedMaxDistance;

    private final ObjectProperty<NavigationState> navigationState = new SimpleObjectProperty<>(NavigationState.DEFAULT);
    private final ObjectBinding<Cursor> cursor;

    private final BooleanProperty drawStars = new SimpleBooleanProperty(true);
    private final BooleanProperty drawAsterisms = new SimpleBooleanProperty(true);
    private final BooleanProperty drawPlanets = new SimpleBooleanProperty(true);
    private final BooleanProperty drawSun = new SimpleBooleanProperty(true);
    private final BooleanProperty drawMoon = new SimpleBooleanProperty(true);
    private final BooleanProperty drawHorizon = new SimpleBooleanProperty(true);

    /**
     * @param catalogue        provides the stars and asterisms stored in it
     * @param dateTime         provides the local date, time and zone
     * @param observerLocation provides the coordinates of the observer
     * @param viewingParameter provides the centre coordinates and the field of view
     */
    public SkyCanvasManager(StarCatalogue catalogue, DateTimeBean dateTime, ObserverLocationBean observerLocation,
                            ViewingParametersBean viewingParameter) {
        this.canvas = new Canvas(800, 600);
        this.painter = new SkyCanvasPainter(canvas);
        this.viewingParameter = viewingParameter;

        //--------------------------------------------------projection--------------------------------------------------
        Callable<StereographicProjection> createProjection =
                () -> new StereographicProjection(viewingParameter.getCenter());
        projection = Bindings.createObjectBinding(createProjection, viewingParameter.centerProperty());

        //-------------------------------------------------planeToCanvas------------------------------------------------
        Callable<Transform> createTransform = () -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            double fov = Angle.ofDeg(viewingParameter.getFieldOfViewDeg());
            double scalingFactor = width == 0d ?  // width can be 0 at initialisation
                                   1d :
                                   width / (2d * tan(fov / 4d));

            return Transform.affine(scalingFactor, 0, 0, -scalingFactor, width / 2d, height / 2d);
        };
        planeToCanvas = Bindings.createObjectBinding(createTransform,
                                                     canvas.widthProperty(),
                                                     canvas.heightProperty(),
                                                     viewingParameter.fieldOfViewDegProperty());

        //--------------------------------------------------observedSky-------------------------------------------------
        Callable<ObservedSky> createObservedSky =
                () -> new ObservedSky(dateTime.getZonedDateTime(),
                                      observerLocation.getCoordinates(),
                                      projection.get(), catalogue);
        observedSky = Bindings.createObjectBinding(createObservedSky,
                                                   dateTime.zoneProperty(),
                                                   dateTime.dateProperty(),
                                                   dateTime.timeProperty(),
                                                   observerLocation.coordinatesProperty(), projection);

        //-----------------------------------------------mouse properties-----------------------------------------------
        // **** BONUS MODIFICATION: ****

        mousePosition = new SimpleObjectProperty<>(new Point2D(0, 0));

        mouseHorizontalPosition = Bindings.createObjectBinding(() -> {
            Point2D position = mousePosition.get();
            return pointerToHorizontal(position.getX(), position.getY(), projection, planeToCanvas);
        }, planeToCanvas, mousePosition, projection);

        mouseProjectedPosition = Bindings.createObjectBinding(
                () -> projection.get().apply(mouseHorizontalPosition.get()),
                projection, mouseHorizontalPosition);

        closestToProjectedMaxDistance = Bindings.createDoubleBinding(
                () -> planeToCanvas.get().inverseDeltaTransform(CLOSEST_TO_MAX_DISTANCE_PX, 0).getX(),
                planeToCanvas);

        ObjectBinding<Set<CelestialObjectType>> shownTypes = Bindings.createObjectBinding(() -> {
            Set<CelestialObjectType> types = new HashSet<>();
            if (drawMoon.get()) types.add(CelestialObjectType.MOON);
            if (drawSun.get()) types.add(CelestialObjectType.SUN);
            if (drawStars.get()) types.add(CelestialObjectType.STAR);
            if (drawPlanets.get()) types.add(CelestialObjectType.PLANET);
            return Collections.unmodifiableSet(types);
        }, drawMoon, drawSun, drawStars, drawPlanets);

        // BONUS MODIFICATION: computes the visible object closest to the mouse
        objectUnderMouse = Bindings.createObjectBinding(
                () -> objectClosestTo(observedSky.get(), mouseProjectedPosition.get(),
                            closestToProjectedMaxDistance.get(), shownTypes.get()),
                observedSky, mouseProjectedPosition, closestToProjectedMaxDistance, shownTypes);
        objectUnderMouse.addListener((p, o, n) -> {
            NavigationState currentNavigationState = navigationState.get();
            if (n == null) {
                if (currentNavigationState == NavigationState.HOVERING_OBJECT ||
                    currentNavigationState == NavigationState.SELECTING_OBJECT) {
                    navigationState.set(NavigationState.DEFAULT);
                }
            } else if (currentNavigationState == NavigationState.DEFAULT) {
                navigationState.set(NavigationState.HOVERING_OBJECT);
            }
        });
        objectUnderMousePosition = () -> {
            ObservedSky sky = observedSky.get();
            CelestialObject object = objectUnderMouse.get();
            return object == null ? null : sky.computePosition(object);
        };

        selectedObject = new SimpleObjectProperty<>(null);

        // BONUS MODIFICATION: finds the selected object back when the observed sky changes
        InvalidationListener keepTrackOfObject = (observable) -> {
            CelestialObject object = selectedObject.get();
            if (object == null) return;
            CelestialObjectIdentifier identifier = object.identifier();
            ObservedSky sky = observedSky.get();
            switch (identifier) {
                case SUN:
                    selectedObject.set(sky.sun());
                    break;
                case MOON:
                    selectedObject.set(sky.moon());
                    break;
                case STAR:
                    // do nothing since stars do not change
                    break;
                case EARTH:  // should not happen
                    throw new IllegalStateException("The earth is not selectable");
                default:  // planets other than earth
                    selectedObject.set(sky.planet(identifier));
                    break;
            }
        };
        observedSky.addListener(keepTrackOfObject);

        cursor = Bindings.createObjectBinding(() -> navigationState.get().cursor, navigationState);

        mouseNavigator = new MouseNavigator(mousePosition, canvas.widthProperty(), canvas.heightProperty(),
                                            viewingParameter.centerProperty(), this::setCenter);
        //------------------------------------------------canvas events-------------------------------------------------
        // mouse events
        initialiseMouse(viewingParameter);

        // keyboard events
        initialiseKeyboard(viewingParameter);

        //------------------------------------------------require redraw------------------------------------------------
        // **** BONUS MODIFICATION: draws the visible objects on the canvas ****
        InvalidationListener updateCanvas = (observable) -> {
            ObservedSky sky = observedSky.get();
            StereographicProjection projection = this.projection.get();
            Transform transform = planeToCanvas.get();
            painter.clear();
            painter.drawStarsAndAsterisms(sky, transform, drawStars.get(), drawAsterisms.get());
            if (drawPlanets.get()) painter.drawPlanets(sky, transform);
            if (drawSun.get()) painter.drawSun(sky, projection, transform);
            if (drawMoon.get()) painter.drawMoon(sky, projection, transform);
            if (drawHorizon.get()) painter.drawHorizon(projection, transform);
            if (objectUnderMouse.get() != null) {
                painter.highlightObjectUnderMouse(objectUnderMousePosition.get(),
                        transform, CLOSEST_TO_MAX_DISTANCE_PX);
            }
        };

        ChangeListener<Object> updateOnChange = (p, o, n) -> updateCanvas.invalidated(p);

        observedSky.addListener(updateCanvas);
        projection.addListener(updateCanvas);
        planeToCanvas.addListener(updateCanvas);
        drawAsterisms.addListener(updateCanvas);
        drawStars.addListener(updateCanvas);
        drawPlanets.addListener(updateCanvas);
        drawSun.addListener(updateCanvas);
        drawMoon.addListener(updateCanvas);
        drawHorizon.addListener(updateCanvas);
        objectUnderMouse.addListener(updateOnChange);
        painter.asterismColorProperty().addListener(updateCanvas);
        painter.horizonColorProperty().addListener(updateCanvas);
        Translations.currentLanguageProperty().addListener(updateCanvas);
    }

    /**
     * Gives the closet celestial object to the point given
     *
     * @param sky                    observable value containing the sky where the celestial objects are located
     * @param mouseProjectedPosition observable value containing the mouse position on the stereographic plane
     * @param projectedMaxDistance   maximum distance above which no celestial object is returned
     *
     * @return the closest celestial object
     */
    private static CelestialObject objectClosestTo(ObservedSky sky,
                                                   CartesianCoordinates mouseProjectedPosition,
                                                   double projectedMaxDistance,
                                                   Set<CelestialObjectType> visibleTypes) {
        return sky.objectClosestTo(mouseProjectedPosition, projectedMaxDistance, visibleTypes)
                  .orElse(null);
    }


    //-------------------------------------------- objectUnderMouse ----------------------------------------------------

    /**
     * Calculates the horizontal coordinates from the current pointer position
     *
     * @param x             pointer x-coordinate on the canvas
     * @param y             pointer y-coordinate on the canvas
     * @param projection    projection to use in order to convert {@code HorizontalCoordinate} to {@code
     *                      CartesianCoordinate}
     * @param planeToCanvas Observable object with linear transform to convert to the canvas coordinate system
     *
     * @return the horizontal coordinates from the current pointer position
     */
    private static HorizontalCoordinates pointerToHorizontal(double x, double y,
                                                             ObservableObjectValue<StereographicProjection> projection,
                                                             ObservableObjectValue<Transform> planeToCanvas) {
        StereographicProjection p = projection.get();
        Transform transform = planeToCanvas.get();
        Point2D p2D;
        try {
            p2D = transform.inverseTransform(x, y);
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException(e);
        }
        CartesianCoordinates cartesianCoordinates = CartesianCoordinates.of(p2D.getX(), p2D.getY());
        return p.inverseApply(cartesianCoordinates);
    }

    /**
     * Keeps the given coordinates in the established interval
     *
     * @param azimuth  current azimuth
     * @param altitude current altitude
     *
     * @return the coordinates in the interval
     */
    private static HorizontalCoordinates keepCenterInInterval(double azimuth, double altitude) {
        double az = AZIMUTH_INTERVAL.reduce(azimuth);
        double alt = ALTITUDE_INTERVAL.clip(altitude);
        return HorizontalCoordinates.of(az, alt);
    }

    /**
     * Obtains the azimuth and altitude of the given coordinates and keep the given coordinates in the established
     * interval
     *
     * @param center the given coordinates
     *
     * @return the coordinates in the interval
     */
    private static HorizontalCoordinates keepCenterInInterval(HorizontalCoordinates center) {
        return keepCenterInInterval(center.az(), center.alt());
    }

    /**
     * Gives the canvas
     *
     * @return the canvas
     */
    public Canvas canvas() {
        return canvas;
    }

    /**
     * Gives the mouse position in horizontal coordinates
     *
     * @return mouse position in horizontal coordinates
     */
    public ObjectBinding<HorizontalCoordinates> mouseHorizontalPositionProperty() {
        return mouseHorizontalPosition;
    }

    /**
     * Gives a binding containing the celestial object under the mouse
     *
     * @return binding containing the celestial object under the mouse
     */
    public ObjectBinding<CelestialObject> objectUnderMouseProperty() {
        return objectUnderMouse;
    }

    /**
     * Gives a property containing the selected celestial object
     *
     * @return binding containing the selected celestial object
     */
    public ObservableObjectValue<Cursor> cursorProperty() {
        return cursor;
    }

    /**
     * Allows to modify the field of view using the mouse wheel or trackpad
     *
     * @param viewingParameter gives us the current field of view
     */
    private void initialiseMouse(ViewingParametersBean viewingParameter) {
        canvas.setOnMouseMoved(event -> mousePosition.set(new Point2D(event.getX(), event.getY())));


        canvas.setOnMouseClicked(event -> {
            canvas.requestFocus();
            selectedObject.set(objectUnderMouse.get());
            HorizontalCoordinates center = keepCenterInInterval(mouseHorizontalPosition.get());
            viewingParameter.setCenter(center);
        });

        canvas.setOnMouseEntered(event -> {
            canvas.requestFocus();
        });

        canvas.setOnMousePressed(event -> {
            if (navigationState.get() == NavigationState.HOVERING_OBJECT) {
                navigationState.set(NavigationState.SELECTING_OBJECT);
            }
        });

        canvas.setOnMouseReleased(event -> {
            if (navigationState.get() == NavigationState.SELECTING_OBJECT) {
                navigationState.set(NavigationState.HOVERING_OBJECT);
            }
        });

        canvas.setOnScroll(event -> {
            double dx = event.getDeltaX();
            double dy = event.getDeltaY();
            double maxDelta = Math.abs(dx) > Math.abs(dy) ? dx : dy;
            double fovDeg = viewingParameter.getFieldOfViewDeg();
            double newFovDeg = FOV_INTERVAL_DEG.clip(fovDeg + maxDelta);
            viewingParameter.setFieldOfViewDeg(newFovDeg);
        });

    }

    /**
     * Allows to modify the center coordinates by interacting with the up, down, left, right keys
     *
     * @param viewingParameter gives us the current center coordinates
     */
    private void initialiseKeyboard(ViewingParametersBean viewingParameter) {
        canvas.setOnKeyPressed(event -> {
            HorizontalCoordinates center = viewingParameter.getCenter();
            double newAz = center.az();
            double newAlt = center.alt();

            switch (event.getCode()) {
                case RIGHT:
                    newAz += KEYBOARD_AZ_DELTA;
                    break;
                case LEFT:
                    newAz -= KEYBOARD_AZ_DELTA;
                    break;
                case UP:
                    newAlt += KEYBOARD_ALT_DELTA;
                    break;
                case DOWN:
                    newAlt -= KEYBOARD_ALT_DELTA;
                    break;
                default:
                    return;
            }
            setCenter(newAz, newAlt);
            event.consume();
        });
        // **** BONUS MODIFICATION: enables/disables mouse navigation ****
        canvas.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ALT:
                    navigationState.set(NavigationState.PANNING);
                    mouseNavigator.start();
                    break;
                case CONTROL:
                    if (navigationState.get() == NavigationState.PANNING) {
                        mouseNavigator.stop();
                        navigationState.set(NavigationState.DEFAULT);
                    }
                    break;
                default:
                    return;
            }
            event.consume();
        });
    }

    //------------------------------------------ BONUS MODIFICATION ----------------------------------------------------
    /**
     * Allows access to the current fov property
     *
     * @return the current fov property
     */
    public DoubleProperty fovDegProperty() {
        return viewingParameter.fieldOfViewDegProperty();
    }

    /**
     * Set the new display center using the given coordinates
     *
     * @param newAz the new azimut
     * @param newAlt the new altitude
     */
    private void setCenter(double newAz, double newAlt) {
        HorizontalCoordinates center = viewingParameter.getCenter();
        if (newAlt != center.alt() || newAz != center.az()) {
            HorizontalCoordinates newCenter = keepCenterInInterval(newAz, newAlt);
            viewingParameter.setCenter(newCenter);
        }
    }

    /**
     * Gives the property specifying whether the stars must be drawn.
     *
     * @return the property specifying whether the stars must be drawn.
     */
    public BooleanProperty drawStarsProperty() {
        return drawStars;
    }

    /**
     * Gives the property specifying whether the asterisms must be drawn.
     *
     * @return the property specifying whether the asterisms must be drawn.
     */
    public BooleanProperty drawAsterismsProperty() {
        return drawAsterisms;
    }

    /**
     * Gives the property specifying whether the planets must be drawn.
     *
     * @return the property specifying whether the planets must be drawn.
     */
    public BooleanProperty drawPlanetsProperty() {
        return drawPlanets;
    }

    /**
     * Gives the property specifying whether the sun must be drawn.
     *
     * @return the property specifying whether the sun must be drawn.
     */
    public BooleanProperty drawSunProperty() {
        return drawSun;
    }

    /**
     * Gives the property specifying whether the moon must be drawn.
     *
     * @return the property specifying whether the moon must be drawn.
     */
    public BooleanProperty drawMoonProperty() {
        return drawMoon;
    }

    /**
     * Gives the property specifying whether the horizon must be drawn.
     *
     * @return the property specifying whether the horizon must be drawn.
     */
    public BooleanProperty drawHorizonProperty() {
        return drawHorizon;
    }


    /**
     * Gives a property containing the selected celestial object
     *
     * @return binding containing the selected celestial object
     */
    public ReadOnlyObjectProperty<CelestialObject> selectedObjectProperty() {
        return selectedObject;
    }

    /**
     * Sets the selected object to 'null
     */
    public void resetSelectedObject() {
        selectedObject.setValue(null);
    }

    /**
     * Access the property that contains the color of the horizon
     *
     * @return the property containing the color of the horizon
     */
    public ObjectProperty<Color> horizonColorProperty() {
        return painter.horizonColorProperty();
    }

    /**
     * Access the property that contains the color of the asterisms
     *
     * @return the property containing the color of the asterisms
     */
    public ObjectProperty<Color> asterismColorProperty() {
        return painter.asterismColorProperty();
    }

    /**
     * Access the property that contains the maximum speed for mouse movement
     *
     * @return the property containing the maximum speed for mouse movement
     */
    public DoubleProperty maxSpeedProperty() {
        return mouseNavigator.maxSpeedProperty();
    }

    /**
     * Sets the shape of the pointer according to the current status (e.g. if the pointer is on a celestial
     * object the pointer is shaped like a hand)
     */
    private enum NavigationState {
        DEFAULT(Cursor.DEFAULT), PANNING(Cursor.MOVE), HOVERING_OBJECT(Cursor.HAND),
        SELECTING_OBJECT(Cursor.CLOSED_HAND);

        private final Cursor cursor;

        NavigationState(Cursor cursor) {
            this.cursor = cursor;
        }
    }
}
