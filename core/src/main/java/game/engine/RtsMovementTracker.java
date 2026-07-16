package game.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.Nullable;

/**
 * Tracks and advances continuous real-time movement of units toward assigned tile targets.
 * <p>
 * Speed is fixed at 1 tile per real-time second.  The Euclidean distance between the
 * start position and the target tile determines the total travel time in seconds.
 * <p>
 * This class is not thread-safe and is designed for single-threaded game-loop use.
 */
final class RtsMovementTracker {

    private final Map<String, RtsMovementEntry> entries = new HashMap<>();

    /**
     * Starts or replaces movement for {@code unitId}.
     * The unit travels from ({@code fromX}, {@code fromY}) to ({@code toX}, {@code toY})
     * at 1 tile per second (Euclidean distance = travel time in seconds).
     */
    void startMovement(String unitId, float fromX, float fromY, int toX, int toY) {
        var dx = toX - fromX;
        var dy = toY - fromY;
        var totalSeconds = (float) Math.sqrt(dx * dx + dy * dy);
        entries.put(unitId, new RtsMovementEntry(unitId, fromX, fromY, toX, toY, 0f, totalSeconds));
    }

    /**
     * Advances all active movements by {@code deltaSeconds}.
     *
     * @return unmodifiable map of unitId → {@code [toX, toY]} for units that reached their
     *         target this frame; those units are removed from the tracker
     */
    Map<String, int[]> advance(float deltaSeconds) {
        var arrived = new HashMap<String, int[]>();
        var iter = entries.entrySet().iterator();
        while (iter.hasNext()) {
            var e = iter.next();
            var movement = e.getValue();
            var newProgress = movement.progressSeconds() + deltaSeconds;
            if (newProgress >= movement.totalSeconds()) {
                arrived.put(movement.unitId(), new int[]{movement.toX(), movement.toY()});
                iter.remove();
            } else {
                e.setValue(movement.withProgress(newProgress));
            }
        }
        return Collections.unmodifiableMap(arrived);
    }

    /**
     * Returns the current interpolated float tile position ({@code [x, y]}) for each
     * actively moving unit.  Units that have arrived are not included (they were removed
     * from the tracker by {@link #advance}).
     */
    Map<String, float[]> currentPositions() {
        var result = new HashMap<String, float[]>(entries.size());
        for (RtsMovementEntry entry : entries.values()) {
            result.put(entry.unitId(), new float[]{entry.currentX(), entry.currentY()});
        }
        return Collections.unmodifiableMap(result);
    }

    /**
     * Returns the current interpolated float position for {@code unitId}, or {@code null}
     * if the unit has no active movement.
     */
    float @Nullable [] currentPosition(String unitId) {
        var entry = entries.get(unitId);
        if (entry == null) {
            return null;
        }
        return new float[]{entry.currentX(), entry.currentY()};
    }

    /**
     * Removes all active movements.  Call this after END_TURN completes so the tracker
     * does not retain stale entries for the next command phase.
     */
    void clear() {
        entries.clear();
    }

    /** Returns {@code true} when no unit is actively moving. */
    boolean isEmpty() {
        return entries.isEmpty();
    }
}
