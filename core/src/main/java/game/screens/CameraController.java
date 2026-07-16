package game.screens;

import com.badlogic.gdx.math.MathUtils;

final class CameraController {

    private final float mapWorldWidth;
    private final float mapWorldHeight;
    private final float minZoomLevel;
    private final float maxZoomLevel;
    private final float zoomStepPercent;

    private float cameraX;
    private float cameraY;
    private float zoomLevel;
    private float lastDragX;
    private float lastDragY;

    CameraController(float mapWorldWidth,
                     float mapWorldHeight,
                     float minZoomLevel,
                     float maxZoomLevel,
                     float zoomStepPercent) {
        this.mapWorldWidth = mapWorldWidth;
        this.mapWorldHeight = mapWorldHeight;
        this.minZoomLevel = minZoomLevel;
        this.maxZoomLevel = maxZoomLevel;
        this.zoomStepPercent = zoomStepPercent;
        this.zoomLevel = 1f;
    }

    void startDrag(float x, float y) {
        lastDragX = x;
        lastDragY = y;
    }

    void dragTo(float x, float y, float viewportWidth, float viewportHeight) {
        var dx = x - lastDragX;
        var dy = y - lastDragY;
        cameraX -= dx;
        cameraY -= dy;
        clampToViewport(viewportWidth, viewportHeight);

        lastDragX = x;
        lastDragY = y;
    }

    boolean zoomAt(float pointerX, float pointerY, float amountY, float viewportWidth, float viewportHeight) {
        var oldZoomLevel = zoomLevel;
        var factor = BattlefieldScreen.zoomStepFactor(amountY, zoomStepPercent);
        var newZoomLevel = BattlefieldScreen.clampZoomLevel(oldZoomLevel * factor, minZoomLevel, maxZoomLevel);
        if (newZoomLevel == oldZoomLevel) {
            return true;
        }

        cameraX = BattlefieldScreen.cameraAfterZoom(cameraX, pointerX, oldZoomLevel, newZoomLevel);
        cameraY = BattlefieldScreen.cameraAfterZoom(cameraY, pointerY, oldZoomLevel, newZoomLevel);
        zoomLevel = newZoomLevel;
        clampToViewport(viewportWidth, viewportHeight);
        return true;
    }

    void centerOn(float worldCenterX, float worldCenterY, float viewportWidth, float viewportHeight) {
        cameraX = BattlefieldScreen.centeredCameraPosition(worldCenterX, viewportWidth, zoomLevel, mapWorldWidth);
        cameraY = BattlefieldScreen.centeredCameraPosition(worldCenterY, viewportHeight, zoomLevel, mapWorldHeight);
        clampToViewport(viewportWidth, viewportHeight);
    }

    void clampToViewport(float viewportWidth, float viewportHeight) {
        var visibleWorldWidth = viewportWidth / zoomLevel;
        var visibleWorldHeight = viewportHeight / zoomLevel;
        var maxCameraX = Math.max(0f, mapWorldWidth - visibleWorldWidth);
        var maxCameraY = Math.max(0f, mapWorldHeight - visibleWorldHeight);
        cameraX = MathUtils.clamp(cameraX, 0f, maxCameraX);
        cameraY = MathUtils.clamp(cameraY, 0f, maxCameraY);
    }

    float cameraX() {
        return cameraX;
    }

    float cameraY() {
        return cameraY;
    }

    float zoomLevel() {
        return zoomLevel;
    }

    float lastDragX() {
        return lastDragX;
    }

    float lastDragY() {
        return lastDragY;
    }
}
