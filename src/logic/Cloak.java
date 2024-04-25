package logic;

import util.Config;

public class Cloak extends Item{

    public Cloak(double x, double y, double width, double height, String imagePath) {
        super(x, y, width, height, imagePath);
    }

    @Override
    public void useEffect() {
        GameController.getInstance().getPacman().startInvincible(Config.INVINCIBILITY_DURATION);
    }
}
