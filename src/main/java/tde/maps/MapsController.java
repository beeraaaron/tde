package tde.maps;

import javafx.geometry.Point2D;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import tde.TDEController;

public class MapsController {
    private static final double INCREASE_BY_10_PERCENT = 1.1;  // 10% bigger
    private static final double DECREASE_BY_10_PERCENT = 0.9;  // 10% smaller

    private final TDEController mainController;
    private Map<?> map;
    private final StackPane root;

    private Point2D pivot = new Point2D(0, 0);
    private Point2D screenpivot = new Point2D(0, 0);

    private Point2D coordAtMouse = new Point2D(0.0, 0.0);
    private Point2D mouse = new Point2D(0.0, 0.0);

    private Point2D dragOffset = new Point2D(0.0, 0.0);
    private Point2D dragStart = new Point2D(Double.NaN, Double.NaN);

    private double scaleFactor = Double.NaN;

    public MapsController(StackPane aRoot, TDEController mainController) {
        this.mainController = mainController;
        root = aRoot;

        root.setOnMouseMoved(e -> onMouseMoved(e));
        root.setOnMousePressed(e -> onMousePressed(e));
        root.setOnMouseClicked(_ -> onMouseClicked());
        root.setOnMouseReleased(e -> onMouseReleased(e));
        root.setOnMouseDragged(e -> onMouseDragged(e));
        root.setOnScroll(e -> onScroll(e));
        root.setOnMouseExited(_ -> onMouseExited());

        mainController.updateMouseProperties(scaleFactor, null, null);
    }

    public void setMap(Map<?> newMap) {
        clearMap();
        map = newMap;
        initLayer(map);

        var t = computeInitialScaleFactorAndPosition();
        drawScene(t);
        mainController.updateMouseProperties(scaleFactor, mouse,  coordAtMouse);
    }

    private void clearMap() {
        map = null;
        root.getChildren().clear();
    }

    private void initLayer(Map<?> map) {
        var checkBox = new CheckBox(map.getName());
        checkBox.setSelected(map.isVisible());
        checkBox.selectedProperty().addListener((_, _, newValue) -> {
            map.setVisible(newValue);
            drawScene(lv95ToScreen());
        });
        root.getChildren().add(map.getPane());
    }

    private Transform computeInitialScaleFactorAndPosition() {
        var boundingBox = map.getBoundingBox();
        pivot = new Point2D(
                boundingBox.getX() + boundingBox.getWidth() / 2.0,
                boundingBox.getY() + boundingBox.getHeight() / 2.0
        );
        screenpivot = new Point2D(root.getWidth() / 2.0, root.getHeight() / 2.0);

        var hRatio = boundingBox.getHeight() / root.getHeight();
        var wRatio = boundingBox.getWidth() / root.getWidth();

        scaleFactor = Math.max(hRatio, wRatio);
        return lv95ToScreen();
    }

    private Transform screenToLV95() {
        Scale s = new Scale(scaleFactor, -scaleFactor, screenpivot.getX(), screenpivot.getY());
        Translate t = new Translate(pivot.getX(), pivot.getY());

        return t.createConcatenation(s); // transformation local -> LV95
    }

    private Transform lv95ToScreen() {
        Scale s = new Scale(1.0 / scaleFactor, -1.0 / scaleFactor, pivot.getX(), pivot.getY());
        Translate t = new Translate(screenpivot.getX() + dragOffset.getX() - pivot.getX(),
                screenpivot.getY() + dragOffset.getY() - pivot.getY());
        return t.createConcatenation(s);
    }

    protected void onMouseMoved(MouseEvent e) {
        mouse = new Point2D(e.getX(), e.getY()); // mouse local coordinates
        Transform t = screenToLV95();
        coordAtMouse = t.transform(mouse);
        mainController.updateMouseProperties(scaleFactor, mouse,  coordAtMouse);
    }

    protected void onScroll(ScrollEvent e) {
        screenpivot = new Point2D(e.getX(), e.getY());
        pivot = coordAtMouse;
        scaleFactor = scaleFactor * (e.getDeltaY() > 0 ? DECREASE_BY_10_PERCENT : INCREASE_BY_10_PERCENT);

        drawScene(lv95ToScreen());
        mainController.updateMouseProperties(scaleFactor, mouse,  coordAtMouse);
    }

    protected void onMouseClicked() {
        TPEMouseEvent evt = new TPEMouseEvent(mouse, coordAtMouse);
        map.onMouseClicked(evt);
    }

    protected void onMousePressed(MouseEvent e) {
        dragStart = new Point2D(e.getX(), e.getY());
    }

    protected void onMouseDragged(MouseEvent e) {
        mouse = new Point2D(e.getX(), e.getY());
        dragOffset = mouse.subtract(dragStart);

        Transform t = lv95ToScreen();
        drawScene(t);
        mainController.updateMouseProperties(scaleFactor, mouse,  coordAtMouse);
    }

    protected void onMouseReleased(MouseEvent e) {
        mouse = new Point2D(e.getX(), e.getY()); // mouse local coordinates
        screenpivot = screenpivot.add(dragOffset);

        dragStart = new Point2D(Double.NaN, Double.NaN);
        dragOffset = new Point2D(0.0, 0.0);
        var t = screenToLV95();
        coordAtMouse = t.transform(mouse);
        mainController.updateMouseProperties(scaleFactor, mouse,  coordAtMouse);
    }

    protected void onMouseExited() {
        mainController.updateMouseProperties(scaleFactor, null, null);
    }

    private void drawScene(Transform t) {
        map.draw(t);
    }

    public record TPEMouseEvent(Point2D mouse, Point2D coord) { }
}
