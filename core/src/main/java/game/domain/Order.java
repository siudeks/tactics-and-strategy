package game.domain;

import java.util.UUID;

public record Order(
    UUID id,
    UnitId unitId,
    Side side,
    OrderType type,
    TileCoordinate target
) {

    public static Order of(UUID id, String unitId, Side side, OrderType type, TileCoordinate target) {
        return of(id, UnitId.of(unitId), side, type, target);
    }

    public static Order of(UUID id, UnitId unitId, Side side, OrderType type, TileCoordinate target) {
        return new Order(id, unitId, side, type, target);
    }

    public static Order of(UnitId unitId, Side side, OrderType type, TileCoordinate target) {
        return new Order(UUID.randomUUID(), unitId, side, type, target);
    }
}
