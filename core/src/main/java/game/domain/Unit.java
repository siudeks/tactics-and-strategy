package game.domain;

public record Unit(
    String id,
    Side side,
    UnitType type,
    UnitSize size,
    int tileX,
    int tileY
) {
}
