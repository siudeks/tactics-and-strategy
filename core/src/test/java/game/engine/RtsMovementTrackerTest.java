package game.engine;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RtsMovementTrackerTest {

    @Test
    void startMovement_createsActiveEntry_visibleInCurrentPositions() {
        var tracker = new RtsMovementTracker();

        tracker.startMovement("u1", 0f, 0f, 4, 0);

        var positions = tracker.currentPositions();
        var pos = positions.get("u1");
        assertNotNull(pos);
        assertEquals(0f, pos[0], 0.0001f, "Should be at start X");
        assertEquals(0f, pos[1], 0.0001f, "Should be at start Y");
    }

    @Test
    void advance_movesUnitAlongPath() {
        var tracker = new RtsMovementTracker();
        // 4-tile horizontal journey → totalSeconds = 4.0
        tracker.startMovement("u1", 0f, 0f, 4, 0);

        tracker.advance(1f); // 1 second → 25% progress

        var pos = tracker.currentPositions().get("u1");
        assertNotNull(pos);
        assertEquals(1f, pos[0], 0.0001f, "Should be 1/4 of the way");
        assertEquals(0f, pos[1], 0.0001f);
    }

    @Test
    void advance_returnsArrivedUnits_andRemovesThemFromTracker() {
        var tracker = new RtsMovementTracker();
        tracker.startMovement("u1", 0f, 0f, 3, 0); // totalSeconds = 3.0

        var arrived = tracker.advance(3f); // exactly at target

        assertAll(
            () -> assertTrue(arrived.containsKey("u1")),
            () -> assertArrayEquals(new int[]{3, 0}, arrived.get("u1")),
            () -> assertTrue(tracker.isEmpty(), "Tracker should be empty after arrival"),
            () -> assertNull(tracker.currentPosition("u1"), "Arrived unit should not appear in positions")
        );
    }

    @Test
    void advance_withExcessTime_stillArrivesAtTarget() {
        var tracker = new RtsMovementTracker();
        tracker.startMovement("u1", 0f, 0f, 2, 0); // totalSeconds = 2.0

        var arrived = tracker.advance(5f); // way more than needed

        assertTrue(arrived.containsKey("u1"));
        assertArrayEquals(new int[]{2, 0}, arrived.get("u1"));
    }

    @Test
    void currentPosition_returnsNull_whenUnitNotMoving() {
        var tracker = new RtsMovementTracker();

        assertNull(tracker.currentPosition("no-such-unit"));
    }

    @Test
    void currentPosition_returnsInterpolatedFloat_duringMovement() {
        var tracker = new RtsMovementTracker();
        tracker.startMovement("u1", 0f, 0f, 4, 3); // diagonal: sqrt(16+9)=5.0

        tracker.advance(2.5f); // 50% progress

        var pos = tracker.currentPosition("u1");
        assertNotNull(pos);
        assertEquals(2f, pos[0], 0.0001f);
        assertEquals(1.5f, pos[1], 0.0001f);
    }

    @Test
    void startMovement_replacesExistingMovement() {
        var tracker = new RtsMovementTracker();
        tracker.startMovement("u1", 0f, 0f, 10, 0);
        tracker.advance(1f); // advance partway

        // Re-assign to a different target
        tracker.startMovement("u1", 1f, 0f, 5, 0);

        var pos = tracker.currentPosition("u1");
        assertNotNull(pos);
        // Progress resets to 0 from new fromX=1
        assertEquals(1f, pos[0], 0.0001f, "Should start from new fromX");
    }

    @Test
    void clear_removesAllMovements() {
        var tracker = new RtsMovementTracker();
        tracker.startMovement("u1", 0f, 0f, 5, 0);
        tracker.startMovement("u2", 0f, 0f, 3, 3);

        tracker.clear();

        assertTrue(tracker.isEmpty());
        assertTrue(tracker.currentPositions().isEmpty());
    }

    @Test
    void advance_emptyTracker_returnsEmptyMap() {
        var tracker = new RtsMovementTracker();

        var arrived = tracker.advance(1f);

        assertTrue(arrived.isEmpty());
    }

    @Test
    void startMovement_sameStartAndTarget_arrivesImmediately() {
        var tracker = new RtsMovementTracker();
        // totalSeconds = 0 (start == target)
        tracker.startMovement("u1", 3f, 3f, 3, 3);

        var arrived = tracker.advance(0f);

        assertAll(
            () -> assertTrue(arrived.containsKey("u1"), "Should arrive immediately when already at target"),
            () -> assertArrayEquals(new int[]{3, 3}, arrived.get("u1"))
        );
    }
}
