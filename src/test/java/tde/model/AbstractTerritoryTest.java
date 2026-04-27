package tde.model;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the abstract base class for territories. Uses a factory
 * method to get a class under test.
 * <p>
 *     <b>Important Note:</b> Every class that extends AbstractTerritory
 *     should provide a test class that extends this class. Such an extension
 *     must provide an implementation for the factory method. So whenever
 *     a concrete class is tested, the abstract base class is tested too.
 * </p>
 * <p>
 *     In return this base class provides protected factory methods for
 *     various {@link LandArea} constellations for use in the derived
 *     test classes.
 * </p>
 */
abstract class AbstractTerritoryTest {
    private AbstractTerritory cut; // class under test

    @Test
    void draw() {
        cut = create();
        Pane pane = new Pane();
        Transform transform = new Translate();
        cut.setBoundaries(createMultiLandAreaWithHole());
        cut.draw(pane, transform);
        assertEquals(1, pane.getChildren().size());
        Node node = pane.getChildren().getFirst();
        assertInstanceOf(Shape.class, node);
    }

    @Test
    void testName() {
        cut = create();
        cut.setName("hello");
        assertEquals("hello",  cut.getName());

        assertThrows(IllegalArgumentException.class, () -> cut.setName(null));
        var e = assertThrows(IllegalArgumentException.class, () -> cut.setName(""));
        assertEquals("Name must not be null or empty", e.getMessage());
    }

    @Test
    void testPopulation() {
        cut = create();
        cut.setPopulation(5);
        assertEquals(5, cut.getPopulation());
        cut.setPopulation(0);
        assertEquals(0, cut.getPopulation());

        var e = assertThrows(IllegalArgumentException.class, () -> cut.setPopulation(-1));
        assertEquals("Population must not be < 0", e.getMessage());
    }

    @Test
    void testArea() {
        cut = create();
        cut.setArea(542.3);
        assertEquals(542.3, cut.getArea(), 0.05);
        cut.setArea(0);
        assertEquals(0.0, cut.getArea());

        assertThrows(IllegalArgumentException.class, () -> cut.setArea(-Double.MIN_VALUE));
        assertThrows(IllegalArgumentException.class, () -> cut.setArea(Double.NEGATIVE_INFINITY));
    }

    @Test
    void testBorderWidth() {
        cut = create();
        cut.setArea(542.3);
        assertEquals(542.3, cut.getArea(), 0.05);
        cut.setArea(0);
        assertEquals(0.0, cut.getArea());

        assertThrows(IllegalArgumentException.class, () -> cut.setArea(-Double.MIN_VALUE));
        assertThrows(IllegalArgumentException.class, () -> cut.setArea(Double.NEGATIVE_INFINITY));
    }

    @Test
    void testBorderColor() {
        cut = create();
        cut.setBorderColor(Color.CYAN);
        assertEquals(Color.CYAN, cut.getBorderColor());

        assertThrows(IllegalArgumentException.class, () -> cut.setBorderColor(null));
    }

    @Test
    void testFill() {
        cut = create();
        cut.setFill(Color.RED);
        assertEquals(Color.RED, cut.getFill());
        assertThrows(IllegalArgumentException.class, () -> cut.setFill(null));
    }

    @Test
    void testBoundaries() {
        cut = create();
        var t = assertThrows(IllegalArgumentException.class, () -> cut.setBoundaries(null));
        assertEquals("Boundaries must not be null or empty", t.getMessage());
    }

    @Test
    void getBoundingBox() {
        cut = create();
        cut.setBoundaries(createSingleLandArea());
        var r = cut.getBoundingBox();
        assertEquals(1, r.getX(), 0.05);
        assertEquals(3, r.getY(), 0.05);
        assertEquals(3, r.getWidth(), 0.05);
        assertEquals(5, r.getHeight(), 0.05);

        cut.setBoundaries(createMultiLandArea());
        r = cut.getBoundingBox();
        assertEquals(1, r.getX(), 0.05);
        assertEquals(1, r.getY(), 0.05);
        assertEquals(8, r.getWidth(), 0.05);
        assertEquals(7, r.getHeight(), 0.05);
    }

