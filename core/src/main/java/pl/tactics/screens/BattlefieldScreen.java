package pl.tactics.screens;

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
import pl.tactics.domain.CampaignState;
import pl.tactics.domain.Side;
import pl.tactics.domain.Unit;
import pl.tactics.engine.GameRuntime;
import pl.tactics.scenario.LoadedScenario;
import pl.tactics.terrain.TerrainMapDefinition;
import pl.tactics.terrain.TerrainTileAtlas;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class BattlefieldScreen extends ScreenAdapter {

    public interface PaletteSurface {
        void setPaletteMode(TerrainMapDefinition.PaletteMode mode);
    }
    private static final Color BG = Color.valueOf("1E232B");
    private static final Color MAP_BG = Color.valueOf("181814");
    private static final Color PANEL_BG = Color.valueOf("2C3038");
    private static final Color STATUS_BG = Color.valueOf("1F242B");
    private static final Color GRID = Color.valueOf("4E5D4A");
    private static final Color ALLIES_UNIT_FILL = Color.valueOf("E5D44E");
    private static final Color ALLIES_UNIT_OUTLINE = Color.valueOf("243B8F");
    private static final Color AXIS_UNIT_FILL = Color.valueOf("C97B3E");
    private static final Color AXIS_UNIT_OUTLINE = Color.valueOf("4A2210");

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
            loadedScenario.scenarioDefinition().mapHeight()
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

        TextButton paletteButton = new TextButton("Paleta: " + mapPanel.getPaletteLabel(), buttonStyle);
        paletteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapPanel.togglePalette();
                gameRuntime.togglePaletteMode();
                paletteButton.setText("Paleta: " + mapPanel.getPaletteLabel());
            }
        });

        panel.add(new Label("Panel rozkazow", labelStyle)).left().padTop(10f).row();
        unitNameLabel = new Label("", labelStyle);
        unitInfoSection = new Table();
        unitInfoSection.add(unitNameLabel).left();
        unitInfoSection.setVisible(false);
        panel.add(unitInfoSection).growX().left().row();
        panel.add(paletteButton).row();
        panel.add(new TextButton("Ruch", buttonStyle)).row();
        panel.add(new TextButton("Atak", buttonStyle)).row();
        panel.add(new TextButton("Obrona", buttonStyle)).row();
        panel.add(new TextButton("Patrol", buttonStyle)).row();
        panel.add().growY().row();

        TextButton endTurnButton = new TextButton("Zakoncz ture  [Enter]", buttonStyle);
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

    public String runtimePaletteLabel() {
        return gameRuntime.getPaletteMode() == TerrainMapDefinition.PaletteMode.ORIGINAL
            ? "Oryginalna" : "Poprawiona";
    }

    public void togglePaletteThroughRuntime() {
        gameRuntime.togglePaletteMode();
        mapPanel.setPaletteMode(gameRuntime.getPaletteMode());
    }

    public void attachRuntimeBridgeForPaletteSurface(PaletteSurface surface) {
        surface.setPaletteMode(gameRuntime.getPaletteMode());
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

        private boolean debugGridOverlay;
        private String selectedUnitId;
        private float cameraX;
        private float cameraY;
        private float zoomLevel;
        private float lastDragX;
        private float lastDragY;

        private final int mapWidthTiles;
        private final int mapHeightTiles;
        private final float mapWorldWidth;
        private final float mapWorldHeight;

        private MapPanel(Texture pixel,
                         Runnable onEndTurn,
                         Supplier<CampaignState> campaignStateSupplier,
                         int scenarioMapHeightTiles) {
            this.pixel = pixel;
            this.onEndTurn = onEndTurn;
            this.campaignStateSupplier = Objects.requireNonNull(campaignStateSupplier, "campaignStateSupplier must not be null");
            this.scenarioMapHeightTiles = scenarioMapHeightTiles;
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
                    selectedUnitId = unitIdAtScreenPoint(placements, sx, sy, state.activeSide());
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
                    if (keycode == com.badlogic.gdx.Input.Keys.P) {
                        togglePalette();
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
                        selectedUnitId = null;
                        return true;
                    }
                    return false;
                }
            });
        }

        public String getPaletteLabel() {
            if (mapDefinition.getPaletteMode() == TerrainMapDefinition.PaletteMode.ORIGINAL) {
                return "Oryginalna";
            }
            return "Poprawiona";
        }

        public void togglePalette() {
            TerrainMapDefinition.PaletteMode currentMode = mapDefinition.getPaletteMode();
            TerrainMapDefinition.PaletteMode nextMode = currentMode == TerrainMapDefinition.PaletteMode.ORIGINAL
                ? TerrainMapDefinition.PaletteMode.IMPROVED
                : TerrainMapDefinition.PaletteMode.ORIGINAL;
            mapDefinition.setPaletteMode(nextMode);
            tileAtlas.rebuild(mapDefinition);
        }

        public void setPaletteMode(TerrainMapDefinition.PaletteMode mode) {
            mapDefinition.setPaletteMode(mode);
            tileAtlas.rebuild(mapDefinition);
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
            List<UnitRenderPlacement> placements = computeVisibleUnitPlacements(
                campaignStateSupplier.get(),
                scenarioMapHeightTiles,
                panelX,
                panelY,
                panelWidth,
                panelHeight,
                cameraX,
                cameraY,
                zoomLevel
            );
            for (UnitRenderPlacement placement : placements) {
                if (placement.unit().id().equals(selectedUnitId)) {
                    float border = 2f;
                    batch.setColor(Color.WHITE);
                    drawBlock(batch, placement.screenX() - border, placement.screenY() - border,
                        placement.drawSize() + border * 2, border);
                    drawBlock(batch, placement.screenX() - border, placement.screenY() + placement.drawSize(),
                        placement.drawSize() + border * 2, border);
                    drawBlock(batch, placement.screenX() - border, placement.screenY() - border,
                        border, placement.drawSize() + border * 2);
                    drawBlock(batch, placement.screenX() + placement.drawSize(), placement.screenY() - border,
                        border, placement.drawSize() + border * 2);
                }
                UnitIconPalette palette = paletteFor(placement.unit().side());
                drawUnitIcon(batch, placement.screenX(), placement.screenY(), placement.drawSize(), palette.fill(), palette.outline());
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

        void resetSelection() {
            CampaignState state = campaignStateSupplier.get();
            List<Unit> active = state.units().stream()
                .filter(u -> u.side() == state.activeSide())
                .toList();
            selectedUnitId = active.isEmpty() ? null : active.getFirst().id();
        }

        private void cycleSelectedUnit() {
            CampaignState state = campaignStateSupplier.get();
            List<Unit> active = state.units().stream()
                .filter(u -> u.side() == state.activeSide())
                .toList();
            selectedUnitId = nextSelectedUnitId(active, selectedUnitId);
        }

        public void dispose() {
            tileAtlas.dispose();
        }

        private void drawUnitIcon(Batch batch, float x, float y, float size, Color fillColor, Color outlineColor) {
            float pixel = size / 16f;
            // 16x16 grid, 0=border, 1=fill, 2=diagonal, 3=side square, 4=center
            int[][] pattern = {
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,2,1,1,1,1,1,1,1,1,1,1,1,1,2,0},
                {0,1,2,1,1,1,1,3,3,1,1,1,1,2,1,0},
                {0,1,1,2,1,1,1,3,3,1,1,1,2,1,1,0},
                {0,1,1,1,2,1,1,1,1,1,1,2,1,1,1,0},
                {0,1,1,1,1,2,1,1,1,1,2,1,1,1,1,0},
                {0,1,1,1,1,1,2,1,1,2,1,1,1,1,1,0},
                {0,1,3,3,1,1,1,3,3,1,1,1,3,3,1,0},
                {0,1,3,3,1,1,1,3,3,1,1,1,3,3,1,0},
                {0,1,1,1,1,1,3,1,1,2,1,1,1,1,1,0},
                {0,1,1,1,1,2,1,1,1,1,2,1,1,1,1,0},
                {0,1,1,1,2,1,1,1,1,1,1,2,1,1,1,0},
                {0,1,1,2,1,1,1,3,3,1,1,1,2,1,1,0},
                {0,1,2,1,1,1,1,3,3,1,1,1,1,2,1,0},
                {0,2,1,1,1,1,1,1,1,1,1,1,1,1,2,0},
                {0,0,0,0,0,0,0,4,4,0,0,0,0,0,0,0},
            };
            for (int gy = 0; gy < 16; gy++) {
                for (int gx = 0; gx < 16; gx++) {
                    int v = pattern[gy][gx];
                    if (v == 0) {
                        batch.setColor(outlineColor);
                        drawCell(batch, x, y, pixel, gx, gy);
                    } else if (v == 1) {
                        batch.setColor(fillColor);
                        drawCell(batch, x, y, pixel, gx, gy);
                    } else if (v == 2) {
                        batch.setColor(outlineColor);
                        drawCell(batch, x, y, pixel, gx, gy);
                    } else if (v == 3) {
                        batch.setColor(outlineColor);
                        drawCell(batch, x, y, pixel, gx, gy);
                    } else if (v == 4) {
                        batch.setColor(outlineColor);
                        drawCell(batch, x, y, pixel, gx, gy);
                    }
                }
            }
        }

        private void drawBlock(Batch batch, float x, float y, float width, float height) {
            batch.draw(pixel, x, y, width, height);
        }

        private void drawCell(Batch batch, float baseX, float baseY, float pixelSize, int gridX, int gridY) {
            drawBlock(batch, baseX + gridX * pixelSize, baseY + gridY * pixelSize, pixelSize, pixelSize);
        }
    }
}
