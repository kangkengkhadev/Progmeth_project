package logic;

public class YellowGhost extends LowGhost {
    public YellowGhost(double x, double y, double width, double height, String imgPath) {
        super(x, y, width, height, imgPath);
    }

    @Override
    protected void updateTarget() {
        Pacman player = GameController.getInstance().getPacman();
        Vector2D playerDiscretePosition = new Vector2D((int)player.getPosition().getX(), (int)player.getPosition().getY());
        target = new Vector2D(playerDiscretePosition.getX(), playerDiscretePosition.getY() - 1);
    }
}
