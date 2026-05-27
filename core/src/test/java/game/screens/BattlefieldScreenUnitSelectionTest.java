package game.screens;

import org.junit.jupiter.api.Test;
import game.domain.Side;
import game.domain.Unit;
import game.domain.UnitSize;
import game.domain.UnitType;

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

    @Test
    void visibleUnitType_returnsActualType_forOwnUnit() {
        Unit unit = new Unit("A", Side.ALLIES, UnitType.ARTILLERY, UnitSize.BATTALION, 0, 0);

        UnitType result = BattlefieldScreen.visibleUnitType(unit, Side.ALLIES);

        assertEquals(UnitType.ARTILLERY, result);
    }

    @Test
    void visibleUnitType_returnsNull_forEnemyUnit() {
        Unit unit = new Unit("A", Side.AXIS, UnitType.HQ, UnitSize.BATTALION, 0, 0);

        UnitType result = BattlefieldScreen.visibleUnitType(unit, Side.ALLIES);

        assertNull(result);
    }

    private static Unit unit(String id) {
        return new Unit(id, Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 0, 0);
    }

    private static Unit unit(String id, Side side) {
        return new Unit(id, side, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 0, 0);
    }
}
