package game.domain;

public record TileCoordinate(
    /** Horizontal tile coordinate within the scenario grid. */
    int x,
    /** Vertical tile coordinate within the scenario grid. */
    int y
) {
    public static TileCoordinate of(int x, int y) {
        return new TileCoordinate(x, y);
    }
}
