package logic.entity.ghost.state;

import logic.fsm.BaseState;
import logic.entity.ghost.BaseGhost;
import logic.Vector2D;
import util.Config;

public class RespawnState extends BaseState {
    private BaseGhost ghost;

    public RespawnState(BaseGhost ghost) {
        this.ghost = ghost;
    }

    @Override
    public void update(double delta) {
        if (ghost.getPosition().getX() == Config.GHOST_X_ORIGIN && ghost.getPosition().getY() == Config.GHOST_Y_ORIGIN) {
            ghost.getFsm().changeState(new SpawnState(ghost));
        }
    }

    @Override
    public void onEnter() {
        Vector2D newTarget = new Vector2D(Config.GHOST_X_ORIGIN, Config.GHOST_Y_ORIGIN);
        ghost.setTarget(newTarget);
    }

    @Override
    public void onExit() {

    }
}