    @Test
    void contains() {
        cut = create();
        cut.setBoundaries(createSingleLandArea());
        assertTrue(cut.contains(2, 4));
        assertFalse(cut.contains(4, 5));

        cut.setBoundaries(createMultiLandArea());
        assertTrue(cut.contains(2, 4));
        assertTrue(cut.contains(7.5, 1.5));
        assertFalse(cut.contains(4, 5));

        cut.setBoundaries(createSingleLandAreaWithHole());
        assertTrue(cut.contains(2, 4));
        assertFalse(cut.contains(4, 5));
        assertFalse(cut.contains(2.5, 5.5));

        cut.setBoundaries(createMultiLandAreaWithHole());
        assertTrue(cut.contains(2, 4));
        assertTrue(cut.contains(7.5, 1.5));
        assertFalse(cut.contains(4, 5));
        assertFalse(cut.contains(2.5, 5.5));
    }

    abstract AbstractTerritory create();

    /**
     * Create one single piece of land like this:
     *  ***
     *  ***
     *  ***
     *  ***
     *  ***
     * |
     * |
     * |___ <- the coordinates origin
     * @return the above shown land area.
     */
    protected LandArea createSingleLandArea() {
        var boundaries = new LandArea.Area.Boundaries(List.of(
                new Coordinates(1, 3, 0),
                new Coordinates(1, 8, 0),
                new Coordinates(4, 8, 0),
                new Coordinates(4, 3, 0)
        ));
        var area = new LandArea.Area(List.of(boundaries));
        return new LandArea(List.of(area));
    }

    /**
     * Create one single piece of land with a hole like this:
     *  ***
     *  ***
     *  * *
     *  ***
     *  ***
     * |
     * |
     * +-- <- the coordinates origin
     * @return the above shown land area.
     */
    protected LandArea createSingleLandAreaWithHole() {
        var boundaries = new LandArea.Area.Boundaries(List.of(
                new Coordinates(1, 3, 0),
                new Coordinates(1, 8, 0),
                new Coordinates(4, 8, 0),
                new Coordinates(4, 3, 0)
        ));
        var hole = new LandArea.Area.Boundaries(List.of(
                new Coordinates(2, 5, 0),
                new Coordinates(2, 6, 0),
                new Coordinates(3, 6, 0),
                new Coordinates(3, 5, 0)
        ));
        var area = new LandArea.Area(List.of(boundaries, hole));
        return new LandArea(List.of(area));
    }

    /**
     * Create two non-contiguous pieces of land like this:
     *  ***
     *  ***
     *  ***
     *  ***
     *  ***
     * |      **
     * |
     * +-- <- the coordinates origin
     * @return the above shown land area.
     */
    protected LandArea createMultiLandArea() {
        var boundaries1 = new LandArea.Area.Boundaries(List.of(
                new Coordinates(1, 3, 0),
                new Coordinates(1, 8, 0),
                new Coordinates(4, 8, 0),
                new Coordinates(4, 3, 0)
        ));
        var boundaries2 = new LandArea.Area.Boundaries(List.of(
                new Coordinates(7, 1, 0),
                new Coordinates(7, 2, 0),
                new Coordinates(9, 2, 0),
                new Coordinates(9, 1, 0)
        ));
        var area1 = new LandArea.Area(List.of(boundaries1));
        var area2 = new LandArea.Area(List.of(boundaries2));
        return new LandArea(List.of(area1, area2));
    }

    /**
     * Create two non-contiguous pieces of land like this:
     *  ***
     *  ***
     *  * *
     *  ***
     *  ***
     * |       **
     * |
     * +-- <- the coordinates origin
     * @return the above shown land area.
     */
    protected LandArea createMultiLandAreaWithHole() {
        var boundaries1 = new LandArea.Area.Boundaries(List.of(
                new Coordinates(1, 3, 0),
                new Coordinates(1, 8, 0),
                new Coordinates(4, 8, 0),
                new Coordinates(4, 3, 0)
        ));
        var hole = new LandArea.Area.Boundaries(List.of(
                new Coordinates(2, 5, 0),
                new Coordinates(2, 6, 0),
                new Coordinates(3, 6, 0),
                new Coordinates(3, 5, 0)
        ));
        var boundaries2 = new LandArea.Area.Boundaries(List.of(
                new Coordinates(7, 1, 0),
                new Coordinates(7, 2, 0),
                new Coordinates(9, 2, 0),
                new Coordinates(9, 1, 0)
        ));
        var area1 = new LandArea.Area(List.of(boundaries1, hole));
        var area2 = new LandArea.Area(List.of(boundaries2));
        return new LandArea(List.of(area1, area2));
    }

}
