package tde.maps;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;

/**
 * Represents a displayable map in the TDE app. A map contains objects like Territories (or even more
 * precisely Countries, Cantons, Districts or Municipalieties) or Addresses, etc.
 * @param <T> The type of objects that are managed with this map.
 */
public interface Map<T> {
    /**
     * Informs about the visibility state of this map.
     * <b>NOTE:</b> the visibility is independent of the transparency! Therefore a map return <em>false</em>
     * and yet might not be seen on screen due to a 0% transparency.
     * @return <pre>true</pre> if this map is displayed, <pre>false</pre> if it does <b>not</b> show.
     */
    boolean isVisible();

    /**
     * Set visibility of this map.
     * @param visible <em>true</em> if map shall be visible, <em>false</em> if map shall be hidden.
     */
    void setVisible(boolean visible);

    /**
     * The name of this map.
     * @return the name of this map.
     */
    String getName();

    /**
     * Set the name of this map.
     * @param name new name for this map.
     */
    void setName(String name);

    /**
     * Sets the transparency of this map. The value defines actually the alpha value of the background color
     * and is represented by a <em>double</em> in the range 0.0-1.0. A value of 1.0 means that the map
     * is completely opaque and a value of 0.0 means that the map is completely transparent.
     * @param transparency value in range 0.0 to 1.0 inclusive.
     */
    void setTransparency(double transparency);

    /**
     * Retrieve this maps transparency.
     * @return value in range of 0.0 to 1.0 inclusive.
     */
    double getTransparency();

    /**
     * Retrieve the object that contains the given coordinates. Returns the first such object if more than one
     * contain the coordinates. I.e. searches only for the first match and returns then immediately without looking
     * for any other matches.
     * @param x the easting part of LV95 coordinates
     * @param y the northing part of LV95 coordinates
     * @return the object at the given position or <pre>null</pre> if none was found.
     */
    T getObjectAtPosition(double x, double y);

    /**
     * Draws the maps content.
     * @param transform the transformation to apply for drawing.
     */
    void draw(Transform transform);

    /**
     * called when a mouse button was clicked.
     * @param e see {@link MapsController.TPEMouseEvent TPEMouseEvent}
     */
    void onMouseClicked(MapsController.TPEMouseEvent e);

    /**
     * Get the bounding box around all elements of this map.
     * @return bounding box.
     */
    Rectangle getBoundingBox();

    /**
     * A node that will be managed by the map and can be displayed in the app.
     * @return the node.
     */
    Node getTool();

    /**
     * Gets the pane this map is drawing on.
     * @return the map's pane.
     */
    Pane getPane();
}
