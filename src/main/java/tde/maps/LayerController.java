package tde.maps;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import tde.TDEController;

import java.util.ArrayList;
import java.util.List;

public class LayerController {
    private final TDEController mainController;
    private final VBox root;
    private List<Map<?>> maps;


    public LayerController(VBox root, TDEController mainController) {
        this.mainController = mainController;
        this.root = root;
    }

    public void setMaps(List<Map<?>> maps) {
        clearMap();
        this.maps = maps;
        initLayer(this.maps);
    }

    private void initLayer(List<Map<?>> maps) {
        var checkBoxes = new ArrayList<CheckBox>();
        maps.forEach(map -> {
            var checkBox = new CheckBox(map.getName());
            checkBox.setSelected(map.isVisible());
            checkBox.selectedProperty().addListener((_, _, newValue) -> {
                map.setVisible(newValue);
                mainController.drawScene();
            });
            checkBoxes.add(checkBox);
        });
        root.getChildren().addAll(checkBoxes);
    }

    private void clearMap() {
        maps = null;
        root.getChildren().clear();
    }
}
