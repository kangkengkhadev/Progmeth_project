package logic.entity.ghost;

import util.Config;

public class SwiftGhost extends BaseGhost {
    public SwiftGhost(double x, double y, double width, double height, double speed) {
        super(x, y, width, height, speed, "SwiftGhost.PNG");
    }

    @Override
    protected double getSpeedMultiplier() {
        if (getFsm().getCurrentStateName().equals("FrightenedState") || getFsm().getCurrentStateName().equals("RespawnState") || getFsm().getCurrentStateName().equals("FreezeState")) {
            return super.getSpeedMultiplier();
        }
        return Config.SWIFT_GHOST_SPEED_MULTIPLIER;
    }
}
