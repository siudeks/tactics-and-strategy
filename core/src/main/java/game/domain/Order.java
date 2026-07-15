package game.domain;

public record Order(
    String id,
    String unitId,
    Side side,
    OrderType type,
    int targetX,
    int targetY
) {
}
