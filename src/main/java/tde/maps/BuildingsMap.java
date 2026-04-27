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
/*        if (buildings == null || buildings.isEmpty()) {
            return new Rectangle();
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        for (var building : buildings) {
            Coordinates location = building.location();
            minX = Math.min(minX, location.east());
            minY = Math.min(minY, location.north() - BUILDING_HEIGHT);
            maxX = Math.max(maxX, location.east() + BUILDING_WIDTH);
            maxY = Math.max(maxY, location.north());
        }*/

        return new Rectangle(100, 100, 100, 100);
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
