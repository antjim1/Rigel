package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Class containing the field of view and the centre coordinates that determine the portion of the sky visible in the
 * image
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class ViewingParametersBean {
    private final DoubleProperty fieldOfViewDeg;
    private final ObjectProperty<HorizontalCoordinates> center;

    /**
     * Constructs a new instance of {@code ViewingParametersBean} with {@code null} as initial value for its properties
     */
    public ViewingParametersBean() {
        fieldOfViewDeg = new SimpleDoubleProperty(70);
        center = new SimpleObjectProperty<>(null);
    }

    //------------------------------------------------fieldOfViewDeg----------------------------------------------------

    /**
     * Gives an observable property containing the field of view in degree
     *
     * @return the property containing the field of view in degree
     */
    public DoubleProperty fieldOfViewDegProperty() {
        return fieldOfViewDeg;
    }

    /**
     * Gives the field of view in degree
     *
     * @return the field of view in degree
     */
    public Double getFieldOfViewDeg() {
        return fieldOfViewDeg.get();
    }

    /**
     * Setter for the field of view. If the new field of view is different from the current one, observers of the field
     * of view property are notified of the change
     *
     * @param newDeg a new degree for the field of view
     */
    public void setFieldOfViewDeg(double newDeg) {
        fieldOfViewDeg.setValue(newDeg);
    }

    //----------------------------------------------------center--------------------------------------------------------

    /**
     * Gives an observable property containing the horizontal coordinates of the projection centre
     *
     * @return the property containing the center
     */
    public ObjectProperty<HorizontalCoordinates> centerProperty() {
        return center;
    }

    /**
     * Gives the horizontal coordinates of the projection centre
     *
     * @return the horizontal coordinates of the projection centre
     */
    public HorizontalCoordinates getCenter() {
        return center.get();
    }

    /**
     * Setter for the centre coordinates. If the new coordinates are different from the current one, observers of the
     * coordinates of the projection centre are notified of the change
     */
    public void setCenter(HorizontalCoordinates newCoordinates) {
        center.set(newCoordinates);
    }
}
