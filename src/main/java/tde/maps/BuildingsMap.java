package tde.maps;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import tde.model.Address;

import java.util.List;

public class BuildingsMap extends AbstractMap<Address> {
    private final List<Address> buildings;
    private final Pane pane;

    public BuildingsMap(String name, List<Address> buildings, Pane pane) {
        setName(name);
        this.buildings = buildings;
        this.pane = pane;
        setVisible(true);
    }

    @Override
    protected List<Address> getObjects() {
        return buildings;
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle();
    }

    @Override
    public Node getTool() {
        return null;
    }

    @Override
    public Pane getPane() {
        return pane;
    }
}
