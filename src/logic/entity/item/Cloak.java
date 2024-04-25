package logic.entity.item;

import logic.GameController;
import util.Config;

public class Cloak extends BaseItem {

    public Cloak(double x, double y, double width, double height) {
        super(x, y, width, height, "Cloak.png");
    }

    @Override
    public void useEffect() {
        GameController.getInstance().getPacman().startInvincible(Config.INVINCIBILITY_DURATION);
    }
}
