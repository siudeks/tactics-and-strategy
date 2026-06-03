package game.engine;

public enum RuntimePhase {
    ISSUE_ORDERS(TurnPhase.ISSUE_ORDERS),
    SIMULTANEOUS_MOVE(TurnPhase.SIMULTANEOUS_MOVE),
    COMBAT(TurnPhase.COMBAT),
    RETREAT(TurnPhase.RETREAT),
    END_TURN(TurnPhase.END_TURN),
    COMPLETE(TurnPhase.ISSUE_ORDERS, true);

    private final TurnPhase turnPhase;
    private final boolean complete;

    RuntimePhase(TurnPhase turnPhase) {
        this.turnPhase = turnPhase;
        this.complete = false;
    }

    RuntimePhase(TurnPhase turnPhase, boolean complete) {
        this.turnPhase = turnPhase;
        this.complete = complete;
    }

    public boolean isComplete() {
        return complete;
    }

    public TurnPhase turnPhase() {
        if (complete) {
            throw new IllegalStateException("COMPLETE does not map to a turn phase");
        }
        return turnPhase;
    }

    public static RuntimePhase fromTurnPhase(TurnPhase turnPhase) {
        return switch (turnPhase) {
            case ISSUE_ORDERS -> ISSUE_ORDERS;
            case SIMULTANEOUS_MOVE -> SIMULTANEOUS_MOVE;
            case COMBAT -> COMBAT;
            case RETREAT -> RETREAT;
            case END_TURN -> END_TURN;
        };
    }
}