package tde.importers;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import tde.model.*;
import tde.model.LandArea.Area;

import java.util.ArrayList;
import java.util.List;

public class XMLHandler extends DefaultHandler {
    public static final String TLM_LANDESGEBIET = "swissBOUNDARIES3D_ili2_LV95_V1_5.TLM_GRENZEN.TLM_LANDESGEBIET";
    public static final String TLM_HOHEITSGEBIET = "swissBOUNDARIES3D_ili2_LV95_V1_5.TLM_GRENZEN.TLM_HOHEITSGEBIET";
    public static final String TLM_KANTONSGEBIET = "swissBOUNDARIES3D_ili2_LV95_V1_5.TLM_GRENZEN.TLM_KANTONSGEBIET";
    public static final String TLM_BEZIRKSGEBIET = "swissBOUNDARIES3D_ili2_LV95_V1_5.TLM_GRENZEN.TLM_BEZIRKSGEBIET";

    private final List<Country> countries = new ArrayList<>();
    private final List<Canton> cantons = new ArrayList<>();
    private final List<District> districts = new ArrayList<>();
    private final List<Municipality> municipalities = new ArrayList<>();

    private final StringBuilder charBuffer = new StringBuilder();

    private String currentTid;
    private String currentName;
    private int currentEinwohnerzahl;
    private double currentGemFlaeche;

    private List<Area> currentAreas;
    private List<Area.Boundaries> currentBoundaries;
    private List<Coordinates> currentCoords;
    private double coordE, coordN, coordH;

    private boolean inBoundary = false;

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) {
        charBuffer.setLength(0);

        switch (qName) {
            case TLM_LANDESGEBIET, TLM_HOHEITSGEBIET, TLM_KANTONSGEBIET, TLM_BEZIRKSGEBIET -> {
                currentTid = attributes.getValue("TID");
                resetScalars();
            }
            case "MultiSurface" -> currentAreas = new ArrayList<>();
            case "Surface" -> currentBoundaries = new ArrayList<>();
            case "Boundary", "POLYLINE" -> {
                inBoundary = true;
                currentCoords = new ArrayList<>();
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        charBuffer.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        String value = charBuffer.toString().trim();
        charBuffer.setLength(0);

        switch (qName) {

            case "Name" -> currentName = value;
            case "Einwohnerzahl" -> currentEinwohnerzahl = parseInt(value);
            case "Gem_Flaeche", "Kantonsflaeche", "Bezirksflaeche", "Landesflaeche" ->
                    currentGemFlaeche = parseDouble(value);

            case "C1" -> coordE = parseDouble(value);
            case "C2" -> coordN = parseDouble(value);
            case "C3" -> {
                coordH = parseDouble(value);
                if (inBoundary && currentCoords != null) {
                    currentCoords.add(new Coordinates(coordE, coordN, coordH));
                }
            }

            case "POLYLINE",
                 "Boundary" -> {
                if (currentBoundaries != null && currentCoords != null) {
                    currentBoundaries.add(new Area.Boundaries(List.copyOf(currentCoords)));
                }
                inBoundary = false;
                currentCoords = null;
            }
            case "Surface" -> {
                if (currentAreas != null && currentBoundaries != null) {
                    currentAreas.add(new Area(List.copyOf(currentBoundaries)));
                }
                currentBoundaries = null;
            }

            case TLM_LANDESGEBIET -> buildLandArea().ifPresent(la ->
                    countries.add(new Country(safeName(), currentEinwohnerzahl,
                            currentGemFlaeche, la)));
            case TLM_KANTONSGEBIET -> buildLandArea().ifPresent(la ->
                    cantons.add(new Canton(safeName(), currentEinwohnerzahl,
                            currentGemFlaeche, la)));

            case TLM_BEZIRKSGEBIET -> buildLandArea().ifPresent(la ->
                    districts.add(new District(safeName(), currentEinwohnerzahl,
                            currentGemFlaeche, la)));

            case TLM_HOHEITSGEBIET -> buildLandArea().ifPresent(la ->
                    municipalities.add(new Municipality(safeName(), currentEinwohnerzahl,
                            currentGemFlaeche, la)));

        }
    }

    private java.util.Optional<LandArea> buildLandArea() {
        if (currentAreas == null || currentAreas.isEmpty()) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(new LandArea(List.copyOf(currentAreas)));
    }

    private String safeName() {
        return (currentName != null && !currentName.isEmpty())
                ? currentName : "TID:" + currentTid;
    }

    private void resetScalars() {
        currentName = null;
        currentEinwohnerzahl = 0;
        currentGemFlaeche = 0.0;
        currentAreas = null;
    }

    private int parseInt(String v) {
        try {
            return v.isEmpty() ? 0 : Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parseDouble(String v) {
        try {
            return v.isEmpty() ? 0.0 : Double.parseDouble(v);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public List<Country> getCountries() {
        return countries;
    }

    public List<Canton> getCantons() {
        return cantons;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public List<Municipality> getMunicipalities() {
        return municipalities;
    }
}