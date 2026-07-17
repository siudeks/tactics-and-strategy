package game.domain;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnitIdTest {

    @Test
    void of_returnsNone_forNullValue() {
        @Nullable String maybeValue = null;
        var unitId = UnitId.of(maybeValue);

        assertTrue(switch (unitId) {
            case UnitId.None ignored -> true;
            case UnitId.Value value -> false;
        });
        assertEquals(UnitId.none(), unitId);
    }

    @Test
    void of_returnsValue_forNonNullValue() {
        var unitId = UnitId.of("alpha");

        assertEquals("alpha", switch (unitId) {
            case UnitId.None ignored -> null;
            case UnitId.Value value -> value.value();
        });
    }
}
