package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Class containing the position of the observer
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class ObserverLocationBean {
    private final DoubleProperty lonDeg;
    private final DoubleProperty latDeg;
    private final ObjectBinding<GeographicCoordinates> coordinates;

    /**
     * Constructs a new instance of {@code ObserverLocationBean} giving as initial value {@code null} for the properties
     * {@code lonDeg} and {@code latDeg}
     *
     * @see #lonDegProperty()
     * @see #latDegProperty()
     */
    public ObserverLocationBean() {
        lonDeg = new SimpleDoubleProperty(0);
        latDeg = new SimpleDoubleProperty(0);
        coordinates = Bindings.createObjectBinding(
                () -> GeographicCoordinates.ofDeg(lonDeg.get(), latDeg.get()), lonDeg, latDeg);

    }

    //-----------------------------------------------------lonDeg-------------------------------------------------------

    /**
     * Gives an observable property containing the longitude of the observer's position in degree
     *
     * @return property containing the longitude of the observer's position in degree
     */
    public DoubleProperty lonDegProperty() {
        return lonDeg;
    }

    /**
     * Gives the longitude of the observer's position in degree
     *
     * @return the longitude of the observer's position in degree
     */
    public double getLonDeg() {
        return lonDeg.get();
    }

    /**
     * Setter for the the observer longitude. If the new longitude is different than the current one, listeners of the
     * observer longitude are notified of the change.
     *
     * @param lonDeg new observer longitude in degree
     */
    public void setLonDeg(double lonDeg) {
        this.lonDeg.set(lonDeg);
    }

    //-----------------------------------------------------latDeg-------------------------------------------------------

    /**
     * Gives an observable property containing the latitude of the observer's position in degree
     *
     * @return the property containing the observer latitude in degree
     */
    public DoubleProperty latDegProperty() {
        return latDeg;
    }

    /**
     * Gives the observer latitude in degree
     *
     * @return the observer latitude in degree
     */
    public double getLatDeg() {
        return latDeg.get();
    }

    /**
     * Setter for the observer latitude. If the new latitude is different than the current one, listeners of observer
     * latitude are notified of the change.
     *
     * @param latDeg a new observer latitude in degree
     */
    public void setLatDeg(double latDeg) {
        this.latDeg.set(latDeg);
    }

    //---------------------------------------------------coordinates----------------------------------------------------

    /**
     * Gives a binding containing the the observer geographic coordinates
     *
     * @return the binding containing the observer geographic coordinates
     */
    public ObjectBinding<GeographicCoordinates> coordinatesProperty() {
        return coordinates;
    }

    /**
     * Gives the observer geographic coordinates
     *
     * @return the observer geographic coordinates
     */
    public GeographicCoordinates getCoordinates() {
        return coordinates.get();
    }

    /**
     * Setter for the observer geographic coordinates. If the new geographic coordinates are different from the current
     * ones, listeners of the observer geographic coordinates are notified of the change
     *
     * @param coordinates a new observer geographic coordinates
     */
    public void setCoordinates(GeographicCoordinates coordinates) {
        latDeg.set(coordinates.latDeg());
        lonDeg.set(coordinates.lonDeg());
    }
}
