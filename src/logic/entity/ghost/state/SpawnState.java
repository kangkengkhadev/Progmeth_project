package logic.entity.ghost.state;

import logic.Vector2D;
import logic.entity.ghost.BaseGhost;
import logic.fsm.BaseState;
import util.Config;

public class SpawnState extends BaseState {
    private BaseGhost ghost;

    public SpawnState(BaseGhost ghost) {
        this.ghost = ghost;
    }

    @Override
    public void update(double delta) {
        if (ghost.getPosition().getX() == Config.GHOST_X_ORIGIN && ghost.getPosition().getY() == Config.GHOST_Y_ORIGIN - 1) {
            ghost.getFsm().changeState(new ChaseState(ghost));
        }
    }

    @Override
    public void onEnter() {
        Vector2D newTarget = new Vector2D(Config.GHOST_X_ORIGIN, Config.GHOST_Y_ORIGIN - 1);
        ghost.setTarget(newTarget);
        ghost.getVelocity().setX(0);
        ghost.getVelocity().setY(-Config.NORMAL_GHOST_SPEED);
    }

    @Override
    public void onExit() {

    }
}
