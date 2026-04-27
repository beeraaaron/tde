package tde.model;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    Address cut = new Address(
            new Coordinates(260.0, 130.0, 0),
            LocalDate.of(2026, Month.APRIL, 3),
            "25",
            true,
            "REAL",
            "UNCATEGORIZED",
            "Haus",
            "ZH",
            "Meilen",
            "Hauptstrasse",
            "2341 Meilen"
    );

    @Test
    void testToString() {
        assertEquals(
"""
e:     260 n:    130
Hauptstrasse 25, Haus
ZH-2341 Meilen, Meilen

Official REAL address
UNCATEGORIZED
Modified: 20260403
"""
                , cut.toString());
    }

    @Disabled
    @Test
    void testDraw() {
        Pane pane = new Pane();
        Transform transform = new Translate();
        cut.draw(pane, transform);
        assertEquals(1, pane.getChildren().size());
        Node node = pane.getChildren().getFirst();
        assertInstanceOf(Shape.class, node);
    }

    @Disabled
    @Test
    void testContains() {
        assertTrue(cut.contains(265, 135));
        assertFalse(cut.contains(280, 90));
    }
}
