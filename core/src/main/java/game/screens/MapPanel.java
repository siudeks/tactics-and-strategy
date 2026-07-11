package game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import game.domain.CampaignState;
import game.domain.Side;
import game.domain.Unit;
import game.domain.UnitType;
import game.screens.BattlefieldScreen.MoveTargetAssignment;
import game.screens.BattlefieldScreen.TileCoord;
import game.screens.BattlefieldScreen.UnitIconPalette;
import game.screens.BattlefieldScreen.UnitRenderPlacement;
import game.terrain.TerrainMapDefinition;
import game.platform.TerrainTileAtlas;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

final class MapPanel extends Actor {
    static final float DRAW_TILE_SIZE = 16f;
    static final float UNIT_SIZE_IN_TILES = 2f;
    private static final float MIN_ZOOM_LEVEL = 0.5f;
    private static final float MAX_ZOOM_LEVEL = 3.0f;
    private static final float ZOOM_STEP_PERCENT = 0.1f;

    private final Texture pixel;
    private final TerrainMapDefinition mapDefinition;
    private final TerrainTileAtlas tileAtlas;
    private final Runnable onEndTurn;
    private final Supplier<CampaignState> campaignStateSupplier;
    private final MovementPlaybackStateSource movementPlaybackStateSource;
    private final int scenarioMapHeightTiles;
    private final MoveTargetRecorder moveTargetRecorder;
    private final Runnable onMoveTargetConfirmed;
    private final Map<UnitType, Texture> unitIcons;
    private final Texture unidentifiedIcon;
    private final SelectionState selectionState;
    private final CameraController cameraController;

    private boolean debugGridOverlay;
    private float selectorBlinkTimer;
    private boolean selectorVisible = true;
    private boolean pendingSelectionCameraCenter;
    private final Map<String, TileCoord> moveTargetsByUnit;
    private @Nullable TileCoord movePreviewTile;
    private float movePreviewBlinkTimer;
    private boolean movePreviewVisible;
    private InteractionLockState interactionLockState;
    private Side commandSide;

    private final int mapWidthTiles;
    private final int mapHeightTiles;

    enum InputPath {
        CLICK,
        DRAG,
        ZOOM,
        KEY_SHORTCUT,
        SELECTION
    }

    enum EnterIssuingOrdersOutcome {
        NO_PROGRESS,
        SWITCH_COMMAND_SIDE,
        END_TURN
    }

    @SuppressWarnings("NullAway.Init")
    MapPanel(Texture pixel,
             Runnable onEndTurn,
             Supplier<CampaignState> campaignStateSupplier,
             MovementPlaybackStateSource movementPlaybackStateSource,
             int scenarioMapHeightTiles,
             MoveTargetRecorder moveTargetRecorder,
             Runnable onMoveTargetConfirmed,
             Map<UnitType, Texture> unitIcons,
             Texture unidentifiedIcon) {
        this.pixel = pixel;
        this.onEndTurn = onEndTurn;
        this.campaignStateSupplier = Objects.requireNonNull(campaignStateSupplier, "campaignStateSupplier must not be null");
        this.movementPlaybackStateSource = Objects.requireNonNull(movementPlaybackStateSource,
            "movementPlaybackStateSource must not be null");
        this.scenarioMapHeightTiles = scenarioMapHeightTiles;
        this.moveTargetRecorder = Objects.requireNonNull(moveTargetRecorder, "moveTargetRecorder must not be null");
        this.onMoveTargetConfirmed = Objects.requireNonNull(onMoveTargetConfirmed, "onMoveTargetConfirmed must not be null");
        this.unitIcons = Objects.requireNonNull(unitIcons, "unitIcons must not be null");
        this.unidentifiedIcon = Objects.requireNonNull(unidentifiedIcon, "unidentifiedIcon must not be null");
        this.mapDefinition = new TerrainMapDefinition();
        this.tileAtlas = new TerrainTileAtlas(mapDefinition);
        this.mapWidthTiles = mapDefinition.getWidthTiles();
        this.mapHeightTiles = mapDefinition.getHeightTiles();
        this.selectionState = new SelectionState();
        this.cameraController = new CameraController(
            mapWidthTiles * DRAW_TILE_SIZE,
            mapHeightTiles * DRAW_TILE_SIZE,
            MIN_ZOOM_LEVEL,
            MAX_ZOOM_LEVEL,
            ZOOM_STEP_PERCENT
        );
        this.moveTargetsByUnit = new HashMap<>();
        this.interactionLockState = InteractionLockState.NONE;
        this.commandSide = campaignStateSupplier.get().activeSide();

        setTouchable(Touchable.enabled);
        this.debugGridOverlay = false;
        addInputHandling();
        resetSelection();
    }

