package logic.entity.ghost;

public class ScaffGhost extends BaseGhost {
    public ScaffGhost(double x, double y, double width, double height, double speed) {
        super(x, y, width, height, speed, "ScaffGhost.PNG");
    }

    @Override
    public void startFreeze() {
        // Do nothing
        return;
    }
}
