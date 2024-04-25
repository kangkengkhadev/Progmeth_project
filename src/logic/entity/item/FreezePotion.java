package logic.entity.item;

import logic.GameController;
import logic.Vector2D;
import logic.entity.ghost.BaseGhost;
import util.Config;

import java.util.ArrayList;

public class FreezePotion extends BaseItem {
    public FreezePotion(double x, double y, double width, double height) {
        super(x, y, width, height, "Freeze.png");
    }
    @Override
    public void useEffect() {
        ArrayList<BaseGhost> closedGhost = new ArrayList<BaseGhost>();
        for(BaseGhost ghost : GameController.getInstance().getGhosts()){
            Vector2D ghostPosition = new Vector2D((int)ghost.getCentroid().getX() + 0.5, (int)ghost.getCentroid().getY() + 0.5);
            Vector2D ghostVec = new Vector2D(ghostPosition.getX() - getCentroid().getX(), ghostPosition.getY() - getCentroid().getY());
            if (ghostVec.getLength() < Config.PACMAN_COLLISION_RADIUS*20) {
                closedGhost.add(ghost);
            }
        }
        for(BaseGhost ghost : closedGhost){
            ghost.setDestroyed(true);
            GameController.getInstance().getGhosts().remove(ghost);
        }
    }
}
