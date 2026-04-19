package tde;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TDEApplication extends Application {
    private static final int SCENE_WIDTH  = 1536;
    private static final int SCENE_HEIGHT = 1024;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(TDEController.class.getResource("tde-main.fxml"));

        var generalController = new TDEController();

        fxmlLoader.setController(generalController);
        Scene scene = new Scene(fxmlLoader.load(), SCENE_WIDTH, SCENE_HEIGHT);

        stage.setTitle("Topo Data Explorer");
        stage.setOnCloseRequest(_ -> generalController.onAppExit());
        stage.setScene(scene);
        stage.show();
        generalController.initialize();
    }
}
