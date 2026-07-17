package game.engine;

/**
 * Immutable snapshot of one unit's in-progress real-time movement toward a target tile.
 * Speed is fixed at 1 tile per real-time second, so {@code totalSeconds} equals the
 * Euclidean tile distance from start to target.
 */
record RtsMovementEntry(
    String unitId,
    float fromX,
    float fromY,
    int toX,
    int toY,
    float progressSeconds,
    float totalSeconds
) {

    RtsMovementEntry {
    }

    /** Returns a copy of this entry with {@code progressSeconds} replaced. */
    RtsMovementEntry withProgress(float newProgressSeconds) {
        return new RtsMovementEntry(unitId, fromX, fromY, toX, toY, newProgressSeconds, totalSeconds);
    }

    /** Current interpolated X position in tile space. */
    float currentX() {
        if (totalSeconds == 0f) {
            return toX;
        }
        var t = Math.min(progressSeconds / totalSeconds, 1f);
        return fromX + (toX - fromX) * t;
    }

    /** Current interpolated Y position in tile space. */
    float currentY() {
        if (totalSeconds == 0f) {
            return toY;
        }
        var t = Math.min(progressSeconds / totalSeconds, 1f);
        return fromY + (toY - fromY) * t;
    }

    /** Returns {@code true} when the unit has reached or passed the target. */
    boolean hasArrived() {
        return progressSeconds >= totalSeconds;
    }
}
