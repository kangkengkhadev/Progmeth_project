package logic;

import util.Config;

public class Cloak extends Item{

    public Cloak(double x, double y, double width, double height) {
        super(x, y, width, height, "Cloak.png");
    }

    @Override
    public void useEffect() {
        GameController.getInstance().getPacman().startInvincible(Config.INVINCIBILITY_DURATION);
    }
}
