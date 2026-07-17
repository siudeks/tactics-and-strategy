package game.screens;

import game.domain.UnitId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SelectionStateTest {

    @Test
    void initialState_hasNoSelection_andMoveModeDisabled() {
        var state = new SelectionState();

        assertEquals(UnitId.none(), state.selectedUnitId());
        assertFalse(state.isMoveModeActive());
    }

    @Test
    void selectAndEnterMoveMode_entersMoveModeImmediately_andToggleExitsAndReenters() {
        var state = new SelectionState();
        state.selectAndEnterMoveMode(UnitId.of("u-1"));
        assertTrue(state.isMoveModeActive());
        assertEquals(UnitId.of("u-1"), state.selectedUnitId());

        state.toggleMoveMode();
        assertFalse(state.isMoveModeActive());
        assertEquals(UnitId.of("u-1"), state.selectedUnitId());

        state.toggleMoveMode();
        assertTrue(state.isMoveModeActive());
        assertEquals(UnitId.of("u-1"), state.selectedUnitId());
    }

    @Test
    void toggleMoveMode_withoutSelection_keepsNoSelection() {
        var state = new SelectionState();

        state.toggleMoveMode();

        assertEquals(UnitId.none(), state.selectedUnitId());
        assertFalse(state.isMoveModeActive());
    }

    @Test
    void clearSelection_resetsStateAndDisablesMoveMode() {
        var state = new SelectionState();
        state.selectAndEnterMoveMode(UnitId.of("u-2"));
        state.toggleMoveMode();

        state.clearSelection();

        assertEquals(UnitId.none(), state.selectedUnitId());
        assertFalse(state.isMoveModeActive());
    }

    @Test
    void deactivateMoveMode_keepsSelectedUnit() {
        var state = new SelectionState();
        state.selectAndEnterMoveMode(UnitId.of("u-3"));
        state.toggleMoveMode();

        state.deactivateMoveMode();

        assertEquals(UnitId.of("u-3"), state.selectedUnitId());
        assertFalse(state.isMoveModeActive());
    }
}
