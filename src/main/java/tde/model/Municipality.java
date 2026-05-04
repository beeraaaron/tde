package tde.model;

import javafx.scene.paint.Color;

public class Municipality extends AbstractTerritory {
    private static final Color DEFAULT_BORDER = Color.DARKRED;
    private static final Color DEFAULT_FILL   = Color.LIGHTSALMON;

    public Municipality(String name, int population, double area, LandArea landArea) {
        super(name, population, area, landArea);
        setBorderColor(DEFAULT_BORDER);
        setFill(DEFAULT_FILL);
    }
}
