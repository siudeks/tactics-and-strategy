package game.domain;

import java.util.Objects;

public record ScenarioId(String value) {
    public ScenarioId {
        Objects.requireNonNull(value, "value must not be null");
    }

    @Override
    public String toString() {
        return value;
    }
}
