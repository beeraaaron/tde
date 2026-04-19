package tde.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;

import tde.model.LandArea.Area;

/**
 * Represents a territory which might be a Country, a Canton, a District, or a Municipality.
 */
public abstract class AbstractTerritory implements Territory {
    public static final String NON_EMPTY_EXC = "%s must not be null or empty";
    private static final String NON_NEGATIVE_EXC = "%s must not be < 0";
    private String name;
    private int population;
    private double area;
    private LandArea landArea;

    private double borderWidth = 1.0;
    private Color borderColor = Color.RED;
    private Paint fill = Color.GRAY;

    // Cache for boundaries
    private Shape shape;
    private Rectangle boundingBox;

    public AbstractTerritory(String name,
                             int population,
                             double area,
                             LandArea landArea) {
        setName(name);
        setPopulation(population);
        setArea(area);
        setBoundaries(landArea);
    }

    @Override
    public void draw(Pane p, Transform t) {
        var scale = t.getMxx();
        shape.getTransforms().clear();  // remove any previous transformations
        shape.getTransforms().add(t);   // add current transformation
        shape.setStrokeWidth(borderWidth / scale);  // adjust stroke to scale
        shape.setStroke(borderColor);
        shape.setFill(fill);
        p.getChildren().add(shape);     // add the shape of this AbstractTerritory to the pane
    }

    /* ***************************************** Setters and Getters below **************************************** */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(String.format(NON_EMPTY_EXC, "Name"));
        }
        this.name = name;
    }

    @Override
    public int getPopulation() {
        return population;
    }

    @Override
    public void setPopulation(int population) {
        if (population < 0) {
            throw new IllegalArgumentException(String.format(NON_NEGATIVE_EXC, "Population"));
        }
        this.population = population;
    }

    @Override
    public double getArea() {
        return area;
    }

    @Override
    public void setArea(double area) {
        if (area < 0.0) {
            throw new IllegalArgumentException(String.format(NON_NEGATIVE_EXC, "Area"));
        }
        this.area = area;
    }

    @Override
    public LandArea getBoundaries() {
        return landArea;
    }

    @Override
    public void setBoundaries(LandArea aLandArea) {
        if (aLandArea == null || aLandArea.landArea().isEmpty()) {
            throw new IllegalArgumentException(String.format(NON_EMPTY_EXC, "Boundaries"));
        }
        landArea = aLandArea;
        shape = buildShape(landArea);
    }

    @SuppressWarnings("checkstyle:LeftCurly")
    private Shape buildShape(LandArea aLandArea) {
        // invalidate cache
        Shape result = null;

        // fill cache
        var minX = Double.MAX_VALUE;
        var minY = Double.MAX_VALUE;
        var maxX = -Double.MAX_VALUE;
        var maxY = -Double.MAX_VALUE;

        for (Area a : aLandArea.landArea()) {
            Shape s = null;
            for (Area.Boundaries boundary: a.boundaries()) {
                var polyline = boundary.coordinates();
                double[] pts = new  double[2 * polyline.size()];
                int i = 0;
                for (Coordinates c: polyline) {
                    pts[i * 2] = c.east();
                    pts[i * 2 + 1] = c.north();
                    i++;

                    // determine bounding box
                    if (c.east() < minX) { minX = c.east(); }
                    if (c.east() > maxX) { maxX = c.east(); }
                    if (c.north() < minY) { minY = c.north(); }
                    if (c.north() > maxY) { maxY = c.north(); }
                }
                var polygon = new Polygon(pts);

                polygon.setFill(Color.BLACK); // add fill in order to subtract enclaves and unify exclaves
                if (s == null) {
                    s = polygon;
                } else {
                    s = Shape.subtract(s, new Polygon(pts));
                }
            }
            if (result == null) {
                result = s;
            } else {
                result = Shape.union(result, s);
            }
        }
        boundingBox =  new Rectangle(minX, minY, maxX - minX, maxY - minY);
        return result;
    }

    public double getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(double borderWidth) {
        if (borderWidth < 0.0) {
            throw new IllegalArgumentException(String.format(NON_NEGATIVE_EXC, "Border width"));
        }
        this.borderWidth = borderWidth;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        if (borderColor == null) {
            throw new IllegalArgumentException(String.format(NON_EMPTY_EXC, "Border color"));
        }
        this.borderColor = borderColor;
    }

    public Paint getFill() {
        return fill;
    }

    public void setFill(Paint fill) {
        if (fill == null) {
            throw new IllegalArgumentException(String.format(NON_EMPTY_EXC, "Fill"));
        }
        this.fill = fill;
    }

    @Override
    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    @Override
    public boolean contains(double x, double y) {
        return false;
    }
}
