package game.screens;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SelectionStateTest {

    @Test
    void initialState_hasNoSelection_andMoveModeDisabled() {
        SelectionState state = new SelectionState();

        assertNull(state.selectedUnitId());
        assertFalse(state.isMoveModeActive());
    }

    @Test
    void select_thenToggleMoveMode_entersAndExitsTargeting() {
        SelectionState state = new SelectionState();
        state.select("u-1");

        state.toggleMoveMode();
        assertTrue(state.isMoveModeActive());
        assertEquals("u-1", state.selectedUnitId());

        state.toggleMoveMode();
        assertFalse(state.isMoveModeActive());
        assertEquals("u-1", state.selectedUnitId());
    }

    @Test
    void toggleMoveMode_withoutSelection_keepsNoSelection() {
        SelectionState state = new SelectionState();

        state.toggleMoveMode();

        assertNull(state.selectedUnitId());
        assertFalse(state.isMoveModeActive());
    }

    @Test
    void clearSelection_resetsStateAndDisablesMoveMode() {
        SelectionState state = new SelectionState();
        state.select("u-2");
        state.toggleMoveMode();

        state.clearSelection();

        assertNull(state.selectedUnitId());
        assertFalse(state.isMoveModeActive());
    }

    @Test
    void deactivateMoveMode_keepsSelectedUnit() {
        SelectionState state = new SelectionState();
        state.select("u-3");
        state.toggleMoveMode();

        state.deactivateMoveMode();

        assertEquals("u-3", state.selectedUnitId());
        assertFalse(state.isMoveModeActive());
    }
}
