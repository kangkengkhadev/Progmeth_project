package logic.entity.ghost;

import util.Config;

public class SwiftGhost extends BaseGhost {
    public SwiftGhost(double x, double y, double width, double height, double speed) {
        super(x, y, width, height, speed, "SwiftGhost.png");
    }

    @Override
    protected double getSpeedMultiplier() {
        if (fsm.getCurrentStateName().equals("FrightenedState") || fsm.getCurrentStateName().equals("RespawnState") || fsm.getCurrentStateName().equals("FreezeState")) {
            return super.getSpeedMultiplier();
        }
        return Config.SWIFT_GHOST_SPEED_MULTIPLIER;
    }
}
