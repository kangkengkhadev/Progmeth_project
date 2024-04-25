package logic.entity.ghost.state;

import logic.Vector2D;
import logic.entity.ghost.BaseGhost;
import logic.fsm.BaseState;
import util.Config;

public class FrightenState extends BaseState {
    private BaseGhost ghost;

    public FrightenState(BaseGhost ghost) {
        this.ghost = ghost;
    }

    @Override
    public void update(double delta) {
        int randomXPosition = (int) (Math.random() * Config.MAP_X_DIMENSION);
        int randomYPosition = (int) (Math.random() * Config.MAP_Y_DIMENSION);

        Vector2D newTarget = new Vector2D(randomXPosition, randomYPosition);
        ghost.setTarget(newTarget);
    }

    @Override
    public void onEnter() {
        Vector2D ghostVelocity = ghost.getVelocity();

        ghostVelocity.setX(-ghostVelocity.getX());
        ghostVelocity.setY(-ghostVelocity.getY());

        Thread frightenThread = new Thread(() -> {
            try {
                Thread.sleep(Config.GHOST_FRIGHTENED_DURATION * 1000);
                if (ghost.getFsm().getCurrentState().getClass().getSimpleName().equals("FrightenState")) {
                    ghost.getFsm().changeState(new ChaseState(ghost));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        frightenThread.start();
    }

    @Override
    public void onExit() {

    }
}
