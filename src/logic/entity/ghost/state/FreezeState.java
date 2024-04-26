package logic.entity.ghost.state;

import logic.entity.ghost.BaseGhost;
import logic.fsm.BaseState;
import util.Config;

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
        Thread freezeThread = new Thread(() -> {
            try {
                Thread.sleep(Config.GHOST_FREEZE_DURATION * 1000);
                if (ghost.getFsm().getCurrentStateName().equals("FreezeState")) {
                    ghost.getFsm().changeState(new ChaseState(ghost));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        freezeThread.start();
    }

    @Override
    public void onExit() {

    }
}
