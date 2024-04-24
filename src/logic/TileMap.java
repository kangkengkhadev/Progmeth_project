package logic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import render.Renderable;
import util.Config;

public class TileMap implements Renderable {
    private static Image wallSolidSprite = new Image(ClassLoader.getSystemResource("wall_solid.png").toString());
    private static Image wallParallelSprite = new Image(ClassLoader.getSystemResource("wall_parallel.png").toString());
    private static Image wallTurnSprite = new Image(ClassLoader.getSystemResource("wall_turn.png").toString());
    private static Image wallJunctionSprite = new Image(ClassLoader.getSystemResource("wall_junction.png").toString());
    private static Image wallTerminalSprite = new Image(ClassLoader.getSystemResource("wall_terminal.png").toString());
    private boolean destroyed;
    private Map map;

    public TileMap(Map map) {
        this.map = map;
        setDestroyed(false);
    }

    @Override
    public void draw(GraphicsContext gc) {
        double xPadding = GameController.getInstance().getGamePanel().getXPadding();
        double yPadding = GameController.getInstance().getGamePanel().getYPadding();
        double unitWidth = GameController.getInstance().getGamePanel().getUnitWidth();

        // Iterate through the map info and render the walls
        for (int i = 0; i < map.getRow(); i++) {
            for (int j = 0; j < map.getCol(); j++) {
                int mapCode = map.getMapInfo()[i][j];
                int mapItemsCode = map.getMapItemsInfo()[i][j];
                Image img = null;
                double rotation = 0;
                if (mapItemsCode == 1) {
                    gc.setFill(Color.YELLOW);
                    gc.fillOval(xPadding + (j+0.5) * unitWidth, yPadding + (i+0.5) * unitWidth, 4, 4);
                }
                if (mapCode == 0) {
                    img = wallParallelSprite;
                } else if (mapCode == 1) {
                    rotation = 90;
                    img = wallParallelSprite;
                } else if (mapCode == 2) {
                    img = wallTurnSprite;
                } else if (mapCode == 3) {
                    rotation = 90;
                    img = wallTurnSprite;
                } else if (mapCode == 4) {
                    rotation = 180;
                    img = wallTurnSprite;
                } else if (mapCode == 5) {
                    rotation = 270;
                    img = wallTurnSprite;
                } else if (mapCode == 6) {
                    img = wallJunctionSprite;
                } else if (mapCode == 7) {
                    rotation = 90;
                    img = wallJunctionSprite;
                } else if (mapCode == 8) {
                    rotation = 180;
                    img = wallJunctionSprite;
                } else if (mapCode == 9) {
                    rotation = 270;
                    img = wallJunctionSprite;
                } else if (mapCode == 10) {
                    img = wallTerminalSprite;
                } else if (mapCode == 11) {
                    rotation = 90;
                    img = wallTerminalSprite;
                } else if (mapCode == 12) {
                    rotation = 180;
                    img = wallTerminalSprite;
                } else if (mapCode == 13) {
                    rotation = 270;
                    img = wallTerminalSprite;
                } else if (mapCode == 14) {
                    img = wallSolidSprite;
                }

                // Save the current transformation matrix
                gc.save();
                // Translate the canvas to the center of the unit
                gc.translate(xPadding + j * unitWidth + unitWidth / 2, yPadding + i * unitWidth + unitWidth / 2);
                // Rotate the canvas based on the rotation
                gc.rotate(rotation);
                // Draw the image at the center of the unit
                gc.drawImage(img, -unitWidth / 2, -unitWidth / 2, unitWidth, unitWidth);
                // Restore the transformation matrix for the next iteration
                gc.restore();

            }
        }
    }

    @Override
    public int getZIndex() {
        return Config.TILE_MAP_Z_INDEX;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    @Override
    public boolean isDestroyed() {
        return false;
    }
}
