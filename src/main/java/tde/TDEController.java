package tde;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.xml.sax.SAXException;
import tde.db.DataService;
import tde.db.SimpleDataService;
import tde.importers.CSVLoader;
import tde.importers.XMLHandler;
import tde.maps.Map;
import tde.maps.MapsController;
import tde.maps.TerritoryMap;
import tde.model.Address;
import tde.model.Country;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
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

    private final XMLHandler saxHandler = new XMLHandler();
    private final CSVLoader csvLoader = new CSVLoader();

    private final DataService database = new SimpleDataService();

    protected void initialize() {
        var countries = database.getAllCountries();

        Map<Country> map = new TerritoryMap<>("Countries", countries, new Pane());

        MapsController maps = new MapsController(center, this);
        maps.setMap(map);
    }

    @FXML
    protected void onLoadBuildings() {
        status.setText("Load buildings addresses...");
        try {
            File file = chooseFile("Load CSV File containing building data");
            List<Address> addresses = csvLoader.readAddressData(file);
            database.storeAddressesFromLoader(addresses);
        } catch (IOException ioe) {
            showErrorMessage("buildings", ioe.getMessage());
        }
        status.setText("Building addresses loaded");
    }

    @FXML
    protected void onLoadBoundaries() {
        status.setText("Load boundaries...");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();
            File file = chooseFile("Load XML File containing boundary data");
            saxParser.parse(file, saxHandler);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            showErrorMessage("boundaries", e.getMessage());
        }
        // TODO: store loaded data in SimpleDataService
        status.setText("Boundaries loaded");
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

    public void updateMouseProperties(
            double scaleFactor,
            Point2D mouse,
            Point2D coordAtMouse) {
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
}
