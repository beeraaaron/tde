package tde.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountryTest extends AbstractTerritoryTest {
    @Test
    void ctor() {
        var country = create();
        assertEquals("Country", country.getName());
        assertEquals(4321, country.getPopulation());
        assertEquals(12345.67, country.getArea(), 0.005);
        assertNotNull(country.getBoundaries());
    }

    @Override
    Country create() {
        return new Country("Country", 4321, 12345.67, createSingleLandArea());
    }
}
