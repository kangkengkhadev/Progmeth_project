package logic;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import logic.entity.Pacman;
import logic.entity.ghost.*;
import logic.entity.item.Cloak;
import logic.entity.item.BaseItem;
import logic.entity.item.FreezePotion;
import render.Renderable;
import util.Config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedDeque;

public class GameController {
    private static final GameController instance = new GameController();
    private Map map = new Map(Config.MAP_Y_DIMENSION, Config.MAP_X_DIMENSION);
    private ArrayList<Renderable> renderedEntities = new ArrayList<Renderable>();
    private ArrayList<BaseGhost> ghosts = new ArrayList<BaseGhost>();
    private ArrayList<BaseItem> items = new ArrayList<BaseItem>();
    private Comparator<Renderable> comparator;
    private Pacman pacman;
    private GamePanel gamePanel;
    private TileMap tileMap;
    private int score = 0;
    private int numItems = 1;

    public void start(GraphicsContext gc) {
        // Get the game panel
        gamePanel = (GamePanel) gc.getCanvas();
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
        pacman = new Pacman(Config.PACMAN_X_ORIGIN, Config.PACMAN_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth());
        // Add the pacman to the list of rendered entities
        addNewEntity(pacman);
        // Create the ghosts
        addNewGhost(new YellowGhost(Config.YELLOW_GHOST_X_ORIGIN, Config.YELLOW_GHOST_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth(), Config.NORMAL_GHOST_SPEED, "YellowGhost.PNG"));
//        addNewGhost(new OrangeGhost(Config.ORANGE_GHOST_X_ORIGIN, Config.ORANGE_GHOST_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth(), Config.NORMAL_GHOST_SPEED, "OrangeGhost.PNG"));
//        addNewGhost(new GreenGhost(Config.GREEN_GHOST_X_ORIGIN, Config.GREEN_GHOST_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth(), Config.NORMAL_GHOST_SPEED, "GreenGhost.PNG"));
//        addNewGhost(new TankGhost(Config.TANK_GHOST_X_ORIGIN, Config.TANK_GHOST_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth(), Config.NORMAL_GHOST_SPEED, "TankGhost.PNG"));
//        addNewGhost(new SwiftGhost(Config.SWIFT_GHOST_X_ORIGIN, Config.SWIFT_GHOST_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth(), Config.NORMAL_GHOST_SPEED, "SwiftGhost.PNG"));
//        addNewGhost(new ScaffGhost(Config.SCAFF_GHOST_X_ORIGIN, Config.SCAFF_GHOST_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth(), Config.NORMAL_GHOST_SPEED, "ScaffGhost.PNG"));
    }

    private void spawnItem() {
        if (score == numItems * Config.NEXT_PHRASE_AMOUNT_THRESHOLD) {
            numItems++;
            ArrayList<Vector2D> candidatePositions = new ArrayList<Vector2D>();
            Map map = GameController.getInstance().getMap();
            Vector2D pacmanCurrentDiscretePosition = new Vector2D((int) pacman.getPosition().getX(), (int) pacman.getPosition().getY());
            for (int i = 0; i < map.getRow(); i++) {
                for (int j = 0; j < map.getCol(); j++) {
                    if (map.getMapInfo()[i][j] == -1 && !pacmanCurrentDiscretePosition.equals(new Vector2D(j, i))) {
                        candidatePositions.add(new Vector2D(j, i));
                    }
                }
            }

            Vector2D randomPosition = candidatePositions.get(new Random().nextInt(candidatePositions.size()));
            double prob = Math.random();
            BaseItem item = (prob < 0.5) ? new Cloak(randomPosition.getX(), randomPosition.getY(), gamePanel.getUnitWidth(), gamePanel.getUnitWidth())
                                        : new FreezePotion(randomPosition.getX(), randomPosition.getY(), gamePanel.getUnitWidth(), gamePanel.getUnitWidth());
            addNewItem(item);
        }
    }

    public void update(double delta) {
        pacman.update(delta);
        for (BaseGhost ghost : ghosts) {
            ghost.update(delta);
        }
        spawnItem();
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

    private void addNewGhost(BaseGhost ghost) {
        ghosts.add(ghost);
        addNewEntity(ghost);
    }

    private void addNewItem(BaseItem item) {
        items.add(item);
        addNewEntity(item);
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

    public ArrayList<BaseGhost> getGhosts() {
        return ghosts;
    }

    public ArrayList<BaseItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<BaseItem> items) {
        this.items = items;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
