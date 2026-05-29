package game.screens;

import org.junit.jupiter.api.Test;
import game.domain.Side;
import game.domain.Unit;
import game.domain.UnitSize;
import game.domain.UnitType;
import game.terrain.GeneratedTerrainData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void mapTileAtPanelPoint_returnsTileCoordinates_forClickInsideMap() {
        BattlefieldScreen.TileCoord tile = BattlefieldScreen.mapTileAtPanelPoint(
            32f,
            16f,
            0f,
            0f,
            1f,
            10,
            10
        );

        assertEquals(new BattlefieldScreen.TileCoord(2, 8), tile);
    }

    @Test
    void mapTileAtPanelPoint_returnsNull_forClickOutsideMap() {
        BattlefieldScreen.TileCoord tile = BattlefieldScreen.mapTileAtPanelPoint(
            -1f,
            16f,
            0f,
            0f,
            1f,
            10,
            10
        );

        assertNull(tile);
    }

    @Test
    void moveTargetAssignmentForClick_returnsAssignment_whenMoveModeAndSelectionArePresent() {
        BattlefieldScreen.MoveTargetAssignment assignment = BattlefieldScreen.moveTargetAssignmentForClick(
            true,
            "A",
            new BattlefieldScreen.TileCoord(3, 4),
            new BattlefieldScreen.TileCoord(3, 4),
            true
        );

        assertEquals(new BattlefieldScreen.MoveTargetAssignment("A", new BattlefieldScreen.TileCoord(3, 4)), assignment);
    }

    @Test
    void moveTargetAssignmentForClick_returnsNull_whenMoveModeIsDisabled() {
        BattlefieldScreen.MoveTargetAssignment assignment = BattlefieldScreen.moveTargetAssignmentForClick(
            false,
            "A",
            new BattlefieldScreen.TileCoord(3, 4),
            new BattlefieldScreen.TileCoord(3, 4),
            true
        );

        assertNull(assignment);
    }

    @Test
    void moveTargetAssignmentForClick_returnsNull_whenSelectionMissing() {
        BattlefieldScreen.MoveTargetAssignment assignment = BattlefieldScreen.moveTargetAssignmentForClick(
            true,
            null,
            new BattlefieldScreen.TileCoord(3, 4),
            new BattlefieldScreen.TileCoord(3, 4),
            true
        );

        assertNull(assignment);
    }

    @Test
    void moveTargetAssignmentForClick_returnsNull_whenClickedTileIsImpassable() {
        BattlefieldScreen.MoveTargetAssignment assignment = BattlefieldScreen.moveTargetAssignmentForClick(
            true,
            "A",
            new BattlefieldScreen.TileCoord(3, 4),
            new BattlefieldScreen.TileCoord(3, 4),
            false
        );

        assertNull(assignment);
    }

    @Test
    void moveTargetAssignmentForClick_returnsNull_whenPreviewIsMissing() {
        BattlefieldScreen.MoveTargetAssignment assignment = BattlefieldScreen.moveTargetAssignmentForClick(
            true,
            "A",
            null,
            new BattlefieldScreen.TileCoord(3, 4),
            true
        );

        assertNull(assignment);
    }

    @Test
    void moveTargetAssignmentForClick_returnsNull_whenClickedTileDoesNotMatchPreview() {
        BattlefieldScreen.MoveTargetAssignment assignment = BattlefieldScreen.moveTargetAssignmentForClick(
            true,
            "A",
            new BattlefieldScreen.TileCoord(3, 4),
            new BattlefieldScreen.TileCoord(4, 4),
            true
        );

        assertNull(assignment);
    }

    @Test
    void movePreviewTile_returnsHoveredTile_whenMoveModeSelectionAndTerrainAreValid() {
        BattlefieldScreen.TileCoord previewTile = BattlefieldScreen.movePreviewTile(
            true,
            "A",
            new BattlefieldScreen.TileCoord(7, 8),
            true
        );

        assertEquals(new BattlefieldScreen.TileCoord(7, 8), previewTile);
    }

    @Test
    void movePreviewTile_returnsNull_whenHoveredTileIsImpassable() {
        BattlefieldScreen.TileCoord previewTile = BattlefieldScreen.movePreviewTile(
            true,
            "A",
            new BattlefieldScreen.TileCoord(7, 8),
            false
        );

        assertNull(previewTile);
    }

    @Test
    void isPassableTerrainCode_returnsFalse_forVoidAndWater() {
        assertFalse(BattlefieldScreen.isPassableTerrainCode(GeneratedTerrainData.TERRAIN_VOID));
        assertFalse(BattlefieldScreen.isPassableTerrainCode(GeneratedTerrainData.TERRAIN_WATER));
    }

    @Test
    void isPassableTerrainCode_returnsTrue_forSandAndMountain() {
        assertTrue(BattlefieldScreen.isPassableTerrainCode(GeneratedTerrainData.TERRAIN_SAND));
        assertTrue(BattlefieldScreen.isPassableTerrainCode(GeneratedTerrainData.TERRAIN_MOUNTAIN));
    }

    @Test
    void shouldConsumeClickInMoveMode_returnsTrue_whenMoveModeIsActive() {
        boolean consume = BattlefieldScreen.shouldConsumeClickInMoveMode(true);

        assertTrue(consume);
    }

    @Test
    void shouldConsumeClickInMoveMode_returnsFalse_whenMoveModeIsDisabled() {
        boolean consume = BattlefieldScreen.shouldConsumeClickInMoveMode(false);

        assertFalse(consume);
    }

    private static Unit unit(String id) {
        return new Unit(id, Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 0, 0);
    }

    private static Unit unit(String id, Side side) {
        return new Unit(id, side, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 0, 0);
    }
}
