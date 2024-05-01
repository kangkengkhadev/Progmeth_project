package logic.entity.item;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.entity.Entity;
import logic.GameController;
import util.Config;

public abstract class BaseItem extends Entity {
    private Image sprite;

    public BaseItem(double x, double y, double width, double height, String imagePath) {
        super(x, y, width, height);
        this.sprite = new Image(ClassLoader.getSystemResource(imagePath).toString());
    }

    @Override
    public int getZIndex() {
        return Config.ITEM_Z_INDEX;
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (!destroyed) {
            gc.drawImage(sprite,
                    position.getX() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(),
                    position.getY() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding(),
                    width,
                    height);
        }
    }

    public void useEffect(){

    }
}
