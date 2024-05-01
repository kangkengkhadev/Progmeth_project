package logic.entity.item;

import logic.GameController;
import logic.Vector2D;
import logic.entity.ghost.BaseGhost;
import logic.entity.ghost.ScaffGhost;
import util.Config;

import java.util.ArrayList;

public class FreezePotion extends BaseItem {
    public FreezePotion(double x, double y, double width, double height) {
        super(x, y, width, height, "Freeze.png");
    }
    @Override
    public void useEffect() {
        for(BaseGhost ghost : GameController.getInstance().getGhosts()){
            ghost.startFreeze();
        }
    }
}
