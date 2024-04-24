package logic;

import util.Config;

public class OrangeGhost extends LowGhost {
    private GhostState state;

    public OrangeGhost(double x, double y, double width, double height, String imgPath) {
        super(x, y, width, height, imgPath);
        startChase();
    }

    private void startChase() {
        state = GhostState.CHASE;
        System.out.println("Orange Ghost State: Chase");
        updateTarget();
        Thread chaseThread = new Thread(() -> {
            try {
                Thread.sleep(Config.ORANGE_GHOST_CHASE_DURATION * 1000);
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
        System.out.println("Orange Ghost State: Scatter");
        updateTarget();
        Thread scatterThread = new Thread(() -> {
            try {
                Thread.sleep(Config.ORANGE_GHOST_SCATTER_DURATION * 1000);
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
            target = new Vector2D(playerDiscretePosition.getX(), playerDiscretePosition.getY());
        } else if (state == GhostState.SCATTER) {
            target = new Vector2D(Config.ORANGE_GHOST_X_ORIGIN, Config.ORANGE_GHOST_Y_ORIGIN);
        }
    }

    public GhostState getState() {
        return state;
    }
}
