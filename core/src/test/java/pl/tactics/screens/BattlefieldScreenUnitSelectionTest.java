package pl.tactics.screens;

import org.junit.jupiter.api.Test;
import pl.tactics.domain.Side;
import pl.tactics.domain.Unit;
import pl.tactics.domain.UnitSize;
import pl.tactics.domain.UnitType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BattlefieldScreenUnitSelectionTest {

    @Test
    void nextSelectedUnitId_returnsFirstUnit_whenCurrentIdIsNull() {
        List<Unit> units = List.of(unit("A"), unit("B"), unit("C"));

        String result = BattlefieldScreen.nextSelectedUnitId(units, null);

        assertEquals("A", result);
    }

    @Test
    void nextSelectedUnitId_advancesToNextUnit() {
        List<Unit> units = List.of(unit("A"), unit("B"), unit("C"));

        String result = BattlefieldScreen.nextSelectedUnitId(units, "A");

        assertEquals("B", result);
    }

    @Test
    void nextSelectedUnitId_wrapsToFirstAfterLast() {
        List<Unit> units = List.of(unit("A"), unit("B"), unit("C"));

        String result = BattlefieldScreen.nextSelectedUnitId(units, "C");

        assertEquals("A", result);
    }

    @Test
    void nextSelectedUnitId_returnsNull_whenListIsEmpty() {
        List<Unit> units = List.of();

        String result = BattlefieldScreen.nextSelectedUnitId(units, null);

        assertNull(result);
    }

    @Test
    void nextSelectedUnitId_returnsFirstUnit_whenCurrentIdNotFound() {
        List<Unit> units = List.of(unit("A"), unit("B"));

        String result = BattlefieldScreen.nextSelectedUnitId(units, "X");

        assertEquals("A", result);
    }

    @Test
    void nextSelectedUnitId_returnsSelf_whenSingleUnit() {
        List<Unit> units = List.of(unit("A"));

        String result = BattlefieldScreen.nextSelectedUnitId(units, "A");

        assertEquals("A", result);
    }

    private static Unit unit(String id) {
        return new Unit(id, Side.ALLIES, UnitType.ARMOR, UnitSize.BATTALION, 0, 0);
    }
}
