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

    public static Order of(String id, String unitId, Side side, OrderType type, int targetX, int targetY) {
        return of(id, unitId, side, type, new TileCoordinate(targetX, targetY));
    }
}
