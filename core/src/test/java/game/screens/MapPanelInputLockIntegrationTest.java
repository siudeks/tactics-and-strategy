package game.screens;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapPanelInputLockIntegrationTest {

    @Test
    void shouldBlockInputPath_blocksAllMapPathsDuringPhaseNotification() {
        assertTrue(MapPanel.shouldBlockInputPath(InteractionLockState.PHASE_NOTIFICATION, MapPanel.InputPath.CLICK));
        assertTrue(MapPanel.shouldBlockInputPath(InteractionLockState.PHASE_NOTIFICATION, MapPanel.InputPath.DRAG));
        assertTrue(MapPanel.shouldBlockInputPath(InteractionLockState.PHASE_NOTIFICATION, MapPanel.InputPath.ZOOM));
        assertTrue(MapPanel.shouldBlockInputPath(InteractionLockState.PHASE_NOTIFICATION, MapPanel.InputPath.KEY_SHORTCUT));
        assertTrue(MapPanel.shouldBlockInputPath(InteractionLockState.PHASE_NOTIFICATION, MapPanel.InputPath.SELECTION));
    }

    @Test
    void shouldBlockInputPath_allowsOnlyDragAndZoomDuringMovementPlayback() {
        assertTrue(MapPanel.shouldBlockInputPath(InteractionLockState.MOVEMENT_PLAYBACK, MapPanel.InputPath.CLICK));
        assertFalse(MapPanel.shouldBlockInputPath(InteractionLockState.MOVEMENT_PLAYBACK, MapPanel.InputPath.DRAG));
        assertFalse(MapPanel.shouldBlockInputPath(InteractionLockState.MOVEMENT_PLAYBACK, MapPanel.InputPath.ZOOM));
        assertTrue(MapPanel.shouldBlockInputPath(InteractionLockState.MOVEMENT_PLAYBACK, MapPanel.InputPath.KEY_SHORTCUT));
        assertTrue(MapPanel.shouldBlockInputPath(InteractionLockState.MOVEMENT_PLAYBACK, MapPanel.InputPath.SELECTION));
    }

    @Test
    void movementPlaybackPointerDrag_initializesDragStateAndPansCamera() {
        var controller = new CameraController(320f, 320f, 0.5f, 3f, 0.1f);

        var dragInitialized = MapPanel.initializePointerDrag(
            InteractionLockState.MOVEMENT_PLAYBACK,
            controller,
            60f,
            70f
        );
        controller.dragTo(20f, 30f, 80f, 80f);

        assertTrue(dragInitialized);
        assertAll(
            () -> assertEquals(20f, controller.lastDragX()),
            () -> assertEquals(30f, controller.lastDragY()),
            () -> assertEquals(40f, controller.cameraX()),
            () -> assertEquals(40f, controller.cameraY())
        );
    }

    @Test
    void shouldBlockInputPath_releasesAllPathsWhenUnlocked() {
        assertFalse(MapPanel.shouldBlockInputPath(InteractionLockState.NONE, MapPanel.InputPath.CLICK));
        assertFalse(MapPanel.shouldBlockInputPath(InteractionLockState.NONE, MapPanel.InputPath.DRAG));
        assertFalse(MapPanel.shouldBlockInputPath(InteractionLockState.NONE, MapPanel.InputPath.ZOOM));
        assertFalse(MapPanel.shouldBlockInputPath(InteractionLockState.NONE, MapPanel.InputPath.KEY_SHORTCUT));
        assertFalse(MapPanel.shouldBlockInputPath(InteractionLockState.NONE, MapPanel.InputPath.SELECTION));
    }

    @Test
    void interactionLockState_blocksHudActionsAndEndTurnUntilUnlocked() {
        assertTrue(InteractionLockState.TURN_COMMIT.blocksHudActions());
        assertTrue(InteractionLockState.MOVEMENT_PLAYBACK.blocksHudActions());
        assertTrue(InteractionLockState.MOVEMENT_PLAYBACK.blocksEndTurn());
        assertFalse(InteractionLockState.NONE.blocksHudActions());
        assertFalse(InteractionLockState.NONE.blocksEndTurn());
    }
}