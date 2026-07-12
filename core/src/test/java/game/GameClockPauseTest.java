package game;

import game.engine.GameClock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameClockPauseTest {

    @Test
    void isPaused_startsAsFalse() {
        var clock = new GameClock();
        assertFalse(clock.isPaused());
    }

    @Test
    void togglePause_pausesRunningClock() {
        var clock = new GameClock();
        clock.togglePause();
        assertTrue(clock.isPaused());
    }

    @Test
    void togglePause_resumesPausedClock() {
        var clock = new GameClock();
        clock.togglePause();
        clock.togglePause();
        assertFalse(clock.isPaused());
    }

    @Test
    void advance_whenPaused_doesNotAdvanceClock() {
        var clock = new GameClock();
        clock.togglePause();
        clock.advance(10f);
        assertEquals(0L, clock.elapsedHours());
        assertEquals("Day 1  00:00", clock.formattedTime());
    }

    @Test
    void advance_afterResume_advancesNormally() {
        var clock = new GameClock();
        clock.togglePause();
        clock.advance(5f);   // paused – should be ignored
        clock.togglePause();
        clock.advance(3f);   // running – should count
        assertEquals(3L, clock.elapsedHours());
    }

    @Test
    void advance_accumulatesOnlyWhileRunning() {
        var clock = new GameClock();
        clock.advance(2f);           // 2 hours
        clock.togglePause();
        clock.advance(100f);         // ignored
        clock.togglePause();
        clock.advance(1f);           // 1 more hour
        assertEquals(3L, clock.elapsedHours());
    }
}
