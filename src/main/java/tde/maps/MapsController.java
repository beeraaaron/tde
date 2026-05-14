package tde.maps;

import javafx.geometry.Point2D;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import tde.TDEController;

import java.util.ArrayList;
import java.util.List;

public class MapsController {
    private static final double INCREASE_BY_10_PERCENT = 1.1;  // 10% bigger
    private static final double DECREASE_BY_10_PERCENT = 0.9;  // 10% smaller

    private final TDEController mainController;
    private List<Map<?>> maps;
    private final StackPane root;

    private Point2D pivot = new Point2D(0, 0);
    private Point2D screenpivot = new Point2D(0, 0);

    private Point2D coordAtMouse = new Point2D(0.0, 0.0);
    private Point2D mouse = new Point2D(0.0, 0.0);

    private Point2D dragOffset = new Point2D(0.0, 0.0);
    private Point2D dragStart = new Point2D(Double.NaN, Double.NaN);

    private double scaleFactor = Double.NaN;

    public MapsController(StackPane root, TDEController mainController) {
        this.mainController = mainController;
        this.root = root;

        this.root.setOnMouseMoved(e -> onMouseMoved(e));
        this.root.setOnMousePressed(e -> onMousePressed(e));
        this.root.setOnMouseClicked(_ -> onMouseClicked());
        this.root.setOnMouseReleased(e -> onMouseReleased(e));
        this.root.setOnMouseDragged(e -> onMouseDragged(e));
        this.root.setOnScroll(e -> onScroll(e));
        this.root.setOnMouseExited(_ -> onMouseExited());

        mainController.updateMouseProperties(scaleFactor, null, null);
    }

    public void setMaps(List<Map<?>> maps) {
        clearMap();
        this.maps = maps;
        initLayer(this.maps);

        var t = computeInitialScaleFactorAndPosition();
        drawScene(t);
        mainController.updateMouseProperties(scaleFactor, mouse, coordAtMouse);
    }

    private void clearMap() {
        maps = null;
        root.getChildren().clear();
    }

    private void initLayer(List<Map<?>> maps) {
        var panes = new ArrayList<Pane>();
        maps.forEach(map -> {
            var checkBox = new CheckBox();
            checkBox.textProperty().bind(mainController.getI18n().bind(map.getName()));
            checkBox.setSelected(map.isVisible());
            checkBox.selectedProperty().addListener((_, _, newValue) -> {
                map.setVisible(newValue);
                drawScene(lv95ToScreen());
            });
            panes.add(map.getPane());
        });
        root.getChildren().addAll(panes);
    }

    private Transform computeInitialScaleFactorAndPosition() {
        var boundingBox = calculateBoundingBox();
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

    private Rectangle calculateBoundingBox() {
        double minX = 0;
        double minY = 0;
        double maxX = 0;
        double maxY = 0;
        for (var map : maps) {
            var b = map.getBoundingBox();
            minX = Math.min(minX, b.getX());
            minY = Math.min(minY, b.getY());
            maxX = Math.max(maxX, b.getX() + b.getWidth());
            maxY = Math.max(maxY, b.getY() + b.getHeight());
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    private Transform screenToLV95() {
        try {
            return lv95ToScreen().createInverse();
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException(e);
        }
    }

    public Transform lv95ToScreen() {
        Scale s = new Scale(1.0 / scaleFactor, -1.0 / scaleFactor, pivot.getX(), pivot.getY());
        Translate t = new Translate(screenpivot.getX() + dragOffset.getX() - pivot.getX(),
                screenpivot.getY() + dragOffset.getY() - pivot.getY());
        return t.createConcatenation(s);
    }

    protected void onMouseMoved(MouseEvent e) {
        mouse = new Point2D(e.getX(), e.getY());
        coordAtMouse = screenToLV95().transform(mouse);
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
        maps.forEach(map -> map.onMouseClicked(evt));
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
        mouse = new Point2D(e.getX(), e.getY());
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

    public void drawScene(Transform t) {
        maps.forEach(map -> map.draw(t));
    }

    public record TPEMouseEvent(Point2D mouse, Point2D coord) { }
}
