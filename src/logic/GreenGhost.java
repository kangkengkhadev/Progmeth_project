package logic;

import util.Config;

public class GreenGhost extends LowGhost {
    private GhostState state;

    public GreenGhost(double x, double y, double width, double height, double speed, String imgPath) {
        super(x, y, width, height, speed, imgPath);
        startChase();
    }

    private void startChase() {
        state = GhostState.CHASE;
        updateTarget();
        Thread chaseThread = new Thread(() -> {
            try {
                Thread.sleep(Config.GREEN_GHOST_CHASE_DURATION * 1000);
                if (getState() == GhostState.CHASE) {
                    startScatter();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        chaseThread.start();
    }

    private void startScatter() {
        state = GhostState.SCATTER;
        System.out.println("Green Ghost State: Scatter");
        updateTarget();
        Thread scatterThread = new Thread(() -> {
            try {
                Thread.sleep(Config.GREEN_GHOST_SCATTER_DURATION * 1000);
                if (getState() == GhostState.SCATTER) {
                    startChase();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        scatterThread.start();
    }

    protected void updateTarget() {
        Pacman player = GameController.getInstance().getPacman();
        Vector2D playerDiscretePosition = new Vector2D((int)player.getPosition().getX(), (int)player.getPosition().getY());
        Vector2D playerVelocity = player.getVelocity();
        if (state == GhostState.CHASE) {
            target = new Vector2D(playerDiscretePosition.getX() + playerVelocity.getX() / Config.PACMAN_SPEED,
                                  playerDiscretePosition.getY() + playerVelocity.getY() / Config.PACMAN_SPEED);
        } else if (state == GhostState.SCATTER) {
            target = new Vector2D(Config.GREEN_GHOST_X_ORIGIN, Config.GREEN_GHOST_Y_ORIGIN);
        }
    }

    public GhostState getState() {
        return state;
    }
}
