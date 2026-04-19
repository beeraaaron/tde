package tde.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LandAreaBoundariesTest {
    @Test
    void testIllegalLandArea() {
        var e = assertThrows(IllegalArgumentException.class, () -> new LandArea(null));
        assertEquals("landArea is null or empty", e.getMessage());
        e = assertThrows(IllegalArgumentException.class, () -> new LandArea(List.of()));
        assertEquals("landArea is null or empty", e.getMessage());
    }

    @Test
    void testIllegalArea() {
        var e = assertThrows(IllegalArgumentException.class, () -> new LandArea.Area(null));
        assertEquals("boundaries must not be null or empty", e.getMessage());
        e = assertThrows(IllegalArgumentException.class, () -> new LandArea.Area(List.of()));
        assertEquals("boundaries must not be null or empty", e.getMessage());
    }

    @Test
    void testIllegalBoundaries() {
        var e = assertThrows(IllegalArgumentException.class, () -> new LandArea.Area.Boundaries(null));
        assertEquals("coordinates must not be null or empty", e.getMessage());
        e = assertThrows(IllegalArgumentException.class, () -> new LandArea.Area.Boundaries(List.of()));
        assertEquals("coordinates must not be null or empty", e.getMessage());
    }
}
