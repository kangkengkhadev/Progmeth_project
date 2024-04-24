package logic;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import render.Renderable;
import util.Config;

import java.util.ArrayList;
import java.util.Comparator;

public class GameController {
    private static final GameController instance = new GameController();
    private Map map = new Map(Config.MAP_Y_DIMENSION, Config.MAP_X_DIMENSION, "grid_data_out.csv");
    private ArrayList<Renderable> renderedEntities = new ArrayList<Renderable>();
    private ArrayList<Ghost> ghosts = new ArrayList<Ghost>();
    private Comparator<Renderable> comparator;
    private Pacman pacman;
    private GamePanel gamePanel;
    private TileMap tileMap;

    public void start(GraphicsContext gc) {
        // Get the game panel
        gamePanel = (GamePanel)gc.getCanvas();
        // Set the comparator for sorting entities
        comparator = (Renderable p, Renderable q) -> {
            // Higher zIndex means the entity is rendered on top
            if (p.getZIndex() == q.getZIndex()) {
                return 0;
            }
            return (p.getZIndex() > q.getZIndex() ? -1 : 1);
        };
        // Create the tile map
        tileMap = new TileMap(map);
        // Add the tile map to the list of rendered entities
        addNewEntity(tileMap);
        // Create the pacman
        pacman = new Pacman(Config.PACMAN_X_ORIGIN, Config.PACMAN_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth(), "Pacman.PNG");
        // Add the pacman to the list of rendered entities
        addNewEntity(pacman);
        // Create the ghosts
        addNewGhost(new YellowGhost(Config.YELLOW_GHOST_X_ORIGIN, Config.YELLOW_GHOST_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth(), "YellowGhost.PNG"));
    }

    public void update(double delta) {
        // Update the pacman
        pacman.update(delta);
        // Update the ghosts
        for (Ghost ghost : ghosts) {
            ghost.update(delta);
        }
    }

    public void render(GraphicsContext gc) {
        // Clear the canvas
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // Fill the canvas with black color
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        // Render the map
        renderEntities(gc);
    }

    private void addNewGhost(Ghost ghost) {
        ghosts.add(ghost);
        addNewEntity(ghost);
    }

    private void addNewEntity(Renderable rendererObj) {
        // Add the entity to the list of rendered entities
        renderedEntities.add(rendererObj);
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

    public Map getMap() {
        return map;
    }

    public static GameController getInstance() {
        return instance;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public Pacman getPacman() {
        return pacman;
    }
}
