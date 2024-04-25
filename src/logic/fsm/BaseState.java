package logic.fsm;

public abstract class BaseState {
    public abstract void update(double delta);
    public abstract void onEnter();
    public abstract void onExit();
}
