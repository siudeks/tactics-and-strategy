package game.screens;

import java.util.Objects;

enum InteractionLockState {
    NONE,
    TURN_COMMIT,
    PHASE_NOTIFICATION,
    MOVEMENT_PLAYBACK,
    PHASE_TRANSITION;

    boolean blocks(MapPanel.InputPath inputPath) {
        Objects.requireNonNull(inputPath, "inputPath must not be null");
        return switch (this) {
            case NONE -> false;
            case MOVEMENT_PLAYBACK -> switch (inputPath) {
                case DRAG, ZOOM -> false;
                case CLICK, KEY_SHORTCUT, SELECTION -> true;
            };
            case TURN_COMMIT, PHASE_NOTIFICATION, PHASE_TRANSITION -> true;
        };
    }

    boolean blocksHudActions() {
        return this != NONE;
    }

    boolean blocksEndTurn() {
        return this != NONE;
    }
}