package logic;

public abstract class PhysicEntity extends Entity {
    protected double radius;

    public PhysicEntity(double x, double y, double radius) {
        super(x, y);
        setRadius(radius);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
