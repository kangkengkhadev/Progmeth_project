package logic.entity.ghost;

import logic.GameController;
import logic.entity.Pacman;

public class TankGhost extends BaseGhost {
    public TankGhost(double x, double y, double width, double height, double speed, String imgPath) {
        super(x, y, width, height, speed, imgPath);
    }

    @Override
    public void attack() {
        Pacman player = GameController.getInstance().getPacman();
        player.takeDamage(2);
    }
}
