package game.domain;

import java.util.Objects;

public record UnitId(String value) {
    public UnitId {
        Objects.requireNonNull(value, "value must not be null");
    }

    @Override
    public String toString() {
        return value;
    }
}
