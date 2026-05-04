package tde;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.xml.sax.SAXException;
import tde.db.DataService;
import tde.db.SimpleDataService;
import tde.importers.CSVLoader;
import tde.importers.XMLHandler;
import tde.maps.*;
import tde.model.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TDEController {
    @FXML private Label status;
    @FXML private Label mouseX;
    @FXML private Label mouseY;
    @FXML private Label coordE;
    @FXML private Label coordN;
    @FXML private Label scaleLabel;
    @FXML private BorderPane mainStructure;

    @FXML private StackPane center;
    @FXML private VBox layers;

    private final XMLHandler xmlHandler = new XMLHandler();
    private final CSVLoader csvLoader = new CSVLoader();

    private final DataService database = new SimpleDataService();

    MapsController mapController;
    LayerController layerController;

    protected void initialize() {
        Map<Country> countries = new TerritoryMap<>("Countries", database.getAllCountries(), new Pane());
        Map<Canton> kantone = new TerritoryMap<>("Cantons", database.getAllCantons(), new Pane());
        Map<District> bezirke = new TerritoryMap<>("Districts", database.getAllDistricts(), new Pane());
        Map<Municipality> hoheiten = new TerritoryMap<>("Municipalities", database.getAllMunicipalities(), new Pane());

        Map<Address> buildingsMap = new BuildingsMap("Buildings", database.getAllAddresses(), new Pane());

        var maps = new ArrayList<Map<?>>(List.of(countries, kantone, bezirke, hoheiten, buildingsMap));

        mapController = new MapsController(center, this);
        mapController.setMaps(maps);

        layerController = new LayerController(layers, this);
        layerController.setMaps(maps);
    }

    @FXML
    protected void onLoadBuildings() {
        status.setText("Load buildings addresses...");
        try {
            File file = chooseFile("Load CSV File containing building data");
            List<Address> addresses = csvLoader.readAddressData(file, "UR");
            database.storeAddressesFromLoader(addresses);
            status.setText("Building addresses loaded");
        } catch (IOException ioe) {
            showErrorMessage("buildings", ioe.getMessage());
        }
        initialize();
    }

    @FXML
    protected void onLoadBoundaries() {
        status.setText("Load boundaries...");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();
            File file = chooseFile("Load XML File containing boundary data");
            saxParser.parse(file, xmlHandler);
            database.storeTerritoriesFromLoader(xmlHandler);
            status.setText("Boundaries loaded");
        } catch (IOException | ParserConfigurationException | SAXException | IllegalArgumentException e) {
            showErrorMessage("boundaries", e.getMessage());
        }
        initialize();
    }

    private File chooseFile(String title) throws IOException {
        var chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.setInitialDirectory(new File(""));
        return chooser.showOpenDialog(null);
    }

    @FXML
    protected void onAppExit() {
        Platform.exit();
    }

    public void updateMouseProperties(double scaleFactor, Point2D mouse, Point2D coordAtMouse) {
        scaleLabel.setText(String.format("1 : %.0f", scaleFactor));
        if (mouse == null) {
            mouseX.setText("");
            mouseY.setText("");
        } else {
            mouseX.setText(String.format("%7.0f", mouse.getX()));
            mouseY.setText(String.format("%7.0f", mouse.getY()));
        }
        if (coordAtMouse == null) {
            coordE.setText("");
            coordN.setText("");
        } else {
            coordE.setText(String.format("%7.0f", coordAtMouse.getX()));
            coordN.setText(String.format("%7.0f", coordAtMouse.getY()));
        }
    }

    private void showErrorMessage(String subject, String msg) {
        status.setText(String.format("Could not load %s due to error: %s", subject, msg));
    }

    public void drawScene() {
        mapController.drawScene(mapController.lv95ToScreen());
    }
}
