package logic;

import util.Config;

public class YellowGhost extends LowGhost {
    private GhostState state;

    public YellowGhost(double x, double y, double width, double height, double speed, String imgPath) {
        super(x, y, width, height, speed, imgPath);
        startChase();
    }

    private void startChase() {
        state = GhostState.CHASE;
        updateTarget();
        Thread chaseThread = new Thread(() -> {
            try {
                Thread.sleep(Config.YELLOW_GHOST_CHASE_DURATION * 1000);
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
        System.out.println("Yellow Ghost State: Scatter");
        updateTarget();
        Thread scatterThread = new Thread(() -> {
            try {
                Thread.sleep(Config.YELLOW_GHOST_SCATTER_DURATION * 1000);
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
        if (state == GhostState.CHASE) {
            target = new Vector2D(playerDiscretePosition.getX(), playerDiscretePosition.getY() - 1);
        } else if (state == GhostState.SCATTER) {
            target = new Vector2D(Config.YELLOW_GHOST_X_ORIGIN, Config.YELLOW_GHOST_Y_ORIGIN);
        }
    }

    public GhostState getState() {
        return state;
    }
}
