package tde.model;

import java.util.List;

/**
 * The land area of a territory can be complex, it may have enclaves and exclaves. In other words
 * the land area of a territory does not necessarily have to be contiguous and its pieces may have holes!
 * <p>
 * Enclaves are areas within a territory that do not belong to it and are completely surrounded by the territory.
 * Exclaves, on the other hand, are areas that have no connection to the main territory.
 * <p></p>
 * <b>Note</b>: Not all exclaves are enclaves within another territory. Conversely, it is possible
 * for an enclave to hold be more than one exclave.
 * <ul>
 *     <li>
 *         The canton of Schaffhausen consists of three areas and none of them is an enclave in a other canton or
 *         country, as all these exclaves have boundaries to Germany <em>and</em> another cantons (Thurgau and Zürich).
 *     </li>
 *     <li>
 *         Both cantons of Appenzell-Ausserrhoden and Appenzell-Innerrhoden are completely surrounded by
 *         the territory of the canton of St. Gallen.
 *     </li>
 * </ul>
 * <p>
 *
 * @param landArea a list of areas that territory (first in list) and exclaves.
 *                 Must not be null or empty
 * @see <a href="https://de.wikipedia.org//wiki//Liste_von_Exklaven_und_Enklaven/#Schweiz">
 *          Liste von Exklaven und Enkalven der Schweiz
 *      </a>
 * @see <a href="https://en.wikipedia.org/wiki/Canton_of_Schaffhausen#Geography">
 *          Canton of Schaffhausen (Geography)
 *      </a>
 * @see <a href="https://en.wikipedia.org/wiki/Canton_of_St._Gallen#Geography">
 *          Canton of St. Gallen (Geography)
 *      </a>
 */
public record LandArea(List<Area> landArea) {
    public LandArea {
        if (landArea == null || landArea.isEmpty()) {
            throw new IllegalArgumentException("landArea is null or empty");
        }
    }

    /**
     * A list of areas that together make up the territory.
     * @param boundaries a list of main territory (first in list) and enclaves that do not belong to the territory.
     *                   Must not be null or empty
     */
    public record Area(List<Boundaries> boundaries) {
        public Area {
            if (boundaries == null || boundaries.isEmpty()) {
                throw new IllegalArgumentException("boundaries must not be null or empty");
            }
        }

        /**
         * A list of coordinate triplets (in LV95) that define the boundaries of an area. These coordinates
         * describe the border of a polygon.
         * @param coordinates a list of {@link tde.model.Coordinates} must not be null or empty
         */
        public record Boundaries(List<Coordinates> coordinates) {
            public Boundaries {
                if (coordinates == null || coordinates.isEmpty()) {
                    throw new IllegalArgumentException("coordinates must not be null or empty");
                }
            }
        }
    }

}
