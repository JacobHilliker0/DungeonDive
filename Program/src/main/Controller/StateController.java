package main.Controller;

public class StateController {
    private GameState currentState;

    public enum GameState {
        EXPLORING,
        COMBAT,
        INVENTORY,
        CHEST,
        PAUSED,
        GAME_OVER,
        VICTORY
    }

    /**
     *constructor initializes game state to exploring
     */
    public StateController() {
        this.currentState = GameState.EXPLORING;
        System.out.println("StateController initialized with EXPLORING state");
    }

    /**
     * Changes current game state
     *
     * @param theNewState new state to change to
     */
    public void changeState(final GameState theNewState) {
        if (theNewState != currentState) {
            System.out.println("Game state changing from " + currentState + " to " + theNewState);
            this.currentState = theNewState;
        }
    }

    /**
     * get current game state
     * @return current game state
     */
    public GameState getCurrentState() {
        return currentState;
    }

    /**
     * checks if game is currently in specified state.
     *
     * @param theState state to check against
     * @return True if current state matches specified state
     */
    public boolean isInState(final GameState theState) {
        return currentState == theState;
    }

    public boolean isInCombat() {
        return currentState == GameState.COMBAT;
    }

}

