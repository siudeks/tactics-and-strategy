package game.screens;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CameraControllerTest {

    @Test
    void dragTo_updatesCameraAndClampsToBounds() {
        CameraController controller = new CameraController(160f, 160f, 0.5f, 3f, 0.1f);
        controller.startDrag(10f, 10f);

        controller.dragTo(-20f, -30f, 80f, 80f);

        assertEquals(30f, controller.cameraX());
        assertEquals(40f, controller.cameraY());
    }

    @Test
    void zoomAt_clampsZoomAndReturnsTrue() {
        CameraController controller = new CameraController(512f, 512f, 0.5f, 3f, 0.1f);

        boolean changed = controller.zoomAt(100f, 100f, 1f, 256f, 256f);

        assertTrue(changed);
        assertEquals(1f / 1.1f, controller.zoomLevel(), 0.0001f);
    }

    @Test
    void centerOn_placesCameraNearRequestedCenterWithinBounds() {
        CameraController controller = new CameraController(300f, 300f, 0.5f, 3f, 0.1f);

        controller.centerOn(124f, 90f, 100f, 80f);

        assertEquals(74f, controller.cameraX(), 0.0001f);
        assertEquals(50f, controller.cameraY(), 0.0001f);
    }
}
