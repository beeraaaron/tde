package tde.maps;

import javafx.scene.transform.Transform;
import tde.model.Drawable;

import java.util.List;

public abstract class AbstractMap<T extends Drawable> implements Map<T> {
    private static final String TRANSPARENCY_EXC = "Transparency must be within range [0.0, 1.0]";
    private String name = "Unknown";
    private double transparency;
    private boolean visible;

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setTransparency(double transparency) {
        if (transparency < 0 || transparency > 1) {
            throw new IllegalArgumentException(TRANSPARENCY_EXC);
        }
        this.transparency = transparency;
    }

    @Override
    public double getTransparency() {
        return transparency;
    }

    @Override
    public T getObjectAtPosition(double x, double y) {
        return getObjects()
                .stream()
                .filter(t -> t.contains(x, y))
                .findFirst().orElse(null);
    }

    @Override
    public void draw(Transform trans) {
        getPane().getChildren().clear();
        if (visible) {
            getObjects().forEach(t -> t.draw(getPane(), trans));
        }
    }

    @Override
    public void onMouseClicked(MapsController.TPEMouseEvent e) {
        // default behavior: do nothing
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    protected abstract List<T> getObjects();
}
