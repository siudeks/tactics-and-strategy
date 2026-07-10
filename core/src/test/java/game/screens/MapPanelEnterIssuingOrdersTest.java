package game.screens;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Note: the ENTER key now calls onEndTurn.run() directly (no completeness check or side-switching
// logic). Because MapPanel is a libGDX Actor requiring a full graphical context, that callback
// path is covered by the headless/integration smoke tests rather than here.
//
// This class retains unit tests for the remaining package-private static helpers that are
// exercisable without a running libGDX context (coverage of NONE / blocked-drag branches not
// duplicated in MapPanelInputLockIntegrationTest).
class MapPanelEnterIssuingOrdersTest {

    @Test
    void shouldBlockInputPath_neverBlocks_whenLockStateIsNone() {
        for (var path : MapPanel.InputPath.values()) {
            assertFalse(
                MapPanel.shouldBlockInputPath(InteractionLockState.NONE, path),
                "Expected NONE to allow " + path
            );
        }
    }

    @Test
    void initializePointerDrag_returnsFalse_andDoesNotMoveCameraOrigin_whenDragIsBlocked() {
        var controller = new CameraController(320f, 320f, 0.5f, 3f, 0.1f);

        // TURN_COMMIT blocks all input paths including DRAG
        boolean result = MapPanel.initializePointerDrag(
            InteractionLockState.TURN_COMMIT,
            controller,
            50f,
            60f
        );

        assertFalse(result, "initializePointerDrag must return false when drag is blocked");
        // Verify startDrag was not called: a subsequent dragTo from origin (0,0) should
        // behave as if lastDrag was never set (remains at default 0,0).
        controller.dragTo(0f, 0f, 80f, 80f); // drag from (0,0) → no camera movement
        assertTrue(controller.cameraX() == 0f && controller.cameraY() == 0f,
            "Camera must remain at origin when drag was never initialised");
    }
}
