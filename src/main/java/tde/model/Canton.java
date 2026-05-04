package tde.model;

import javafx.scene.paint.Color;

public class Canton extends AbstractTerritory {
    private static final Color DEFAULT_BORDER = Color.DARKGREEN;
    private static final Color DEFAULT_FILL   = Color.LIGHTGREEN;

    public Canton(String name, int population, double area, LandArea landArea) {
        super(name, population, area, landArea);
        setBorderColor(DEFAULT_BORDER);
        setFill(DEFAULT_FILL);
    }
}
