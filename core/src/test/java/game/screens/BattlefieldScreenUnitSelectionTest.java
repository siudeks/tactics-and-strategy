package game.screens;

import com.badlogic.gdx.audio.Sound;
import game.domain.Side;
import game.domain.Unit;
import game.domain.UnitId;
import game.domain.UnitSize;
import game.domain.UnitType;
import game.terrain.GeneratedTerrainData;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattlefieldScreenUnitSelectionTest {

    @Test
    void nextSelectedUnitId_returnsFirstUnit_whenCurrentIdIsNull() {
        var units = List.of(unit("A"), unit("B"), unit("C"));

        var result = BattlefieldScreen.nextSelectedUnitId(units, UnitId.none());

        assertEquals(UnitId.of("A"), result);
    }

    @Test
    void nextSelectedUnitId_advancesToNextUnit() {
        var units = List.of(unit("A"), unit("B"), unit("C"));

        var result = BattlefieldScreen.nextSelectedUnitId(units, UnitId.of("A"));

        assertEquals(UnitId.of("B"), result);
    }

    @Test
    void nextSelectedUnitId_wrapsToFirstAfterLast() {
        var units = List.of(unit("A"), unit("B"), unit("C"));

        var result = BattlefieldScreen.nextSelectedUnitId(units, UnitId.of("C"));

        assertEquals(UnitId.of("A"), result);
    }

    @Test
    void nextSelectedUnitId_returnsNull_whenListIsEmpty() {
        List<Unit> units = List.of();

        var result = BattlefieldScreen.nextSelectedUnitId(units, UnitId.none());

        assertEquals(UnitId.none(), result);
    }

    @Test
    void nextSelectedUnitId_returnsFirstUnit_whenCurrentIdNotFound() {
        var units = List.of(unit("A"), unit("B"));

        var result = BattlefieldScreen.nextSelectedUnitId(units, UnitId.of("X"));

        assertEquals(UnitId.of("A"), result);
    }

    @Test
    void nextSelectedUnitId_returnsSelf_whenSingleUnit() {
        var units = List.of(unit("A"));

        var result = BattlefieldScreen.nextSelectedUnitId(units, UnitId.of("A"));

        assertEquals(UnitId.of("A"), result);
    }

    @Test
    void nextUnassignedUnitId_returnsNextUnitWithoutTarget() {
        var units = List.of(unit("A"), unit("B"), unit("C"));

        var result = BattlefieldScreen.nextUnassignedUnitId(
            units,
            UnitId.of("A"),
            Map.of("A", new BattlefieldScreen.TileCoord(1, 1), "B", new BattlefieldScreen.TileCoord(2, 2))
        );

        assertEquals(UnitId.of("C"), result);
    }

    @Test
    void nextUnassignedUnitId_wrapsAroundAndSkipsAssignedUnits() {
        var units = List.of(unit("A"), unit("B"), unit("C"));

        var result = BattlefieldScreen.nextUnassignedUnitId(
            units,
            UnitId.of("C"),
            Map.of("C", new BattlefieldScreen.TileCoord(3, 3))
        );

        assertEquals(UnitId.of("A"), result);
    }

    @Test
    void nextUnassignedUnitId_returnsNull_whenAllUnitsAlreadyAssigned() {
        var units = List.of(unit("A"), unit("B"));

        var result = BattlefieldScreen.nextUnassignedUnitId(
            units,
            UnitId.of("A"),
            Map.of("A", new BattlefieldScreen.TileCoord(1, 1), "B", new BattlefieldScreen.TileCoord(2, 2))
        );

        assertEquals(UnitId.none(), result);
    }

    @Test
    void nextUnassignedUnitId_returnsFirstUnassigned_whenCurrentIdIsNull() {
        var units = List.of(unit("A"), unit("B"));

        var result = BattlefieldScreen.nextUnassignedUnitId(
            units,
            UnitId.none(),
            Map.of("A", new BattlefieldScreen.TileCoord(1, 1))
        );

        assertEquals(UnitId.of("B"), result);
    }

    @Test
    void unitIdAtScreenPoint_returnsId_whenPointInsideBoundsAndSideMatches() {
        var p = new BattlefieldScreen.UnitRenderPlacement(unit("A", Side.ALLIES), 10f, 10f, 20f);
        var result = BattlefieldScreen.unitIdAtScreenPoint(List.of(p), 15f, 15f, Side.ALLIES);
        assertEquals(UnitId.of("A"), result);
    }

    @Test
    void unitIdAtScreenPoint_returnsNull_whenPointInsideBoundsButWrongSide() {
        var p = new BattlefieldScreen.UnitRenderPlacement(unit("A", Side.AXIS), 10f, 10f, 20f);
        var result = BattlefieldScreen.unitIdAtScreenPoint(List.of(p), 15f, 15f, Side.ALLIES);
        assertEquals(UnitId.none(), result);
    }

    @Test
    void unitIdAtScreenPoint_returnsNone_whenPointOutsideBounds() {
        var p = new BattlefieldScreen.UnitRenderPlacement(unit("A", Side.ALLIES), 10f, 10f, 20f);
        var result = BattlefieldScreen.unitIdAtScreenPoint(List.of(p), 31f, 15f, Side.ALLIES);
        assertEquals(UnitId.none(), result);
    }

    @Test
    void unitIdAtScreenPoint_returnsNone_whenPlacementsEmpty() {
        var result = BattlefieldScreen.unitIdAtScreenPoint(List.of(), 15f, 15f, Side.ALLIES);
        assertEquals(UnitId.none(), result);
    }

    @Test
    void unitIdAtScreenPoint_returnsFirstMatch_whenMultipleOverlap() {
        var p1 = new BattlefieldScreen.UnitRenderPlacement(unit("FIRST", Side.ALLIES), 10f, 10f, 20f);
        var p2 = new BattlefieldScreen.UnitRenderPlacement(unit("SECOND", Side.ALLIES), 10f, 10f, 20f);
        var result = BattlefieldScreen.unitIdAtScreenPoint(List.of(p1, p2), 15f, 15f, Side.ALLIES);
        assertEquals(UnitId.of("FIRST"), result);
    }

    @Test
    void unitIdAtScreenPoint_returnsNull_whenPointOnExclusiveUpperBound() {
        var p = new BattlefieldScreen.UnitRenderPlacement(unit("A", Side.ALLIES), 10f, 10f, 20f);
        var result = BattlefieldScreen.unitIdAtScreenPoint(List.of(p), 30f, 15f, Side.ALLIES);
        assertEquals(UnitId.none(), result);
    }

    @Test
    void visibleUnitType_returnsActualType_forOwnUnit() {
        var unit = new Unit("A", Side.ALLIES, UnitType.ARTILLERY, UnitSize.BATTALION, 0, 0);

        var result = BattlefieldScreen.visibleUnitType(unit, Side.ALLIES);

        assertEquals(UnitType.ARTILLERY, result);
    }

    @Test
    void visibleUnitType_returnsNull_forEnemyUnit() {
        var unit = new Unit("A", Side.AXIS, UnitType.HQ, UnitSize.BATTALION, 0, 0);

        var result = BattlefieldScreen.visibleUnitType(unit, Side.ALLIES);

        assertNull(result);
    }

    @Test
    void mapTileAtPanelPoint_returnsTileCoordinates_forClickInsideMap() {
        var tile = BattlefieldScreen.mapTileAtPanelPoint(
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
        var tile = BattlefieldScreen.mapTileAtPanelPoint(
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
        var assignment = BattlefieldScreen.moveTargetAssignmentForClick(
            true,
            UnitId.of("A"),
            new BattlefieldScreen.TileCoord(3, 4),
            new BattlefieldScreen.TileCoord(3, 4),
            true
        );

        assertEquals(new BattlefieldScreen.MoveTargetAssignment(UnitId.of("A"), new BattlefieldScreen.TileCoord(3, 4)), assignment);
    }

    @Test
    void moveTargetAssignmentForClick_returnsNull_whenMoveModeIsDisabled() {
        var assignment = BattlefieldScreen.moveTargetAssignmentForClick(
            false,
            UnitId.of("A"),
            new BattlefieldScreen.TileCoord(3, 4),
            new BattlefieldScreen.TileCoord(3, 4),
            true
        );

        assertNull(assignment);
    }

    @Test
    void moveTargetAssignmentForClick_returnsNull_whenSelectionMissing() {
        var assignment = BattlefieldScreen.moveTargetAssignmentForClick(
            true,
            UnitId.none(),
            new BattlefieldScreen.TileCoord(3, 4),
            new BattlefieldScreen.TileCoord(3, 4),
            true
        );

        assertNull(assignment);
    }

    @Test
    void moveTargetAssignmentForClick_returnsNull_whenClickedTileIsImpassable() {
        var assignment = BattlefieldScreen.moveTargetAssignmentForClick(
            true,
            UnitId.of("A"),
            new BattlefieldScreen.TileCoord(3, 4),
            new BattlefieldScreen.TileCoord(3, 4),
            false
        );

        assertNull(assignment);
    }

    @Test
    void moveTargetAssignmentForClick_returnsNull_whenPreviewIsMissing() {
        var assignment = BattlefieldScreen.moveTargetAssignmentForClick(
            true,
            UnitId.of("A"),
            null,
            new BattlefieldScreen.TileCoord(3, 4),
            true
        );

        assertNull(assignment);
    }

    @Test
    void moveTargetAssignmentForClick_returnsNull_whenClickedTileDoesNotMatchPreview() {
        var assignment = BattlefieldScreen.moveTargetAssignmentForClick(
            true,
            UnitId.of("A"),
            new BattlefieldScreen.TileCoord(3, 4),
            new BattlefieldScreen.TileCoord(4, 4),
            true
        );

        assertNull(assignment);
    }

    @Test
    void shouldPlayMoveConfirmationSound_returnsTrue_whenAssignmentExists() {
        var result = BattlefieldScreen.shouldPlayMoveConfirmationSound(
            new BattlefieldScreen.MoveTargetAssignment(UnitId.of("A"), new BattlefieldScreen.TileCoord(3, 4))
        );

        assertTrue(result);
    }

    @Test
    void shouldPlayMoveConfirmationSound_returnsFalse_whenAssignmentMissing() {
        var result = BattlefieldScreen.shouldPlayMoveConfirmationSound(null);

        assertFalse(result);
    }

    @Test
    void createMoveConfirmationSound_returnsFactorySound_whenFactorySucceeds() {
        var sound = new RecordingSound();

        Sound result = BattlefieldScreen.createMoveConfirmationSound(() -> sound);

        assertEquals(sound, result);
    }

    @Test
    void createMoveConfirmationSound_returnsNull_whenFactoryThrows() {
        var result = BattlefieldScreen.createMoveConfirmationSound(
            () -> {
                throw new IllegalStateException("boom");
            }
        );

        assertNull(result);
    }

    @Test
    void playMoveConfirmationSound_playsSoundWithConfiguredVolume() {
        var sound = new RecordingSound();

        BattlefieldScreen.playMoveConfirmationSound(sound);

        assertEquals(1, sound.stopCalls.get());
        assertEquals(1, sound.playCalls.get());
        assertTrue(sound.lastVolume > 0f);
    }

    @Test
    void playMoveConfirmationSound_doesNotThrow_whenSoundThrows() {
        assertDoesNotThrow(() -> BattlefieldScreen.playMoveConfirmationSound(new ThrowingSound()));
    }

    @Test
    void moveConfirmSoundWavBytes_containsWaveHeader() {
        var wavBytes = BattlefieldScreen.moveConfirmSoundWavBytes(22050);

        assertNotNull(wavBytes);
        assertTrue(wavBytes.length > 44);
        assertEquals('R', wavBytes[0]);
        assertEquals('I', wavBytes[1]);
        assertEquals('F', wavBytes[2]);
        assertEquals('F', wavBytes[3]);
        assertEquals('W', wavBytes[8]);
        assertEquals('A', wavBytes[9]);
        assertEquals('V', wavBytes[10]);
        assertEquals('E', wavBytes[11]);
    }

    @Test
    void movePreviewTile_returnsHoveredTile_whenMoveModeSelectionAndTerrainAreValid() {
        var previewTile = BattlefieldScreen.movePreviewTile(
            true,
            UnitId.of("A"),
            new BattlefieldScreen.TileCoord(7, 8),
            true
        );

        assertEquals(new BattlefieldScreen.TileCoord(7, 8), previewTile);
    }

    @Test
    void movePreviewTile_returnsNull_whenHoveredTileIsImpassable() {
        var previewTile = BattlefieldScreen.movePreviewTile(
            true,
            UnitId.of("A"),
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
        var consume = BattlefieldScreen.shouldConsumeClickInMoveMode(true);

        assertTrue(consume);
    }

    @Test
    void shouldConsumeClickInMoveMode_returnsFalse_whenMoveModeIsDisabled() {
        var consume = BattlefieldScreen.shouldConsumeClickInMoveMode(false);

        assertFalse(consume);
    }

    private static Unit unit(String id) {
        return new Unit(id, Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 0, 0);
    }

    private static Unit unit(String id, Side side) {
        return new Unit(id, side, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 0, 0);
    }

    private static class RecordingSound implements Sound {
        private final AtomicInteger playCalls = new AtomicInteger(0);
        private final AtomicInteger stopCalls = new AtomicInteger(0);
        private float lastVolume;

        @Override
        public long play() {
            playCalls.incrementAndGet();
            return 1L;
        }

        @Override
        public long play(float volume) {
            playCalls.incrementAndGet();
            lastVolume = volume;
            return 1L;
        }

        @Override
        public long play(float volume, float pitch, float pan) {
            playCalls.incrementAndGet();
            lastVolume = volume;
            return 1L;
        }

        @Override
        public long loop() {
            return 1L;
        }

        @Override
        public long loop(float volume) {
            return 1L;
        }

        @Override
        public long loop(float volume, float pitch, float pan) {
            return 1L;
        }

        @Override
        public void dispose() {
        }

        @Override
        public void stop() {
            stopCalls.incrementAndGet();
        }

        @Override
        public void pause() {
        }

        @Override
        public void resume() {
        }

        @Override
        public void stop(long soundId) {
        }

        @Override
        public void pause(long soundId) {
        }

        @Override
        public void resume(long soundId) {
        }

        @Override
        public void setLooping(long soundId, boolean looping) {
        }

        @Override
        public void setPitch(long soundId, float pitch) {
        }

        @Override
        public void setVolume(long soundId, float volume) {
            lastVolume = volume;
        }

        @Override
        public void setPan(long soundId, float pan, float volume) {
            lastVolume = volume;
        }
    }

    private static final class ThrowingSound extends RecordingSound {
        @Override
        public long play(float volume) {
            throw new RuntimeException("boom");
        }
    }
}
