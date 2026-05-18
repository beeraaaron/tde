package tde;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
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
import tde.util.I18nService;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TDEController implements Initializable {
    @FXML private Menu fileMenu;
    @FXML private Menu helpMenu;
    @FXML private MenuItem languageMenu;
    @FXML private MenuItem buildingMenu;
    @FXML private MenuItem boundaryMenu;
    @FXML private MenuItem exitMenu;
    @FXML private MenuItem aboutMenu;

    @FXML private Label commandLabel;
    @FXML private Label statusLabel;
    @FXML private Label mouseLabel;
    @FXML private Label coordinateLabel;
    @FXML private Label layerLabel;
    @FXML private Label scaleLabel;

    @FXML private Label status;
    @FXML private Label mouseX;
    @FXML private Label mouseY;
    @FXML private Label coordE;
    @FXML private Label coordN;
    @FXML private Label scale;
    @FXML private BorderPane mainStructure;

    @FXML private StackPane center;
    @FXML private VBox layers;

    private final XMLHandler xmlHandler = new XMLHandler();
    private final CSVLoader csvLoader = new CSVLoader();

    private final I18nService i18n = new I18nService(Locale.GERMAN);
    private final DataService database = new SimpleDataService();

    MapsController mapController;
    LayerController layerController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileMenu.textProperty().bind(i18n.bind("menu.file"));
        helpMenu.textProperty().bind(i18n.bind("menu.help"));

        languageMenu.textProperty().bind(i18n.bind("menu.language"));
        buildingMenu.textProperty().bind(i18n.bind("menu.building"));
        boundaryMenu.textProperty().bind(i18n.bind("menu.boundary"));
        exitMenu.textProperty().bind(i18n.bind("menu.exit"));
        aboutMenu.textProperty().bind(i18n.bind("menu.about"));

        scaleLabel.textProperty().bind(i18n.bind("label.scale"));
        commandLabel.textProperty().bind(i18n.bind("label.command"));
        statusLabel.textProperty().bind(i18n.bind("label.status"));
        mouseLabel.textProperty().bind(i18n.bind("label.mouse"));
        coordinateLabel.textProperty().bind(i18n.bind("label.coordinate"));
        layerLabel.textProperty().bind(i18n.bind("label.layer"));
    }

    protected void initialize() {
        Map<Country> countries = new TerritoryMap<>("map.country", database.getAllCountries(), new Pane());
        Map<Canton> kantone = new TerritoryMap<>("map.canton", database.getAllCantons(), new Pane());
        Map<District> bezirke = new TerritoryMap<>("map.district", database.getAllDistricts(), new Pane());
        Map<Municipality> hoheiten = new TerritoryMap<>("map.municipality", database.getAllMunicipalities(), new Pane());

        Map<Address> buildingsMap = new BuildingsMap("map.building", database.getAllAddresses(), new Pane());

        var maps = new ArrayList<Map<?>>(List.of(countries, kantone, bezirke, hoheiten, buildingsMap));

        mapController = new MapsController(center, this);
        mapController.setMaps(maps);

        layerController = new LayerController(layers, this);
        layerController.setMaps(maps);
    }

    @FXML
    protected void onSwitchLanguage() {
        if (!i18n.getLocale().getLanguage().equals("de")) {
            i18n.setLocale(Locale.GERMAN);
        } else {
            i18n.setLocale(Locale.ENGLISH);
        }
    }

    @FXML
    protected void onLoadBuildings() {
        status.textProperty().bind(i18n.bind("status.load.building"));
        try {
            File file = chooseFile(i18n.get("file.choose.building"));
            List<Address> addresses = csvLoader.readAddressData(file, "UR");
            database.storeAddressesFromLoader(addresses);
            status.textProperty().bind(i18n.bind("status.loaded.building"));
        } catch (Exception e) {
            showErrorMessage("buildings", e.getMessage());
        }
        initialize();
    }

    @FXML
    protected void onLoadBoundaries() {
        status.textProperty().bind(i18n.bind("status.load.boundary"));
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();
            File file = chooseFile(i18n.get("file.choose.boundary"));
            saxParser.parse(file, xmlHandler);
            database.storeTerritoriesFromLoader(xmlHandler);
            status.textProperty().bind(i18n.bind("status.loaded.boundary"));
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
        scale.setText(String.format("1 : %.0f", scaleFactor));
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
        status.textProperty().bind(i18n.bind("status.error", subject, msg));
    }

    public void drawScene() {
        mapController.drawScene(mapController.lv95ToScreen());
    }

    public I18nService getI18n() {
        return i18n;
    }
}
