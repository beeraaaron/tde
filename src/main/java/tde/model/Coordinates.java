package tde.model;

/**
 * Represents a point on the Swiss Coordinate System (LV95)
 * .
 * @param east     The easting coordinate.
 * @param north    The northing coordinate.
 * @param altitude The altitude of this coordinate.
 * @see <a href="https://www.swisstopo.admin.ch/en/the-swiss-coordinates-system">
 *          Swiss coordinates system (LV95)
 *      </a>
 */
public record Coordinates(double east, double north, double altitude) { }
