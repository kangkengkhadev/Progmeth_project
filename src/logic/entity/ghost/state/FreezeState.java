package logic.entity.ghost.state;

import logic.entity.ghost.BaseGhost;
import logic.fsm.BaseState;

public class FreezeState extends BaseState {
    private BaseGhost ghost;

    public FreezeState(BaseGhost ghost) {
        this.ghost = ghost;
    }

    @Override
    public void update(double delta) {

    }

    @Override
    public void onEnter() {

    }

    @Override
    public void onExit() {

    }
}