    private void addInputHandling() {
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                initializePointerDrag(interactionLockState, cameraController, x, y);
                if (getStage() != null) {
                    getStage().setKeyboardFocus(MapPanel.this);
                    getStage().setScrollFocus(MapPanel.this);
                }
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (shouldBlockInputPath(interactionLockState, InputPath.DRAG)) {
                    return;
                }
                cameraController.dragTo(x, y, getWidth(), getHeight());
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (shouldBlockInputPath(interactionLockState, InputPath.CLICK)) {
                    return;
                }
                float dx = x - cameraController.lastDragX();
                float dy = y - cameraController.lastDragY();
                if (dx * dx + dy * dy >= 100f) return; // drag, not a click

                TileCoord clickedTile = BattlefieldScreen.mapTileAtPanelPoint(
                    x,
                    y,
                    cameraController.cameraX(),
                    cameraController.cameraY(),
                    cameraController.zoomLevel(),
                    mapWidthTiles,
                    mapHeightTiles
                );
                boolean clickedTilePassable = isTilePassable(clickedTile);
                MoveTargetAssignment assignment = BattlefieldScreen.moveTargetAssignmentForClick(
                    selectionState.isMoveModeActive(),
                    selectionState.selectedUnitId(),
                    movePreviewTile,
                    clickedTile,
                    clickedTilePassable
                );
                if (assignment != null) {
                    moveTargetsByUnit.put(assignment.unitId(), assignment.tile());
                    moveTargetRecorder.assign(assignment.unitId(), assignment.tile());
                    if (BattlefieldScreen.shouldPlayMoveConfirmationSound(assignment)) {
                        onMoveTargetConfirmed.run();
                    }
                    String selectedUnitId = selectionState.selectedUnitId();
                    selectionState.deactivateMoveMode();
                    clearMovePreview();
                    CampaignState state = campaignStateSupplier.get();
                    selectUnit(BattlefieldScreen.nextUnassignedUnitId(unitsForCommandSide(state), selectedUnitId, moveTargetsByUnit));
                    return;
                }
                if (BattlefieldScreen.shouldConsumeClickInMoveMode(selectionState.isMoveModeActive())) {
                    return;
                }

                float sx = x + getX();
                float sy = y + getY();
                CampaignState state = campaignStateSupplier.get();
                List<UnitRenderPlacement> placements = BattlefieldScreen.computeVisibleUnitPlacements(
                    state,
                    movementPlaybackStateSource.get(),
                    mapHeightTiles,
                    getX(),
                    getY(),
                    getWidth(),
                    getHeight(),
                    cameraController.cameraX(),
                    cameraController.cameraY(),
                    cameraController.zoomLevel());
                selectUnit(BattlefieldScreen.unitIdAtScreenPoint(placements, sx, sy, commandSide));
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                if (shouldBlockInputPath(interactionLockState, InputPath.SELECTION)) {
                    clearMovePreview();
                    return true;
                }
                TileCoord hoveredTile = BattlefieldScreen.mapTileAtPanelPoint(
                    x,
                    y,
                    cameraController.cameraX(),
                    cameraController.cameraY(),
                    cameraController.zoomLevel(),
                    mapWidthTiles,
                    mapHeightTiles
                );
                movePreviewTile = BattlefieldScreen.movePreviewTile(
                    selectionState.isMoveModeActive(),
                    selectionState.selectedUnitId(),
                    hoveredTile,
                    isTilePassable(hoveredTile)
                );
                movePreviewVisible = true;
                movePreviewBlinkTimer = 0f;
                return movePreviewTile != null;
            }

            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                if (shouldBlockInputPath(interactionLockState, InputPath.ZOOM)) {
                    return true;
                }
                boolean ctrlPressed = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)
                    || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
                if (!ctrlPressed || amountY == 0f) {
                    return false;
                }
                return cameraController.zoomAt(x, y, amountY, getWidth(), getHeight());
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (shouldBlockInputPath(interactionLockState, InputPath.KEY_SHORTCUT)) {
                    return true;
                }
                if (keycode == Input.Keys.G) {
                    debugGridOverlay = !debugGridOverlay;
                    return true;
                }
                if (keycode == Input.Keys.ENTER) {
                    handleEnterKey();
                    return true;
                }
                if (keycode == Input.Keys.TAB) {
                    cycleSelectedUnit();
                    return true;
                }
                if (keycode == Input.Keys.ESCAPE) {
                    selectUnit(null);
                    return true;
                }
                if (keycode == Input.Keys.M) {
                    toggleMoveMode();
                    return true;
                }
                return false;
            }
        });
    }

    void setInteractionLockState(InteractionLockState interactionLockState) {
        this.interactionLockState = Objects.requireNonNull(interactionLockState, "interactionLockState must not be null");
        if (shouldBlockInputPath(interactionLockState, InputPath.CLICK)
            || shouldBlockInputPath(interactionLockState, InputPath.SELECTION)) {
            selectionState.deactivateMoveMode();
            clearMovePreview();
        }
    }

    static boolean shouldBlockInputPath(InteractionLockState interactionLockState, InputPath inputPath) {
        Objects.requireNonNull(interactionLockState, "interactionLockState must not be null");
        Objects.requireNonNull(inputPath, "inputPath must not be null");
        return interactionLockState.blocks(inputPath);
    }

    static boolean initializePointerDrag(InteractionLockState interactionLockState,
                                         CameraController cameraController,
                                         float x,
                                         float y) {
        Objects.requireNonNull(interactionLockState, "interactionLockState must not be null");
        Objects.requireNonNull(cameraController, "cameraController must not be null");
        if (shouldBlockInputPath(interactionLockState, InputPath.DRAG)) {
            return false;
        }
        cameraController.startDrag(x, y);
        return true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = getX();
        float y = getY();
        float w = getWidth();
        float h = getHeight();

        cameraController.clampToViewport(w, h);

        batch.setColor(BattlefieldScreen.MAP_BG);
        batch.draw(pixel, x, y, w, h);

        drawTerrain(batch, x, y, w, h);

        drawAssignedMoveTargets(batch, x, y);

        drawMovePreview(batch, x, y);

        if (debugGridOverlay) {
            drawDebugGrid(batch, x, y, w, h);
        }

        drawUnits(batch, x, y, w, h);

        batch.setColor(Color.WHITE);
    }

    private void drawTerrain(Batch batch, float panelX, float panelY, float panelW, float panelH) {
        float zoomLevel = cameraController.zoomLevel();
        float cameraX = cameraController.cameraX();
        float cameraY = cameraController.cameraY();
        float scaledTileSize = DRAW_TILE_SIZE * zoomLevel;
        float visibleWorldWidth = panelW / zoomLevel;
        int startCol = MathUtils.clamp((int) Math.floor(cameraX / DRAW_TILE_SIZE), 0, mapWidthTiles - 1);
        int endCol = MathUtils.clamp((int) Math.ceil((cameraX + visibleWorldWidth) / DRAW_TILE_SIZE), 0, mapWidthTiles - 1);

        batch.setColor(Color.WHITE);
        for (int rowTop = 0; rowTop < mapHeightTiles; rowTop++) {
            float worldY = (mapHeightTiles - rowTop - 1) * DRAW_TILE_SIZE;
            float screenY = panelY + (worldY - cameraY) * zoomLevel;
            if (screenY + scaledTileSize < panelY || screenY > panelY + panelH) {
                continue;
            }

            for (int col = startCol; col <= endCol; col++) {
                float worldX = col * DRAW_TILE_SIZE;
                float screenX = panelX + (worldX - cameraX) * zoomLevel;

                int tileId = mapDefinition.getMapTileId(col, rowTop);
                batch.draw(tileAtlas.getRegion(tileId), screenX, screenY, scaledTileSize, scaledTileSize);
            }
        }
    }

    private void drawDebugGrid(Batch batch, float panelX, float panelY, float panelW, float panelH) {
        float zoomLevel = cameraController.zoomLevel();
        float cameraX = cameraController.cameraX();
        float cameraY = cameraController.cameraY();
        batch.setColor(BattlefieldScreen.GRID);

        float visibleWorldWidth = panelW / zoomLevel;
        int startCol = MathUtils.clamp((int) Math.floor(cameraX / DRAW_TILE_SIZE), 0, mapWidthTiles - 1);
        int endCol = MathUtils.clamp((int) Math.ceil((cameraX + visibleWorldWidth) / DRAW_TILE_SIZE), 0, mapWidthTiles);
        for (int col = startCol; col <= endCol; col++) {
            float lineX = panelX + (col * DRAW_TILE_SIZE - cameraX) * zoomLevel;
            batch.draw(pixel, lineX, panelY, 1f, panelH);
        }

        for (int rowTop = 0; rowTop <= mapHeightTiles; rowTop++) {
            float worldY = (mapHeightTiles - rowTop) * DRAW_TILE_SIZE;
            float lineY = panelY + (worldY - cameraY) * zoomLevel;
            if (lineY < panelY || lineY > panelY + panelH) {
                continue;
            }
            batch.draw(pixel, panelX, lineY, panelW, 1f);
        }
    }

    private void drawMovePreview(Batch batch, float panelX, float panelY) {
        if (movePreviewTile == null) {
            return;
        }

        float zoomLevel = cameraController.zoomLevel();
        float cameraX = cameraController.cameraX();
        float cameraY = cameraController.cameraY();
        float scaledTileSize = DRAW_TILE_SIZE * zoomLevel;
        float previewDrawSize = scaledTileSize * UNIT_SIZE_IN_TILES;
        float screenX = panelX + (movePreviewTile.tileX() * DRAW_TILE_SIZE - cameraX) * zoomLevel;
        float worldY = (mapHeightTiles - movePreviewTile.tileY() - UNIT_SIZE_IN_TILES) * DRAW_TILE_SIZE;
        float screenY = panelY + (worldY - cameraY) * zoomLevel;
        float border = previewDrawSize / 16f;
        float borderX = screenX - border;
        float borderY = screenY - border;

        batch.setColor(movePreviewVisible ? Color.WHITE : Color.BLACK);
        drawBlock(batch, borderX, borderY, previewDrawSize + border * 2f, border);
        drawBlock(batch, borderX, screenY + previewDrawSize, previewDrawSize + border * 2f, border);
        drawBlock(batch, borderX, screenY, border, previewDrawSize);
        drawBlock(batch, screenX + previewDrawSize, screenY, border, previewDrawSize);
    }

    private void drawAssignedMoveTargets(Batch batch, float panelX, float panelY) {
        if (moveTargetsByUnit.isEmpty()) {
            return;
        }

        float zoomLevel = cameraController.zoomLevel();
        float cameraX = cameraController.cameraX();
        float cameraY = cameraController.cameraY();
        float scaledTileSize = DRAW_TILE_SIZE * zoomLevel;
        float poleWidth = Math.max(1f, scaledTileSize / 10f);
        float poleHeight = scaledTileSize * 0.75f;
        float flagWidth = scaledTileSize * 0.45f;
        float flagHeight = scaledTileSize * 0.3f;

        for (TileCoord targetTile : moveTargetsByUnit.values()) {
            float tileScreenX = panelX + (targetTile.tileX() * DRAW_TILE_SIZE - cameraX) * zoomLevel;
            float worldY = (mapHeightTiles - targetTile.tileY() - 1f) * DRAW_TILE_SIZE;
            float tileScreenY = panelY + (worldY - cameraY) * zoomLevel;

            float poleX = tileScreenX + scaledTileSize * 0.15f;
            float poleY = tileScreenY + scaledTileSize * 0.1f;
            float flagX = poleX + poleWidth;
            float flagY = poleY + poleHeight - flagHeight;

            batch.setColor(Color.WHITE);
            drawBlock(batch, poleX, poleY, poleWidth, poleHeight);
            batch.setColor(Color.valueOf("D9482B"));
            drawBlock(batch, flagX, flagY, flagWidth, flagHeight);
        }
    }

    private void drawUnits(Batch batch, float panelX, float panelY, float panelWidth, float panelHeight) {
        CampaignState campaignState = campaignStateSupplier.get();
        String selectedUnitId = selectionState.selectedUnitId();
        float cameraX = cameraController.cameraX();
        float cameraY = cameraController.cameraY();
        float zoomLevel = cameraController.zoomLevel();
        List<UnitRenderPlacement> placements = BattlefieldScreen.computeVisibleUnitPlacements(
            campaignState,
            movementPlaybackStateSource.get(),
            scenarioMapHeightTiles,
            panelX,
            panelY,
            panelWidth,
            panelHeight,
            cameraX,
            cameraY,
            zoomLevel
        );
        UnitRenderPlacement selectedPlacement = null;
        for (UnitRenderPlacement placement : placements) {
            if (placement.unit().id().equals(selectedUnitId)) {
                selectedPlacement = placement;
                continue;
            }
            drawUnitPlacement(batch, placement, commandSide);
        }
        if (selectedPlacement != null) {
            drawUnitPlacement(batch, selectedPlacement, commandSide);
        }
    }

    private void drawUnitPlacement(Batch batch, UnitRenderPlacement placement, Side activeSide) {
        String selectedUnitId = selectionState.selectedUnitId();
        float border = placement.drawSize() / 16f;
        float borderX = placement.screenX() - border;
        float borderY = placement.screenY() - border;
        UnitIconPalette palette = BattlefieldScreen.paletteFor(placement.unit().side());
        UnitType visibleType = BattlefieldScreen.visibleUnitType(placement.unit(), activeSide);
        drawUnitIcon(batch, placement.screenX(), placement.screenY(), placement.drawSize(), palette.fill(), palette.outline(), visibleType);
        if (placement.unit().id().equals(selectedUnitId) && selectorVisible) {
            batch.setColor(Color.WHITE);
            drawBlock(batch, borderX, borderY, placement.drawSize() + border * 2f, border);
            drawBlock(batch, borderX, placement.screenY() + placement.drawSize(), placement.drawSize() + border * 2f, border);
            drawBlock(batch, borderX, placement.screenY(), border, placement.drawSize());
            drawBlock(batch, placement.screenX() + placement.drawSize(), placement.screenY(), border, placement.drawSize());
        }
    }

    public @Nullable String getSelectedUnitId() {
        return selectionState.selectedUnitId();
    }

    private void selectUnit(@Nullable String unitId) {
        String previousUnitId = selectionState.selectedUnitId();
        if (!Objects.equals(previousUnitId, unitId)) {
            selectionState.select(unitId);
            clearMovePreview();
        } else if (unitId == null) {
            selectionState.clearSelection();
        }
        selectorBlinkTimer = 0f;
        selectorVisible = true;
        centerCameraOnSelectedUnitIfNeeded();
    }

    public boolean isMoveModeActive() {
        return selectionState.isMoveModeActive();
    }

    public void toggleMoveMode() {
        if (selectionState.selectedUnitId() == null) {
            selectionState.clearSelection();
            clearMovePreview();
            return;
        }
        selectionState.toggleMoveMode();
        if (!selectionState.isMoveModeActive()) {
            clearMovePreview();
        }
    }

    private void centerCameraOnSelectedUnitIfNeeded() {
        String selectedUnitId = selectionState.selectedUnitId();
        if (selectedUnitId == null) {
            pendingSelectionCameraCenter = false;
            return;
        }
        if (!BattlefieldScreen.isViewportReadyForCameraCentering(getWidth(), getHeight())) {
            pendingSelectionCameraCenter = true;
            return;
        }
        CampaignState state = campaignStateSupplier.get();
        Unit selectedUnit = state.units().stream()
            .filter(unit -> unit.id().equals(selectedUnitId))
            .findFirst()
            .orElse(null);
        if (selectedUnit == null) {
            pendingSelectionCameraCenter = false;
            return;
        }

        if (BattlefieldScreen.isUnitFullyVisibleInViewport(
            selectedUnit,
            scenarioMapHeightTiles,
            cameraController.cameraX(),
            cameraController.cameraY(),
            getWidth(),
            getHeight(),
            cameraController.zoomLevel())) {
            pendingSelectionCameraCenter = false;
            return;
        }

        float unitWorldCenterX = selectedUnit.tileX() * DRAW_TILE_SIZE + (UNIT_SIZE_IN_TILES * DRAW_TILE_SIZE) / 2f;
        float unitWorldCenterY = (scenarioMapHeightTiles - selectedUnit.tileY() - UNIT_SIZE_IN_TILES / 2f) * DRAW_TILE_SIZE;

        cameraController.centerOn(unitWorldCenterX, unitWorldCenterY, getWidth(), getHeight());
        pendingSelectionCameraCenter = false;
    }

    void resetSelection() {
        clearMovePreview();
        CampaignState state = campaignStateSupplier.get();
        moveTargetsByUnit.clear();
        commandSide = state.activeSide();
        List<Unit> active = unitsForCommandSide(state);
        selectUnit(active.isEmpty() ? null : active.getFirst().id());
    }

    private void cycleSelectedUnit() {
        CampaignState state = campaignStateSupplier.get();
        List<Unit> active = unitsForCommandSide(state);
        selectUnit(BattlefieldScreen.nextSelectedUnitId(active, selectionState.selectedUnitId()));
    }

    private void handleEnterKey() {
        CampaignState state = campaignStateSupplier.get();
        EnterIssuingOrdersOutcome outcome = enterIssuingOrdersOutcome(
            areAllMoveOrdersAssignedForCommandSide(state),
            commandSide,
            state.activeSide()
        );
        applyEnterIssuingOrdersOutcome(
            outcome,
            () -> switchCommandSideForIssuingOrders(state),
            onEndTurn
        );
    }

    static EnterIssuingOrdersOutcome enterIssuingOrdersOutcome(boolean currentCommandSideComplete,
                                                               Side commandSide,
                                                               Side initialActiveSide) {
        if (!currentCommandSideComplete) {
            return EnterIssuingOrdersOutcome.NO_PROGRESS;
        }
        return commandSide == initialActiveSide
            ? EnterIssuingOrdersOutcome.SWITCH_COMMAND_SIDE
            : EnterIssuingOrdersOutcome.END_TURN;
    }

    static void applyEnterIssuingOrdersOutcome(EnterIssuingOrdersOutcome outcome,
                                               Runnable onSwitchCommandSide,
                                               Runnable onEndTurn) {
        Objects.requireNonNull(outcome, "outcome must not be null");
        Objects.requireNonNull(onSwitchCommandSide, "onSwitchCommandSide must not be null");
        Objects.requireNonNull(onEndTurn, "onEndTurn must not be null");
        switch (outcome) {
            case NO_PROGRESS -> {
                return;
            }
            case SWITCH_COMMAND_SIDE -> onSwitchCommandSide.run();
            case END_TURN -> onEndTurn.run();
        }
    }

    private void switchCommandSideForIssuingOrders(CampaignState state) {
        commandSide = oppositeCommandSide(commandSide);
        selectionState.deactivateMoveMode();
        clearMovePreview();
        selectUnit(BattlefieldScreen.nextUnassignedUnitId(unitsForCommandSide(state), null, moveTargetsByUnit));
    }

    private boolean areAllMoveOrdersAssignedForCommandSide(CampaignState state) {
        return unitsForCommandSide(state).stream()
            .map(Unit::id)
            .allMatch(moveTargetsByUnit::containsKey);
    }

    private List<Unit> unitsForCommandSide(CampaignState state) {
        return state.units().stream()
            .filter(unit -> unit.side() == commandSide)
            .toList();
    }

    private static Side oppositeCommandSide(Side side) {
        return switch (side) {
            case ALLIES -> Side.AXIS;
            case AXIS -> Side.ALLIES;
            case NEUTRAL -> Side.NEUTRAL;
        };
    }

    @FunctionalInterface
    interface MoveTargetRecorder {
        void assign(String unitId, TileCoord tileCoord);
    }

    @FunctionalInterface
    interface MovementPlaybackStateSource {
        @Nullable MovementPlaybackRenderState get();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (pendingSelectionCameraCenter) {
            centerCameraOnSelectedUnitIfNeeded();
        }
        selectorBlinkTimer += delta;
        if (selectorBlinkTimer >= 0.5f) {
            selectorBlinkTimer -= 0.5f;
            selectorVisible = !selectorVisible;
        }
        movePreviewBlinkTimer += delta;
        if (movePreviewBlinkTimer >= 0.5f) {
            movePreviewBlinkTimer -= 0.5f;
            movePreviewVisible = !movePreviewVisible;
        }
    }

    public void dispose() {
        tileAtlas.dispose();
    }

    private void drawUnitIcon(Batch batch,
                  float x,
                  float y,
                  float size,
                  Color fillColor,
                  Color outlineColor,
                  @Nullable UnitType visibleType) {
        float pixel = size / 16f;
        batch.setColor(outlineColor);
        drawBlock(batch, x - pixel, y - pixel, size + pixel * 2f, pixel);
        drawBlock(batch, x - pixel, y + size, size + pixel * 2f, pixel);
        drawBlock(batch, x - pixel, y, pixel, size);
        drawBlock(batch, x + size, y, pixel, size);

        batch.setColor(fillColor);
        drawBlock(batch, x, y, size, size);

        batch.setColor(outlineColor);
        batch.draw(iconTexture(visibleType), x, y, size, size);
    }

    private Texture iconTexture(@Nullable UnitType visibleType) {
        if (visibleType == null) {
            return unidentifiedIcon;
        }
        return unitIcons.getOrDefault(visibleType, unidentifiedIcon);
    }

    private void drawBlock(Batch batch, float x, float y, float width, float height) {
        batch.draw(pixel, x, y, width, height);
    }

    private boolean isTilePassable(@Nullable TileCoord tile) {
        if (tile == null) {
            return false;
        }
        int mapIndex = tile.tileY() * mapWidthTiles + tile.tileX();
        return BattlefieldScreen.isPassableTerrainCode(mapDefinition.getTerrainCode(mapIndex));
    }

    private void clearMovePreview() {
        movePreviewTile = null;
        movePreviewBlinkTimer = 0f;
        movePreviewVisible = true;
    }
}
