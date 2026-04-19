package tde.maps;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import tde.model.Territory;

import java.util.List;

public class TerritoryMap<T extends Territory> extends AbstractMap<T> {
    private final List<T> territories;
    private final Pane pane;

    public TerritoryMap(String name, List<T> territories, Pane pane) {
        setName(name);
        this.territories = territories;
        this.pane = pane;
        setVisible(true);
    }

    @Override
    protected List<T> getObjects() {
        return territories;
    }

    @Override
    public Pane getPane() {
        return pane;
    }

    @Override
    public Rectangle getBoundingBox() {
        if (territories == null || territories.isEmpty()) {
            return new Rectangle();
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (T territory : territories) {
            var b = territory.getBoundingBox();
            minX = Math.min(minX, b.getX());
            minY = Math.min(minY, b.getY());
            maxX = Math.max(maxX, b.getX() + b.getWidth());
            maxY = Math.max(maxY, b.getY() + b.getHeight());
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public Node getTool() {
        return null;
    }
}
