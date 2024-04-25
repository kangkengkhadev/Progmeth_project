package logic.entity.ghost.state;

import logic.fsm.BaseState;
import logic.entity.ghost.BaseGhost;
import logic.Vector2D;

public class RespawnState extends BaseState {
    private BaseGhost ghost;

    public RespawnState(BaseGhost ghost) {
        this.ghost = ghost;
    }

    @Override
    public void update(double delta) {
        if (ghost.getPosition().getX() == 23 && ghost.getPosition().getY() == 10) {
            ghost.getFsm().changeState(new SpawnState(ghost));
        }
    }

    @Override
    public void onEnter() {
        Vector2D newTarget = new Vector2D(23, 10);
        ghost.setTarget(newTarget);
    }

    @Override
    public void onExit() {

    }
}
