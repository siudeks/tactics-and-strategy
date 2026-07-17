package game.screens;

import game.domain.UnitId;

/**
 * Finite-state machine for battlefield selection and MOVE-targeting mode.
 */
final class SelectionState {

    private SelectionFsmState state = NoSelectionState.INSTANCE;

    UnitId selectedUnitId() {
        return state.selectedUnitId();
    }

    boolean isMoveModeActive() {
        return state instanceof MoveTargetingState;
    }

    void clearSelection() {
        state = NoSelectionState.INSTANCE;
    }

    /**
     * Selects the given unit and transitions directly into {@link MoveTargetingState}
     * (or back to {@link NoSelectionState} when {@code unitId} is {@code null}), so
     * MOVE targeting is active immediately after selection.
     */
    void selectAndEnterMoveMode(UnitId unitId) {
        state = switch (unitId) {
            case UnitId.None ignored -> NoSelectionState.INSTANCE;
            case UnitId.Value selected -> new MoveTargetingState(selected);
        };
    }

    void toggleMoveMode() {
        state = switch (state) {
            case NoSelectionState ignored -> state;
            case UnitSelectedState selected -> new MoveTargetingState(selected.selectedUnitId());
            case MoveTargetingState targeting -> new UnitSelectedState(targeting.selectedUnitId());
        };
    }

    void deactivateMoveMode() {
        if (state instanceof MoveTargetingState targeting) {
            state = new UnitSelectedState(targeting.selectedUnitId());
        }
    }

    private sealed interface SelectionFsmState permits NoSelectionState, UnitSelectedState, MoveTargetingState {
        UnitId selectedUnitId();
    }

    private static final class NoSelectionState implements SelectionFsmState {
        private static final NoSelectionState INSTANCE = new NoSelectionState();

        private NoSelectionState() {
        }

        @Override
        public UnitId selectedUnitId() {
            return UnitId.none();
        }
    }

    private record UnitSelectedState(UnitId selectedUnitId) implements SelectionFsmState {
    }

    private record MoveTargetingState(UnitId selectedUnitId) implements SelectionFsmState {
    }
}
