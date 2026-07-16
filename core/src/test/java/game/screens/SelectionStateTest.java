package game.screens;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SelectionStateTest {

    @Test
    void initialState_hasNoSelection_andMoveModeDisabled() {
        var state = new SelectionState();

        assertNull(state.selectedUnitId());
        assertFalse(state.isMoveModeActive());
    }

    @Test
    void selectAndEnterMoveMode_entersMoveModeImmediately_andToggleExitsAndReenters() {
        var state = new SelectionState();
        state.selectAndEnterMoveMode("u-1");
        assertTrue(state.isMoveModeActive());
        assertEquals("u-1", state.selectedUnitId());

        state.toggleMoveMode();
        assertFalse(state.isMoveModeActive());
        assertEquals("u-1", state.selectedUnitId());

        state.toggleMoveMode();
        assertTrue(state.isMoveModeActive());
        assertEquals("u-1", state.selectedUnitId());
    }

    @Test
    void toggleMoveMode_withoutSelection_keepsNoSelection() {
        var state = new SelectionState();

        state.toggleMoveMode();

        assertNull(state.selectedUnitId());
        assertFalse(state.isMoveModeActive());
    }

    @Test
    void clearSelection_resetsStateAndDisablesMoveMode() {
        var state = new SelectionState();
        state.selectAndEnterMoveMode("u-2");
        state.toggleMoveMode();

        state.clearSelection();

        assertNull(state.selectedUnitId());
        assertFalse(state.isMoveModeActive());
    }

    @Test
    void deactivateMoveMode_keepsSelectedUnit() {
        var state = new SelectionState();
        state.selectAndEnterMoveMode("u-3");
        state.toggleMoveMode();

        state.deactivateMoveMode();

        assertEquals("u-3", state.selectedUnitId());
        assertFalse(state.isMoveModeActive());
    }
}
