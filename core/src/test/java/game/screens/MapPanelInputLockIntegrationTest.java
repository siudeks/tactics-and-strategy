package game.screens;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapPanelInputLockIntegrationTest {

    @Test
    void shouldBlockInputPath_returnsTrueForClickDragZoomKeyAndSelection_whenInputIsLocked() {
        assertTrue(MapPanel.shouldBlockInputPath(true, MapPanel.InputPath.CLICK));
        assertTrue(MapPanel.shouldBlockInputPath(true, MapPanel.InputPath.DRAG));
        assertTrue(MapPanel.shouldBlockInputPath(true, MapPanel.InputPath.ZOOM));
        assertTrue(MapPanel.shouldBlockInputPath(true, MapPanel.InputPath.KEY_SHORTCUT));
        assertTrue(MapPanel.shouldBlockInputPath(true, MapPanel.InputPath.SELECTION));
    }

    @Test
    void shouldBlockInputPath_returnsFalseForClickDragZoomKeyAndSelection_whenInputIsUnlocked() {
        assertFalse(MapPanel.shouldBlockInputPath(false, MapPanel.InputPath.CLICK));
        assertFalse(MapPanel.shouldBlockInputPath(false, MapPanel.InputPath.DRAG));
        assertFalse(MapPanel.shouldBlockInputPath(false, MapPanel.InputPath.ZOOM));
        assertFalse(MapPanel.shouldBlockInputPath(false, MapPanel.InputPath.KEY_SHORTCUT));
        assertFalse(MapPanel.shouldBlockInputPath(false, MapPanel.InputPath.SELECTION));
    }

    @Test
    void shouldBlockInputPath_unlockLifecycle_releasesAllInputPathsAfterOverlayLockEnds() {
        boolean clickBlockedDuringOverlay = MapPanel.shouldBlockInputPath(true, MapPanel.InputPath.CLICK);
        boolean clickBlockedAfterOverlay = MapPanel.shouldBlockInputPath(false, MapPanel.InputPath.CLICK);
        boolean dragBlockedDuringOverlay = MapPanel.shouldBlockInputPath(true, MapPanel.InputPath.DRAG);
        boolean dragBlockedAfterOverlay = MapPanel.shouldBlockInputPath(false, MapPanel.InputPath.DRAG);
        boolean zoomBlockedDuringOverlay = MapPanel.shouldBlockInputPath(true, MapPanel.InputPath.ZOOM);
        boolean zoomBlockedAfterOverlay = MapPanel.shouldBlockInputPath(false, MapPanel.InputPath.ZOOM);
        boolean keyBlockedDuringOverlay = MapPanel.shouldBlockInputPath(true, MapPanel.InputPath.KEY_SHORTCUT);
        boolean keyBlockedAfterOverlay = MapPanel.shouldBlockInputPath(false, MapPanel.InputPath.KEY_SHORTCUT);
        boolean selectionBlockedDuringOverlay = MapPanel.shouldBlockInputPath(true, MapPanel.InputPath.SELECTION);
        boolean selectionBlockedAfterOverlay = MapPanel.shouldBlockInputPath(false, MapPanel.InputPath.SELECTION);

        assertTrue(clickBlockedDuringOverlay);
        assertFalse(clickBlockedAfterOverlay);
        assertTrue(dragBlockedDuringOverlay);
        assertFalse(dragBlockedAfterOverlay);
        assertTrue(zoomBlockedDuringOverlay);
        assertFalse(zoomBlockedAfterOverlay);
        assertTrue(keyBlockedDuringOverlay);
        assertFalse(keyBlockedAfterOverlay);
        assertTrue(selectionBlockedDuringOverlay);
        assertFalse(selectionBlockedAfterOverlay);
    }
}