package game.domain;

import java.util.Objects;

public record Unit(
    String id,
    Side side,
    UnitType type,
    UnitSize size,
    int tileX,
    int tileY
) {
    public Unit {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(side, "side must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(size, "size must not be null");
    }
}
