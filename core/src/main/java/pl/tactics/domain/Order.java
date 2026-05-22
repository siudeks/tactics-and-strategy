package pl.tactics.domain;

import java.util.Objects;

public record Order(
    String id,
    String unitId,
    Side side,
    OrderType type,
    int targetX,
    int targetY
) {
    public Order {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(unitId, "unitId must not be null");
        Objects.requireNonNull(side, "side must not be null");
        Objects.requireNonNull(type, "type must not be null");
    }
}
