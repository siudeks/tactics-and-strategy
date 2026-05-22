package pl.tactics.engine;

public final class DeterministicContext {
    private final long seed;

    private DeterministicContext(long seed) {
        this.seed = seed;
    }

    public static DeterministicContext withSeed(long seed) {
        return new DeterministicContext(seed);
    }

    public long seed() {
        return seed;
    }
}
