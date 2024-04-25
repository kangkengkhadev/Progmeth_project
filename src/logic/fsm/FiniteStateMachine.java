package logic.fsm;

public class FiniteStateMachine {
    BaseState initialState;
    BaseState currentState;

    public FiniteStateMachine (BaseState initialState) {
        this.initialState = initialState;
        currentState = initialState;
        initialState.onEnter();
    }

    public void update(double delta) {
        currentState.update(delta);
    }

    public void changeState(BaseState newState) {
        currentState.onExit();
        currentState = newState;
        currentState.onEnter();
    }

    public BaseState getCurrentState() {
        return currentState;
    }
}
