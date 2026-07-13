package game.engine;

import java.util.Objects;

public record MoveCommandResult(MoveCommandOutcome outcome) {
    public MoveCommandResult {
        Objects.requireNonNull(outcome, "outcome must not be null");
    }
}
