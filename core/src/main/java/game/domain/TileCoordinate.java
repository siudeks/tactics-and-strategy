package game.domain;

public record TileCoordinate(int x, int y) {
    public TileCoordinate {
        // Keep canonical constructor for future boundary validation hooks.
    }
}
