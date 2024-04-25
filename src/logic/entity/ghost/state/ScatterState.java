package logic.entity.ghost.state;

import logic.Vector2D;
import logic.entity.ghost.BaseGhost;
import logic.fsm.BaseState;
import util.Config;

public class ScatterState extends BaseState {
    BaseGhost ghost;

    public ScatterState(BaseGhost ghost) {
        this.ghost = ghost;
    }

    @Override
    public void update(double delta) {
        Vector2D newTarget = switch (ghost.getClass().getSimpleName()) {
            case "YellowGhost" -> new Vector2D(Config.YELLOW_GHOST_X_ORIGIN, Config.YELLOW_GHOST_Y_ORIGIN);
            case "OrangeGhost" -> new Vector2D(Config.ORANGE_GHOST_X_ORIGIN, Config.ORANGE_GHOST_Y_ORIGIN);
            case "GreenGhost" -> new Vector2D(Config.GREEN_GHOST_X_ORIGIN, Config.GREEN_GHOST_Y_ORIGIN);
            default -> null;
        };

        ghost.setTarget(newTarget);
    }

    @Override
    public void onEnter() {
        long scatterDuration = switch (ghost.getClass().getSimpleName()) {
            case "YellowGhost" -> Config.YELLOW_GHOST_SCATTER_DURATION;
            case "OrangeGhost" -> Config.ORANGE_GHOST_SCATTER_DURATION;
            case "GreenGhost" -> Config.GREEN_GHOST_SCATTER_DURATION;
            default -> 0;
        };

        Thread scatterThread = new Thread(() -> {
            try {
                Thread.sleep(scatterDuration * 1000);
                if (ghost.getFsm().getCurrentState().getClass().getSimpleName().equals("ScatterState")) {
                    ghost.getFsm().changeState(new ChaseState(ghost));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        scatterThread.start();
    }

    @Override
    public void onExit() {

    }
}
