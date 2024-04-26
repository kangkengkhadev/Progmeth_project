package logic.entity.ghost.state;

import logic.GameController;
import logic.entity.Pacman;
import logic.Vector2D;
import logic.entity.ghost.BaseGhost;
import logic.fsm.BaseState;
import util.Config;

public class ChaseState extends BaseState {
    BaseGhost ghost;

    public ChaseState(BaseGhost ghost) {
        this.ghost = ghost;
    }

    @Override
    public void update(double delta) {
        Pacman player = GameController.getInstance().getPacman();
        Vector2D playerDiscretePosition = new Vector2D((int)player.getPosition().getX(), (int)player.getPosition().getY());
        Vector2D playerVelocity = player.getVelocity();
        Vector2D targetOfGreenGhost = new Vector2D(playerDiscretePosition.getX() + playerVelocity.getX() / Config.PACMAN_SPEED,
                playerDiscretePosition.getY() + playerVelocity.getY() / Config.PACMAN_SPEED);
        int randomXPosition = (int) (Math.random() * Config.MAP_X_DIMENSION);
        int randomYPosition = (int) (Math.random() * Config.MAP_Y_DIMENSION);
        Vector2D newTarget = switch (ghost.getClass().getSimpleName()) {
            case "YellowGhost" -> new Vector2D(playerDiscretePosition.getX(), playerDiscretePosition.getY() - 1);
            case "OrangeGhost" -> new Vector2D(playerDiscretePosition.getX(), playerDiscretePosition.getY());
            case "GreenGhost" -> targetOfGreenGhost;
            case "TankGhost" -> new Vector2D(playerDiscretePosition.getX()-(targetOfGreenGhost.getX()-ghost.getPosition().getX()),playerDiscretePosition.getY()+(targetOfGreenGhost.getY()-ghost.getPosition().getX()));
            case "SwiftGhost" -> new Vector2D(playerDiscretePosition.getX(), playerDiscretePosition.getY() + 1);
            case "ScaffGhost" -> new Vector2D(randomXPosition, randomYPosition);
            default -> null;
        };
        ghost.setTarget(newTarget);
    }

    @Override
    public void onEnter() {
        long chaseDuration = switch (ghost.getClass().getSimpleName()) {
            case "YellowGhost" -> Config.YELLOW_GHOST_CHASE_DURATION;
            case "OrangeGhost" -> Config.ORANGE_GHOST_CHASE_DURATION;
            case "GreenGhost" -> Config.GREEN_GHOST_CHASE_DURATION;
            case "TankGhost" -> Config.TANK_GHOST_CHASE_DURATION;
            case "SwiftGhost" -> Config.SWIFT_GHOST_CHASE_DURATION;
            case "ScaffGhost" -> Config.SCAFF_GHOST_CHASE_DURATION;
            default -> 0;
        };

        Thread chaseThread = new Thread(() -> {
            try {
                Thread.sleep(chaseDuration * 1000);
                if (ghost.getFsm().getCurrentStateName().equals("ChaseState")) {
                    ghost.getFsm().changeState(new ScatterState(ghost));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        chaseThread.start();
    }

    @Override
    public void onExit() {

    }
}
