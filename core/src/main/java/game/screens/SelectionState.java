package game.screens;

import org.jspecify.annotations.Nullable;

/**
 * Finite-state machine for battlefield selection and MOVE-targeting mode.
 */
final class SelectionState {

    private SelectionFsmState state = NoSelectionState.INSTANCE;

    @Nullable
    String selectedUnitId() {
        return state.selectedUnitId();
    }

    boolean isMoveModeActive() {
        return state instanceof MoveTargetingState;
    }

    void clearSelection() {
        state = NoSelectionState.INSTANCE;
    }

    void select(@Nullable String unitId) {
        state = unitId == null ? NoSelectionState.INSTANCE : new MoveTargetingState(unitId);
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
        @Nullable
        String selectedUnitId();
    }

    private static final class NoSelectionState implements SelectionFsmState {
        private static final NoSelectionState INSTANCE = new NoSelectionState();

        private NoSelectionState() {
        }

        @Override
        public @Nullable String selectedUnitId() {
            return null;
        }
    }

    private record UnitSelectedState(String selectedUnitId) implements SelectionFsmState {
    }

    private record MoveTargetingState(String selectedUnitId) implements SelectionFsmState {
    }
}
