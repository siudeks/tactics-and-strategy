package game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import game.domain.CampaignState;
import game.domain.Side;
import game.domain.Unit;
import game.domain.UnitType;
import game.engine.GameRuntime;
import game.scenario.LoadedScenario;
import game.terrain.TerrainMapDefinition;
import game.terrain.TerrainTileAtlas;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class BattlefieldScreen extends ScreenAdapter {

    private static final Color BG = Color.valueOf("1E232B");
    private static final Color MAP_BG = Color.valueOf("181814");
    private static final Color PANEL_BG = Color.valueOf("2C3038");
    private static final Color STATUS_BG = Color.valueOf("1F242B");
    private static final Color GRID = Color.valueOf("4E5D4A");
    private static final Color ALLIES_UNIT_FILL = Color.valueOf("E5D44E");
    private static final Color ALLIES_UNIT_OUTLINE = Color.valueOf("243B8F");
    private static final Color AXIS_UNIT_FILL = Color.valueOf("C97B3E");
    private static final Color AXIS_UNIT_OUTLINE = Color.valueOf("4A2210");
    private static final String ICON_MEDIUM_TANK_FILE = "ui/icon_medium_tank_64x64.png";
    private static final String ICON_LIGHT_TANK_FILE = "ui/icon_light_tank_64x64.png";
    private static final String ICON_INFANTRY_TANK_FILE = "ui/icon_infantry_tank_64x64.png";
    private static final String ICON_RECCE_FILE = "ui/icon_recce_64x64.png";
    private static final String ICON_MOTORISED_INFANTRY_FILE = "ui/icon_motorised_infantry_64x64.png";
    private static final String ICON_FOOT_INFANTRY_FILE = "ui/infantry_rifle_64x64.png";
    private static final String ICON_SUPPORT_GROUP_FILE = "ui/icon_support_group_64x64.png";
    private static final String ICON_ANTI_TANK_FILE = "ui/icon_anti_tank_64x64.png";
    private static final String ICON_ARTILLERY_FILE = "ui/icon_artillery_64x64.png";
    private static final String ICON_HQ_FILE = "ui/icon_hq_64x64.png";
    private static final String ICON_UNIDENTIFIED_FILE = "ui/icon_unidentified_64x64.png";

    private final Game game;
    private final LoadedScenario loadedScenario;

    private Stage stage;
    private BitmapFont font;
    private Texture whiteTexture;
    private MapPanel mapPanel;
    private GameRuntime gameRuntime;
    private Label statusLabel;
    private Label unitNameLabel;
    private Table unitInfoSection;
    private Map<UnitType, Texture> unitIconTextures;
    private Texture unidentifiedIconTexture;

    public BattlefieldScreen(Game game, LoadedScenario loadedScenario) {
        this.game = game;
        this.loadedScenario = loadedScenario;
    }

    @Override
    public void show() {
        gameRuntime = new GameRuntime(loadedScenario);
        stage = new Stage(new ScreenViewport());
        font = new BitmapFont();
        whiteTexture = createWhiteTexture();
        loadUnitIcons();

        TextureRegionDrawable base = new TextureRegionDrawable(new TextureRegion(whiteTexture));
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = base.tint(Color.valueOf("3D4450"));
        buttonStyle.down = base.tint(Color.valueOf("2C323B"));
        buttonStyle.over = base.tint(Color.valueOf("4A5362"));
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;

        Table root = new Table();
        root.setFillParent(true);

        mapPanel = new MapPanel(
            whiteTexture,
            this::endTurn,
            gameRuntime::getCurrentCampaignState,
            loadedScenario.scenarioDefinition().mapHeight(),
            unitIconTextures,
            unidentifiedIconTexture
        );

        Table topArea = new Table();
        topArea.add(mapPanel).grow().pad(8f);
        topArea.add(createCommandPanel(labelStyle, buttonStyle, base.tint(PANEL_BG), mapPanel)).width(300f).growY().pad(8f, 0f, 8f, 8f);

        statusLabel = new Label(runtimeStatusSummary(), labelStyle);
        statusLabel.setAlignment(1);
        Table statusBar = new Table();
        statusBar.setBackground(base.tint(STATUS_BG));
        statusBar.add(statusLabel).left().padLeft(12f);

        root.add(topArea).grow().row();
        root.add(statusBar).growX().height(42f);

        stage.addActor(root);
        stage.setKeyboardFocus(mapPanel);
        stage.setScrollFocus(mapPanel);
        Gdx.input.setInputProcessor(stage);
    }

    private Table createCommandPanel(Label.LabelStyle labelStyle,
                                     TextButton.TextButtonStyle buttonStyle,
                                     Drawable background,
                                     MapPanel mapPanel) {
        Table panel = new Table();
        panel.setBackground(background);
        panel.defaults().growX().pad(8f);

        panel.add(new Label("Command Panel", labelStyle)).left().padTop(10f).row();
        unitNameLabel = new Label("", labelStyle);
        unitInfoSection = new Table();
        unitInfoSection.add(unitNameLabel).left();
        unitInfoSection.setVisible(false);
        panel.add(unitInfoSection).growX().left().row();
        TextButton moveButton = new TextButton("Move", buttonStyle);
        moveButton.setDisabled(true);
        moveButton.setTouchable(Touchable.disabled);
        panel.add(moveButton).row();

        TextButton holdButton = new TextButton("Hold", buttonStyle);
        holdButton.setDisabled(true);
        holdButton.setTouchable(Touchable.disabled);
        panel.add(holdButton).row();
        panel.add().growY().row();

        TextButton endTurnButton = new TextButton("End Turn  [Enter]", buttonStyle);
        endTurnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                endTurn();
            }
        });
        panel.add(endTurnButton).padBottom(4f).row();

        TextButton menuButton = new TextButton("Menu", buttonStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        panel.add(menuButton).padBottom(10f).row();

        return panel;
    }

    public String runtimeStatusSummary() {
        return String.format("Scenariusz: %s | Tura: %d | Strona aktywna: %s",
            loadedScenario.scenarioDefinition().name(),
            gameRuntime.getTurnNumber(),
            gameRuntime.getActiveSideCode());
    }

    private void endTurn() {
        gameRuntime.simulateOneTurn();
        mapPanel.resetSelection();
        if (statusLabel != null) {
            statusLabel.setText(runtimeStatusSummary());
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(BG);
        stage.act(delta);
        if (unitNameLabel != null && unitInfoSection != null) {
            syncUnitInfoPanel(mapPanel.getSelectedUnitId(), new UnitInfoView() {
                public void showUnit(String id) {
                    unitNameLabel.setText(id);
                    unitInfoSection.setVisible(true);
                }
                public void hide() {
                    unitInfoSection.setVisible(false);
                }
            });
        }
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
        mapPanel.dispose();
        whiteTexture.dispose();
        disposeUnitIcons();
    }

    private void loadUnitIcons() {
        unitIconTextures = new EnumMap<>(UnitType.class);
        unitIconTextures.put(UnitType.MEDIUM_TANK, new Texture(Gdx.files.internal(ICON_MEDIUM_TANK_FILE)));
        unitIconTextures.put(UnitType.LIGHT_TANK, new Texture(Gdx.files.internal(ICON_LIGHT_TANK_FILE)));
        unitIconTextures.put(UnitType.INFANTRY_TANK, new Texture(Gdx.files.internal(ICON_INFANTRY_TANK_FILE)));
        unitIconTextures.put(UnitType.RECCE, new Texture(Gdx.files.internal(ICON_RECCE_FILE)));
        unitIconTextures.put(UnitType.MOTORISED_INFANTRY, new Texture(Gdx.files.internal(ICON_MOTORISED_INFANTRY_FILE)));
        unitIconTextures.put(UnitType.FOOT_INFANTRY, new Texture(Gdx.files.internal(ICON_FOOT_INFANTRY_FILE)));
        unitIconTextures.put(UnitType.SUPPORT_GROUP, new Texture(Gdx.files.internal(ICON_SUPPORT_GROUP_FILE)));
        unitIconTextures.put(UnitType.ANTI_TANK, new Texture(Gdx.files.internal(ICON_ANTI_TANK_FILE)));
        unitIconTextures.put(UnitType.ARTILLERY, new Texture(Gdx.files.internal(ICON_ARTILLERY_FILE)));
        unitIconTextures.put(UnitType.HQ, new Texture(Gdx.files.internal(ICON_HQ_FILE)));
        unidentifiedIconTexture = new Texture(Gdx.files.internal(ICON_UNIDENTIFIED_FILE));
    }

    private void disposeUnitIcons() {
        if (unitIconTextures != null) {
            for (Texture texture : unitIconTextures.values()) {
                texture.dispose();
            }
            unitIconTextures.clear();
            unitIconTextures = null;
        }
        if (unidentifiedIconTexture != null) {
            unidentifiedIconTexture.dispose();
            unidentifiedIconTexture = null;
        }
    }

    private Texture createWhiteTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    static List<UnitRenderPlacement> computeVisibleUnitPlacements(CampaignState campaignState,
                                                                  int scenarioMapHeightTiles,
                                                                  float panelX,
                                                                  float panelY,
                                                                  float panelWidth,
                                                                  float panelHeight,
                                                                  float cameraX,
                                                                  float cameraY,
                                                                  float zoomLevel) {
        Objects.requireNonNull(campaignState, "campaignState must not be null");

        float scaledTileSize = MapPanel.DRAW_TILE_SIZE * zoomLevel;
        float unitDrawSize = scaledTileSize * MapPanel.UNIT_SIZE_IN_TILES;
        List<UnitRenderPlacement> placements = new ArrayList<>();
        for (Unit unit : campaignState.units()) {
            float iconWorldX = unit.tileX() * MapPanel.DRAW_TILE_SIZE;
            float iconWorldY = (scenarioMapHeightTiles - unit.tileY() - MapPanel.UNIT_SIZE_IN_TILES) * MapPanel.DRAW_TILE_SIZE;
            float iconScreenX = panelX + (iconWorldX - cameraX) * zoomLevel;
            float iconScreenY = panelY + (iconWorldY - cameraY) * zoomLevel;

            if (isUnitOutsideViewport(iconScreenX, iconScreenY, unitDrawSize, panelX, panelY, panelWidth, panelHeight)) {
                continue;
            }

            placements.add(new UnitRenderPlacement(unit, iconScreenX, iconScreenY, unitDrawSize));
        }
        return List.copyOf(placements);
    }

    private static boolean isUnitOutsideViewport(float iconScreenX,
                                                 float iconScreenY,
                                                 float unitDrawSize,
                                                 float panelX,
                                                 float panelY,
                                                 float panelWidth,
                                                 float panelHeight) {
        float visibleLeft = panelX;
        float visibleRight = panelX + panelWidth;
        float visibleBottom = panelY;
        float visibleTop = panelY + panelHeight;

        float iconRight = iconScreenX + unitDrawSize;
        float iconTop = iconScreenY + unitDrawSize;

        return iconRight <= visibleLeft
            || iconScreenX >= visibleRight
            || iconTop <= visibleBottom
            || iconScreenY >= visibleTop;
    }

    static UnitIconPalette paletteFor(Side side) {
        return side == Side.AXIS
            ? new UnitIconPalette(AXIS_UNIT_FILL, AXIS_UNIT_OUTLINE)
            : new UnitIconPalette(ALLIES_UNIT_FILL, ALLIES_UNIT_OUTLINE);
    }

    static UnitType visibleUnitType(Unit unit, Side activeSide) {
        Objects.requireNonNull(unit, "unit must not be null");
        Objects.requireNonNull(activeSide, "activeSide must not be null");
        return unit.side() == activeSide ? unit.type() : null;
    }

    static String unitIdAtScreenPoint(List<UnitRenderPlacement> placements,
                                      float sx, float sy, Side activeSide) {
        for (UnitRenderPlacement p : placements) {
            if (p.unit().side() != activeSide) continue;
            if (sx >= p.screenX() && sx < p.screenX() + p.drawSize()
                    && sy >= p.screenY() && sy < p.screenY() + p.drawSize()) {
                return p.unit().id();
            }
        }
        return null;
    }

    static String nextSelectedUnitId(List<Unit> activeUnits, String currentId) {
        if (activeUnits.isEmpty()) return null;
        int idx = -1;
        for (int i = 0; i < activeUnits.size(); i++) {
            if (activeUnits.get(i).id().equals(currentId)) {
                idx = i;
                break;
            }
        }
        return activeUnits.get((idx + 1) % activeUnits.size()).id();
    }

    static float clampZoomLevel(float zoomLevel, float minZoomLevel, float maxZoomLevel) {
        return MathUtils.clamp(zoomLevel, minZoomLevel, maxZoomLevel);
    }

    static float zoomStepFactor(float amountY, float zoomStepPercent) {
        return (float) Math.pow(1f + zoomStepPercent, -amountY);
    }

    static float cameraAfterZoom(float cameraPosition,
                                 float pointerPositionInPanel,
                                 float oldZoomLevel,
                                 float newZoomLevel) {
        float worldAtPointer = cameraPosition + pointerPositionInPanel / oldZoomLevel;
        return worldAtPointer - pointerPositionInPanel / newZoomLevel;
    }

    static boolean isViewportReadyForCameraCentering(float panelWidth, float panelHeight) {
        return panelWidth > 0f && panelHeight > 0f;
    }

    static boolean isUnitFullyVisibleInViewport(Unit unit,
                                                int scenarioMapHeightTiles,
                                                float cameraX,
                                                float cameraY,
                                                float panelWidth,
                                                float panelHeight,
                                                float zoomLevel) {
        Objects.requireNonNull(unit, "unit must not be null");

        float visibleLeft = cameraX;
        float visibleBottom = cameraY;
        float visibleRight = cameraX + panelWidth / zoomLevel;
        float visibleTop = cameraY + panelHeight / zoomLevel;

        float unitWorldX = unit.tileX() * MapPanel.DRAW_TILE_SIZE;
        float unitWorldY = (scenarioMapHeightTiles - unit.tileY() - MapPanel.UNIT_SIZE_IN_TILES) * MapPanel.DRAW_TILE_SIZE;
        float unitWorldSize = MapPanel.DRAW_TILE_SIZE * MapPanel.UNIT_SIZE_IN_TILES;

        float unitRight = unitWorldX + unitWorldSize;
        float unitTop = unitWorldY + unitWorldSize;

        return unitWorldX >= visibleLeft
            && unitRight <= visibleRight
            && unitWorldY >= visibleBottom
            && unitTop <= visibleTop;
    }

    static float centeredCameraPosition(float unitCenter,
                                        float panelSize,
                                        float zoomLevel,
                                        float mapWorldSize) {
        float visibleWorldSize = panelSize / zoomLevel;
        float unclamped = unitCenter - visibleWorldSize / 2f;
        float maxCamera = Math.max(0f, mapWorldSize - visibleWorldSize);
        return MathUtils.clamp(unclamped, 0f, maxCamera);
    }

    interface UnitInfoView {
        void showUnit(String unitId);
        void hide();
    }

    static void syncUnitInfoPanel(String selectedUnitId, UnitInfoView view) {
        if (selectedUnitId != null) {
            view.showUnit(selectedUnitId);
        } else {
            view.hide();
        }
    }

    static record UnitRenderPlacement(Unit unit, float screenX, float screenY, float drawSize) {
        UnitRenderPlacement {
            Objects.requireNonNull(unit, "unit must not be null");
        }
    }

    static record UnitIconPalette(Color fill, Color outline) {
        UnitIconPalette {
            Objects.requireNonNull(fill, "fill must not be null");
            Objects.requireNonNull(outline, "outline must not be null");
        }
    }

    private static final class MapPanel extends Actor {
        private static final float DRAW_TILE_SIZE = 16f;
        private static final float UNIT_SIZE_IN_TILES = 2f;
        private static final float MIN_ZOOM_LEVEL = 0.5f;
        private static final float MAX_ZOOM_LEVEL = 3.0f;
        private static final float ZOOM_STEP_PERCENT = 0.1f;

        private final Texture pixel;
        private final TerrainMapDefinition mapDefinition;
        private final TerrainTileAtlas tileAtlas;
        private final Runnable onEndTurn;
        private final Supplier<CampaignState> campaignStateSupplier;
        private final int scenarioMapHeightTiles;
        private final Map<UnitType, Texture> unitIcons;
        private final Texture unidentifiedIcon;

        private boolean debugGridOverlay;
        private String selectedUnitId;
        private float selectorBlinkTimer;
        private boolean selectorVisible = true;
        private float cameraX;
        private float cameraY;
        private float zoomLevel;
        private boolean pendingSelectionCameraCenter;
        private float lastDragX;
        private float lastDragY;

        private final int mapWidthTiles;
        private final int mapHeightTiles;
        private final float mapWorldWidth;
        private final float mapWorldHeight;

        private MapPanel(Texture pixel,
                         Runnable onEndTurn,
                         Supplier<CampaignState> campaignStateSupplier,
                         int scenarioMapHeightTiles,
                         Map<UnitType, Texture> unitIcons,
                         Texture unidentifiedIcon) {
            this.pixel = pixel;
            this.onEndTurn = onEndTurn;
            this.campaignStateSupplier = Objects.requireNonNull(campaignStateSupplier, "campaignStateSupplier must not be null");
            this.scenarioMapHeightTiles = scenarioMapHeightTiles;
            this.unitIcons = Objects.requireNonNull(unitIcons, "unitIcons must not be null");
            this.unidentifiedIcon = Objects.requireNonNull(unidentifiedIcon, "unidentifiedIcon must not be null");
            this.mapDefinition = new TerrainMapDefinition();
            this.tileAtlas = new TerrainTileAtlas(mapDefinition);
            this.mapWidthTiles = mapDefinition.getWidthTiles();
            this.mapHeightTiles = mapDefinition.getHeightTiles();
            this.mapWorldWidth = mapWidthTiles * DRAW_TILE_SIZE;
            this.mapWorldHeight = mapHeightTiles * DRAW_TILE_SIZE;
            this.zoomLevel = 1f;

            setTouchable(Touchable.enabled);
            this.debugGridOverlay = false;
            addInputHandling();
            resetSelection();
        }

        private void addInputHandling() {
            addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    lastDragX = x;
                    lastDragY = y;
                    if (getStage() != null) {
                        getStage().setKeyboardFocus(MapPanel.this);
                        getStage().setScrollFocus(MapPanel.this);
                    }
                    return true;
                }

                @Override
                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    float dx = x - lastDragX;
                    float dy = y - lastDragY;
                    cameraX -= dx;
                    cameraY -= dy;
                    clampCamera();

                    lastDragX = x;
                    lastDragY = y;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    float dx = x - lastDragX;
                    float dy = y - lastDragY;
                    if (dx * dx + dy * dy >= 100f) return; // drag, not a click
                    float sx = x + getX();
                    float sy = y + getY();
                    CampaignState state = campaignStateSupplier.get();
                    List<UnitRenderPlacement> placements = computeVisibleUnitPlacements(
                        state, mapHeightTiles, getX(), getY(), getWidth(), getHeight(), cameraX, cameraY, zoomLevel);
                    selectUnit(unitIdAtScreenPoint(placements, sx, sy, state.activeSide()));
                }

                @Override
                public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                    boolean ctrlPressed = Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_LEFT)
                        || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_RIGHT);
                    if (!ctrlPressed || amountY == 0f) {
                        return false;
                    }

                    float oldZoomLevel = zoomLevel;
                    float factor = zoomStepFactor(amountY, ZOOM_STEP_PERCENT);
                    float newZoomLevel = clampZoomLevel(oldZoomLevel * factor, MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL);
                    if (newZoomLevel == oldZoomLevel) {
                        return true;
                    }

                    cameraX = cameraAfterZoom(cameraX, x, oldZoomLevel, newZoomLevel);
                    cameraY = cameraAfterZoom(cameraY, y, oldZoomLevel, newZoomLevel);
                    zoomLevel = newZoomLevel;
                    clampCamera();
                    return true;
                }

                @Override
                public boolean keyDown(InputEvent event, int keycode) {
                    if (keycode == com.badlogic.gdx.Input.Keys.G) {
                        debugGridOverlay = !debugGridOverlay;
                        return true;
                    }
                    if (keycode == com.badlogic.gdx.Input.Keys.ENTER) {
                        onEndTurn.run();
                        return true;
                    }
                    if (keycode == com.badlogic.gdx.Input.Keys.TAB) {
                        cycleSelectedUnit();
                        return true;
                    }
                    if (keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {
                        selectUnit(null);
                        return true;
                    }
                    return false;
                }
            });
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            float x = getX();
            float y = getY();
            float w = getWidth();
            float h = getHeight();

            clampCamera();

            batch.setColor(MAP_BG);
            batch.draw(pixel, x, y, w, h);

            drawTerrain(batch, x, y, w, h);

            if (debugGridOverlay) {
                drawDebugGrid(batch, x, y, w, h);
            }

            drawUnits(batch, x, y, w, h);

            batch.setColor(Color.WHITE);
        }

        private void drawTerrain(Batch batch, float panelX, float panelY, float panelW, float panelH) {
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
            batch.setColor(GRID);

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

        private void drawUnits(Batch batch, float panelX, float panelY, float panelWidth, float panelHeight) {
            CampaignState campaignState = campaignStateSupplier.get();
            List<UnitRenderPlacement> placements = computeVisibleUnitPlacements(
                campaignState,
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
                drawUnitPlacement(batch, placement, campaignState.activeSide());
            }
            if (selectedPlacement != null) {
                drawUnitPlacement(batch, selectedPlacement, campaignState.activeSide());
            }
        }

        private void drawUnitPlacement(Batch batch, UnitRenderPlacement placement, Side activeSide) {
            float border = placement.drawSize() / 16f;
            float borderX = placement.screenX() - border;
            float borderY = placement.screenY() - border;
            UnitIconPalette palette = paletteFor(placement.unit().side());
            UnitType visibleType = visibleUnitType(placement.unit(), activeSide);
            drawUnitIcon(batch, placement.screenX(), placement.screenY(), placement.drawSize(), palette.fill(), palette.outline(), visibleType);
            if (placement.unit().id().equals(selectedUnitId) && selectorVisible) {
                batch.setColor(Color.WHITE);
                drawBlock(batch, borderX, borderY, placement.drawSize() + border * 2f, border);
                drawBlock(batch, borderX, placement.screenY() + placement.drawSize(), placement.drawSize() + border * 2f, border);
                drawBlock(batch, borderX, placement.screenY(), border, placement.drawSize());
                drawBlock(batch, placement.screenX() + placement.drawSize(), placement.screenY(), border, placement.drawSize());
            }
        }

        private void clampCamera() {
            float visibleWorldWidth = getWidth() / zoomLevel;
            float visibleWorldHeight = getHeight() / zoomLevel;
            float maxCameraX = Math.max(0f, mapWorldWidth - visibleWorldWidth);
            float maxCameraY = Math.max(0f, mapWorldHeight - visibleWorldHeight);
            cameraX = MathUtils.clamp(cameraX, 0f, maxCameraX);
            cameraY = MathUtils.clamp(cameraY, 0f, maxCameraY);
        }

        public String getSelectedUnitId() {
            return selectedUnitId;
        }

        private void selectUnit(String unitId) {
            selectedUnitId = unitId;
            selectorBlinkTimer = 0f;
            selectorVisible = true;
            centerCameraOnSelectedUnitIfNeeded();
        }

        private void centerCameraOnSelectedUnitIfNeeded() {
            if (selectedUnitId == null) {
                pendingSelectionCameraCenter = false;
                return;
            }
            if (!isViewportReadyForCameraCentering(getWidth(), getHeight())) {
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

            if (isUnitFullyVisibleInViewport(selectedUnit, scenarioMapHeightTiles, cameraX, cameraY, getWidth(), getHeight(), zoomLevel)) {
                pendingSelectionCameraCenter = false;
                return;
            }

            float unitWorldCenterX = selectedUnit.tileX() * DRAW_TILE_SIZE + (UNIT_SIZE_IN_TILES * DRAW_TILE_SIZE) / 2f;
            float unitWorldCenterY = (scenarioMapHeightTiles - selectedUnit.tileY() - UNIT_SIZE_IN_TILES / 2f) * DRAW_TILE_SIZE;

            cameraX = centeredCameraPosition(unitWorldCenterX, getWidth(), zoomLevel, mapWorldWidth);
            cameraY = centeredCameraPosition(unitWorldCenterY, getHeight(), zoomLevel, mapWorldHeight);
            clampCamera();
            pendingSelectionCameraCenter = false;
        }

        void resetSelection() {
            CampaignState state = campaignStateSupplier.get();
            List<Unit> active = state.units().stream()
                .filter(u -> u.side() == state.activeSide())
                .toList();
            selectUnit(active.isEmpty() ? null : active.getFirst().id());
        }

        private void cycleSelectedUnit() {
            CampaignState state = campaignStateSupplier.get();
            List<Unit> active = state.units().stream()
                .filter(u -> u.side() == state.activeSide())
                .toList();
            selectUnit(nextSelectedUnitId(active, selectedUnitId));
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
                                  UnitType visibleType) {
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

        private Texture iconTexture(UnitType visibleType) {
            if (visibleType == null) {
                return unidentifiedIcon;
            }
            return unitIcons.getOrDefault(visibleType, unidentifiedIcon);
        }

        private void drawBlock(Batch batch, float x, float y, float width, float height) {
            batch.draw(pixel, x, y, width, height);
        }
    }
}
