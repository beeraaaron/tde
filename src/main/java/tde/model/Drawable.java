package tde.model;

import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;

public interface Drawable {
    void draw(Pane p, Transform t);
    boolean contains(double x, double y);
}
