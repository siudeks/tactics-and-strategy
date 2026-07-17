package game.domain;

import org.jspecify.annotations.Nullable;

public sealed interface UnitId permits UnitId.Value, UnitId.None {
    static UnitId of(@Nullable String value) {
        if (value == null) {
            return none();
        }
        return new Value(value);
    }

    static UnitId none() {
        return None.INSTANCE;
    }

    record Value(String value) implements UnitId {
    }

    enum None implements UnitId {
        INSTANCE;

        @Override
        public String toString() {
            return "none";
        }
    }
}
