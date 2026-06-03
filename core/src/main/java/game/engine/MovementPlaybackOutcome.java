package game.engine;

public enum MovementPlaybackOutcome {
    MOVED,
    SKIPPED;

    public boolean moved() {
        return this == MOVED;
    }

    public boolean skipped() {
        return this == SKIPPED;
    }
}