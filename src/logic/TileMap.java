package logic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class TileMap {
    public static Image wallSolidSprite = new Image(ClassLoader.getSystemResource("wall_solid.png").toString());
    public static Image wallParallelSprite = new Image(ClassLoader.getSystemResource("wall_parallel.png").toString());
    public static Image wallTurnSprite = new Image(ClassLoader.getSystemResource("wall_turn.png").toString());
    public static Image wallJunctionSprite = new Image(ClassLoader.getSystemResource("wall_junction.png").toString());
    public static Image wallTerminalSprite = new Image(ClassLoader.getSystemResource("wall_terminal.png").toString());

    public static void renderMap(GraphicsContext gc, Map map) {
        double xPadding = GameController.getInstance().getGamePanel().getXPadding();
        double yPadding = GameController.getInstance().getGamePanel().getYPadding();
        double unitWidth = GameController.getInstance().getGamePanel().getUnitWidth();

        for (int i = 0; i < map.getRow(); i++) {
            for (int j = 0; j < map.getCol(); j++) {
                int mapCode = map.getMapInfo()[i][j];
                Image img = null;
                double rotation = 0;
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

                gc.save();
                gc.translate(xPadding + j * unitWidth + unitWidth / 2, yPadding + i * unitWidth + unitWidth / 2);
                gc.rotate(rotation);
                gc.drawImage(img, -unitWidth / 2, -unitWidth / 2, unitWidth, unitWidth);
                gc.restore();
            }
        }
    }
}
