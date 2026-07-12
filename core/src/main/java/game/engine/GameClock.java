package game.engine;

/**
 * Tracks in-game time on a real-time scale where one real second equals one in-game hour.
 * The clock starts at day 1, hour 00:00 and advances continuously via {@link #advance(float)}.
 */
public final class GameClock {

    /** Number of real-time seconds that correspond to one in-game hour. */
    static final float REAL_SECONDS_PER_GAME_HOUR = 1f;

    private float elapsedSeconds;
    private boolean paused;

    public GameClock() {
        this.elapsedSeconds = 0f;
        this.paused = false;
    }

    /**
     * Advances the clock by the given real-time delta.
     * Negative or zero values are ignored. Has no effect when the clock is paused.
     *
     * @param deltaSeconds real-time seconds elapsed since the last advance
     */
    public void advance(float deltaSeconds) {
        if (paused) return;
        elapsedSeconds += Math.max(0f, deltaSeconds);
    }

    /** Toggles between paused and running state. */
    public void togglePause() {
        paused = !paused;
    }

    /** Returns {@code true} when the clock is paused and will not advance. */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Returns the total number of whole in-game hours elapsed since the clock was created.
     */
    public long elapsedHours() {
        return (long) (elapsedSeconds / REAL_SECONDS_PER_GAME_HOUR);
    }

    /**
     * Returns the current in-game time formatted as {@code "Day D  HH:00"}.
     * The first in-game day is Day 1, starting at 00:00.
     */
    public String formattedTime() {
        long totalHours = elapsedHours();
        long day = totalHours / 24 + 1;
        long hourOfDay = totalHours % 24;
        return String.format("Day %d  %02d:00", day, hourOfDay);
    }
}
