package logic;

import util.Config;

public class Cloak extends Item{

    public Cloak(double x, double y, double width, double height) {
        super(x, y, width, height, "Cloak.png");
    }

    @Override
    public void useEffect() {
        Vector2D discretePosition = new Vector2D((int)position.getX(), (int)position.getY());
        GameController.getInstance().getPacman().startInvincible(Config.INVINCIBILITY_DURATION);
        GameController.getInstance().getMap().getMapItemsInfo()[(int)discretePosition.getY()][(int)discretePosition.getX()] = -1;
    }
}
