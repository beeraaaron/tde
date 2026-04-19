package tde.model;

import javafx.scene.shape.Rectangle;

public interface Territory extends Drawable {
    String getName();
    void setName(String name);

    int getPopulation();
    void setPopulation(int population);

    double getArea();
    void setArea(double landArea);

    LandArea getBoundaries();
    void setBoundaries(LandArea landArea);

    Rectangle getBoundingBox();
}
