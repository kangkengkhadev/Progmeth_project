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
        // Get the game panel
        gamePanel = (GamePanel)gc.getCanvas();
        // Set the comparator for sorting entities
        comparator = (Entity p, Entity q) -> {
            // Higher zIndex means the entity is rendered on top
            if (p.getZIndex() == q.getZIndex()) {
                return 0;
            }
            return (p.getZIndex() > q.getZIndex() ? 1 : -1);
        };
        // Create the pacman
        pacman = new Pacman(Config.PACMAN_X_ORIGIN, Config.PACMAN_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth(), "Pacman.PNG");
        // Add the pacman to the list of rendered entities
        addNewEntity(pacman);
    }

    public void update(double delta) {
        // Update the pacman
        pacman.update(delta);
    }

    public void render(GraphicsContext gc) {
        // Clear the canvas
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // Fill the canvas with black color
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        // Render the map
        TileMap.renderMap(gc, map);
        renderEntities(gc);
    }

    private void addNewEntity(Entity entity) {
        // Add the entity to the list of rendered entities
        renderedEntities.add(entity);
        // Sort the list of rendered entities
        renderedEntities.sort(comparator);
    }

    private void renderEntities(GraphicsContext gc) {
        // Iterate through the list of rendered entities
        for (int i = renderedEntities.size() - 1; i >= 0; i--) {
            // If the entity is destroyed, remove it from the list
            // Otherwise, render the entity
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
