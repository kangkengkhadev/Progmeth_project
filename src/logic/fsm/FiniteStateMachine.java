package logic.fsm;

public class FiniteStateMachine {
    private BaseState initialState;
    private BaseState currentState;
    private String currentStateName;

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

    public String getCurrentStateName() {
        return currentState.getClass().getSimpleName();
    }
}
