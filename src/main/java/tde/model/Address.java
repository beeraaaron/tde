package tde.model;

import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;

/** Represents an address in Switzerland or Liechtenstein.
 * @param location       coordinates according to LV95.
 * @param modified       date of last update to this address.
 * @param number         house number.
 * @param official       official address according to the [Federal Register of Buildings and Dwellings]
 *     (https://www.bfs.admin.ch/bfs/en/home/registers/federal-register-buildings-dwellings.html "RBD").
 * @param status         realisation status according the RBD.
 * @param category       building category.
 * @param buildingName   building name.
 * @param canton         2 letter abbreviation of canton.
 * @param communityName  name of community (city).
 * @param streetName     street name.
 * @param zipLabel       zip and community name.
 */
public record Address(
        Coordinates location,
        LocalDate modified,
        String number,
        boolean official,
        String status,
        String category,
        String buildingName,
        String canton,
        String communityName,
        String streetName,
        String zipLabel
)implements Drawable {
    private static final String MODIFICATION_DATE_EXC = "Modification date must not be in the future";
    private static final String NUMBER_NAME_EXC = "Either number or building name must be present";
    private static final String EMPTY_EXC = "Canton, community name, street name and zip label must not be null";
    private static final String CANTON_LENGTH_EXC = "Canton must be provided by its 2 letter abbreviation";

    @SuppressWarnings("ParameterNumber")
    public Address {
        checkModificationDate(modified);
        checkEitherPresent(number, buildingName);
        checkNonEmpty(canton, communityName, streetName, zipLabel);
    }

    @Override
    public String toString() {
        return String.format(
"""
e: %7.0f n:%7.0f
%s %s, %s
%s-%s, %s

%s %s address
%s
Modified: %s
""",
                location.east(), location.north(),
                streetName, number, buildingName,
                canton, zipLabel, communityName,
                official ? "Official" : "Non-official", status,
                category,
                modified.format(BASIC_ISO_DATE));
    }

    private void checkNonEmpty(String aCanton, String aCommunityName, String aStreetName, String aZipLabel) {
        if (aCommunityName == null || aStreetName == null || aZipLabel == null
            || aCommunityName.isBlank() || aStreetName.isBlank() || aZipLabel.isBlank()) {
            throw new IllegalArgumentException(EMPTY_EXC);
        }
        if ((aCanton != null && !aCanton.isBlank()) && aCanton.length() != 2) {
            throw new IllegalArgumentException(CANTON_LENGTH_EXC);
        }
    }

    private void checkEitherPresent(String aNumber, String aBuildingName) {
        if (aNumber != null && aNumber.isBlank()) {
            aNumber = null;
        }
        String name = aBuildingName;
        if (name != null && name.isBlank()) {
            name = null;
        }
        if (aNumber == null && name == null) {
            throw new IllegalArgumentException(NUMBER_NAME_EXC);
        }
    }

    private void checkModificationDate(LocalDate modifiedDate) {
        if (LocalDate.now().isBefore(modifiedDate)) {
            throw new IllegalArgumentException(MODIFICATION_DATE_EXC);
        }
    }

    @Override
    public void draw(Pane p, Transform t) {
    }

    @Override
    public boolean contains(double x, double y) {
        return false;
    }
}
