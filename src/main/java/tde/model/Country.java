package tde.model;

import javafx.scene.paint.Color;

/**
 * Represents a Country.
 */
public class Country extends AbstractTerritory {
    private static final Color DEFAULT_BORDER = Color.BLUE;
    private static final Color DEFAULT_FILL = Color.PALETURQUOISE;

    public Country(String name, int population, double area, LandArea landArea) {
        super(name, population, area, landArea);
        setBorderColor(DEFAULT_BORDER);
        setFill(DEFAULT_FILL);
    }
}
