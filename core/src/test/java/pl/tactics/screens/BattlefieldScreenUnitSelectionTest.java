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

    @Test
    void unitIdAtScreenPoint_returnsId_whenPointInsideBoundsAndSideMatches() {
        var p = new BattlefieldScreen.UnitRenderPlacement(unit("A", Side.ALLIES), 10f, 10f, 20f);
        String result = BattlefieldScreen.unitIdAtScreenPoint(List.of(p), 15f, 15f, Side.ALLIES);
        assertEquals("A", result);
    }

    @Test
    void unitIdAtScreenPoint_returnsNull_whenPointInsideBoundsButWrongSide() {
        var p = new BattlefieldScreen.UnitRenderPlacement(unit("A", Side.AXIS), 10f, 10f, 20f);
        String result = BattlefieldScreen.unitIdAtScreenPoint(List.of(p), 15f, 15f, Side.ALLIES);
        assertNull(result);
    }

    @Test
    void unitIdAtScreenPoint_returnsNull_whenPointOutsideBounds() {
        var p = new BattlefieldScreen.UnitRenderPlacement(unit("A", Side.ALLIES), 10f, 10f, 20f);
        String result = BattlefieldScreen.unitIdAtScreenPoint(List.of(p), 31f, 15f, Side.ALLIES);
        assertNull(result);
    }

    @Test
    void unitIdAtScreenPoint_returnsNull_whenPlacementsEmpty() {
        String result = BattlefieldScreen.unitIdAtScreenPoint(List.of(), 15f, 15f, Side.ALLIES);
        assertNull(result);
    }

    @Test
    void unitIdAtScreenPoint_returnsFirstMatch_whenMultipleOverlap() {
        var p1 = new BattlefieldScreen.UnitRenderPlacement(unit("FIRST", Side.ALLIES), 10f, 10f, 20f);
        var p2 = new BattlefieldScreen.UnitRenderPlacement(unit("SECOND", Side.ALLIES), 10f, 10f, 20f);
        String result = BattlefieldScreen.unitIdAtScreenPoint(List.of(p1, p2), 15f, 15f, Side.ALLIES);
        assertEquals("FIRST", result);
    }

    @Test
    void unitIdAtScreenPoint_returnsNull_whenPointOnExclusiveUpperBound() {
        var p = new BattlefieldScreen.UnitRenderPlacement(unit("A", Side.ALLIES), 10f, 10f, 20f);
        String result = BattlefieldScreen.unitIdAtScreenPoint(List.of(p), 30f, 15f, Side.ALLIES);
        assertNull(result);
    }

    private static Unit unit(String id) {
        return new Unit(id, Side.ALLIES, UnitType.ARMOR, UnitSize.BATTALION, 0, 0);
    }

    private static Unit unit(String id, Side side) {
        return new Unit(id, side, UnitType.ARMOR, UnitSize.BATTALION, 0, 0);
    }
}
