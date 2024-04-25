package logic.entity.item;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import logic.entity.Entity;
import logic.GameController;
import util.Config;

public abstract class Item extends Entity {
    private Image sprite;
    private boolean isDestroyed;

    public Item(double x, double y, double width, double height, String imagePath) {
        super(x, y, width, height);
        this.sprite = new Image(ClassLoader.getSystemResource(imagePath).toString());
        this.isDestroyed = false;
    }

    @Override
    public int getZIndex() {
        return Config.ITEM_Z_INDEX;
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (!isDestroyed) {
            gc.drawImage(sprite,
                    position.getX() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(),
                    position.getY() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding(),
                    width,
                    height);
            gc.setFill(Color.GREEN);
            gc.fillRect(position.getX() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(),
                    position.getY() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding(),
                    5,
                    5);
        }
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void destroy() {
        isDestroyed = true;
    }


    public void useEffect(){

    }
}
