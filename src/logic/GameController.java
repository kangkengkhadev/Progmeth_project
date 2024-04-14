package logic;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import util.Config;

import java.util.ArrayList;
import java.util.Comparator;

public class GameController {
    private static final GameController instance = new GameController();
    private final Map map = new Map(Config.MAP_Y_DIMENSION, Config.MAP_X_DIMENSION, "grid_data_out.csv");
    private final ArrayList<Entity> renderedEntities = new ArrayList<Entity>();
    private Comparator<Entity> comparator;
    private Pacman pacman;
    private GamePanel gamePanel;

    public void start(GraphicsContext gc) {
        gamePanel = (GamePanel)gc.getCanvas();
        comparator = (Entity p, Entity q) -> {
            if (p.getZIndex() == q.getZIndex()) {
                return 0;
            }
            return (p.getZIndex() > q.getZIndex() ? 1 : -1);
        };
        pacman = new Pacman(Config.PACMAN_X_ORIGIN, Config.PACMAN_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth(), "Pacman.PNG");
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

    public GamePanel getGamePanel() {
        return gamePanel;
    }
}
