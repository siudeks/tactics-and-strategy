package game.domain;

public record Order(
    String id,
    String unitId,
    Side side,
    OrderType type,
    TileCoordinate target
) {

    public static Order of(String id, String unitId, Side side, OrderType type, TileCoordinate target) {
        return new Order(id, unitId, side, type, target);
    }
}
