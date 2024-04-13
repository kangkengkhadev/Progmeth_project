package logic;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import util.Config;

import java.util.ArrayList;
import java.util.Comparator;

public class GameController {
    private static final GameController instance = new GameController();
    private final Map map = new Map(Config.MAP_Y_DIMENSION, Config.MAP_X_DIMENSION, "grid_data_out.csv");;
    private final ArrayList<Entity> renderedEntities = new ArrayList<Entity>();
    private double unitWidth;
    private Comparator<Entity> comparator;
    private double xPadding;
    private double yPadding;
    private Pacman pacman;

    public void start(GraphicsContext gc) {
        unitWidth = Math.min(gc.getCanvas().getWidth() / Config.MAP_X_DIMENSION, gc.getCanvas().getHeight() / Config.MAP_Y_DIMENSION);
        comparator = (Entity p, Entity q) -> {
            if (p.getZIndex() == q.getZIndex()) {
                return 0;
            }
            return (p.getZIndex() > q.getZIndex() ? 1 : -1);
        };
        xPadding = (gc.getCanvas().getWidth() - unitWidth * Config.MAP_X_DIMENSION) / 2.0;
        yPadding = (gc.getCanvas().getHeight() - unitWidth * Config.MAP_Y_DIMENSION) / 2.0;
        pacman = new Pacman(Config.PACMAN_X_ORIGIN, Config.PACMAN_Y_ORIGIN, unitWidth, "Pacman.PNG");
        addNewEntity(pacman);
    }

    public void update(double delta) {
        pacman.update(delta);
    }

    public void render(GraphicsContext gc) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        TileMap.renderMap(gc, map);
        renderEntities(gc);
    }

    private void addNewEntity(Entity entity) {
        renderedEntities.add(entity);
        renderedEntities.sort(comparator);
    }

    private void renderEntities(GraphicsContext gc) {
        for (int i = renderedEntities.size() - 1; i >= 0; i--) {
            if (renderedEntities.get(i).isDestroyed()) {
                renderedEntities.remove(i);
            } else {
                renderedEntities.get(i).draw(gc);
            }
        }
    }

    public static GameController getInstance() {
        return instance;
    }

    public double getUnitWidth() {
        return this.unitWidth;
    }

    public double getXPadding() {
        return xPadding;
    }

    public double getYPadding() {
        return yPadding;
    }
}
