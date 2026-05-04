package tde.model;

import javafx.scene.paint.Color;

public class District extends AbstractTerritory {
    private static final Color DEFAULT_BORDER = Color.DARKORANGE;
    private static final Color DEFAULT_FILL   = Color.LIGHTYELLOW;

    public District(String name, int population, double area, LandArea landArea) {
        super(name, population, area, landArea);
        setBorderColor(DEFAULT_BORDER);
        setFill(DEFAULT_FILL);
    }
}
