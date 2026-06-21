package game;

import game.engine.GameClock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameClockTest {

    @Test
    void initialTime_isDayOneHourZero() {
        var clock = new GameClock();
        assertEquals("Day 1  00:00", clock.formattedTime());
        assertEquals(0L, clock.elapsedHours());
    }

    @Test
    void advance_oneRealSecond_equalsOneInGameHour() {
        var clock = new GameClock();
        clock.advance(1f);
        assertEquals(1L, clock.elapsedHours());
        assertEquals("Day 1  01:00", clock.formattedTime());
    }

    @Test
    void advance_twentyFourRealSeconds_equalsOneInGameDay() {
        var clock = new GameClock();
        clock.advance(24f);
        assertEquals(24L, clock.elapsedHours());
        assertEquals("Day 2  00:00", clock.formattedTime());
    }

    @Test
    void advance_partialHour_doesNotIncrementHourCount() {
        var clock = new GameClock();
        clock.advance(0.5f);
        assertEquals(0L, clock.elapsedHours());
        assertEquals("Day 1  00:00", clock.formattedTime());
    }

    @Test
    void advance_negativeSeconds_isIgnored() {
        var clock = new GameClock();
        clock.advance(-5f);
        assertEquals(0L, clock.elapsedHours());
        assertEquals("Day 1  00:00", clock.formattedTime());
    }

    @Test
    void advance_multipleSmallSteps_accumulatesCorrectly() {
        var clock = new GameClock();
        for (int i = 0; i < 25; i++) {
            clock.advance(0.1f);  // 25 × 0.1s = 2.5s = 2 in-game hours
        }
        assertEquals(2L, clock.elapsedHours());
    }

    @Test
    void formattedTime_zeroHoursPaddedToTwoDigits() {
        var clock = new GameClock();
        clock.advance(5f);
        assertEquals("Day 1  05:00", clock.formattedTime());
    }
}
