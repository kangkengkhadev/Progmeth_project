package logic;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import logic.entity.Pacman;
import logic.entity.ghost.*;
import logic.entity.item.Cloak;
import logic.entity.item.BaseItem;
import logic.entity.item.FreezePotion;
import render.GamePanel;
import render.Renderable;
import util.Config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class GameController {
    private static final GameController instance = new GameController();
    private Map map;
    private ArrayList<Renderable> renderedEntities;
    private ArrayList<BaseGhost> ghosts;
    private ArrayList<BaseItem> items;
    private Comparator<Renderable> comparator;
    private Pacman pacman;
    private GamePanel gamePanel;
    private TileMap tileMap;
    private int score;
    private int numItems;
    private boolean spawnable;
    private boolean isGameOver;

    public void start(GraphicsContext gc) {
        map = new Map(Config.MAP_Y_DIMENSION, Config.MAP_X_DIMENSION);
        renderedEntities = new ArrayList<Renderable>();
        ghosts = new ArrayList<BaseGhost>();
        items = new ArrayList<BaseItem>();
        gamePanel = (GamePanel) gc.getCanvas();
        comparator = (Renderable p, Renderable q) -> {
            if (p.getZIndex() == q.getZIndex()) {
                return 0;
            }
            return (p.getZIndex() > q.getZIndex() ? -1 : 1);
        };
        tileMap = new TileMap(map);
        addNewEntity(tileMap);
        pacman = new Pacman(Config.PACMAN_X_ORIGIN, Config.PACMAN_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth());
        addNewEntity(pacman);
        addNewGhost(new YellowGhost(Config.GHOST_X_ORIGIN, Config.GHOST_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth(), Config.NORMAL_GHOST_SPEED, "YellowGhost.PNG"));
        score = 0;
        numItems = 1;
        spawnable = true;
        isGameOver = false;
    }

    private void spawnItem() {
        if (score == numItems * Config.NEXT_PHRASE_AMOUNT_THRESHOLD) {
            numItems++;
            ArrayList<Vector2D> candidatePositions = new ArrayList<Vector2D>();
            Map map = GameController.getInstance().getMap();
            Vector2D pacmanCurrentDiscretePosition = new Vector2D((int) pacman.getCentroid().getX(), (int) pacman.getCentroid().getY());
            for (int i = 0; i < map.getRow(); i++) {
                for (int j = 0; j < map.getCol(); j++) {
                    if (map.getMapItemsInfo()[i][j] == -1 && !pacmanCurrentDiscretePosition.equals(new Vector2D(j, i))) {
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

    private void spawnGhost() {
        if (score % Config.NEXT_PHRASE_AMOUNT_THRESHOLD != 0) spawnable = true;
        if (!spawnable) return;

        if (score == Config.NEXT_PHRASE_AMOUNT_THRESHOLD) {
            addNewGhost(new OrangeGhost(Config.GHOST_X_ORIGIN, Config.GHOST_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth(), Config.NORMAL_GHOST_SPEED, "OrangeGhost.PNG"));
            spawnable = false;
        } else if (score == 2 * Config.NEXT_PHRASE_AMOUNT_THRESHOLD) {
            addNewGhost(new GreenGhost(Config.GHOST_X_ORIGIN, Config.GHOST_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth(), Config.NORMAL_GHOST_SPEED, "GreenGhost.PNG"));
            spawnable = false;
        } else if (score == 3 * Config.NEXT_PHRASE_AMOUNT_THRESHOLD) {
            addNewGhost(new ScaffGhost(Config.GHOST_X_ORIGIN, Config.GHOST_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth(), Config.NORMAL_GHOST_SPEED, "ScaffGhost.PNG"));
            spawnable = false;
        } else if (score == 4 * Config.NEXT_PHRASE_AMOUNT_THRESHOLD) {
            addNewGhost(new TankGhost(Config.GHOST_X_ORIGIN, Config.GHOST_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth(), Config.NORMAL_GHOST_SPEED, "TankGhost.PNG"));
            spawnable = false;
        } else if (score == 5 * Config.NEXT_PHRASE_AMOUNT_THRESHOLD) {
            addNewGhost(new SwiftGhost(Config.GHOST_X_ORIGIN, Config.GHOST_Y_ORIGIN, gamePanel.getUnitWidth(), gamePanel.getUnitWidth(), Config.NORMAL_GHOST_SPEED, "SwiftGhost.PNG"));
            spawnable = false;
        }
    }

    public void update(double delta) {
        pacman.update(delta);
        for (BaseGhost ghost : ghosts) {
            ghost.update(delta);
        }
        spawnItem();
        spawnGhost();
    }

    public void render(GraphicsContext gc) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void gameOver() {
        this.isGameOver = true;
    }

    public boolean isGameOver() {
        return this.isGameOver;
    }
}
