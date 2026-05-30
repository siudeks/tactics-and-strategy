package game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.AudioDevice;
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
import game.terrain.GeneratedTerrainData;
import game.terrain.TerrainMapDefinition;
import game.terrain.TerrainTileAtlas;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class BattlefieldScreen extends ScreenAdapter {

    private static final Color BG = Color.valueOf("1E232B");
    static final Color MAP_BG = Color.valueOf("181814");
    private static final Color PANEL_BG = Color.valueOf("2C3038");
    private static final Color STATUS_BG = Color.valueOf("1F242B");
    static final Color GRID = Color.valueOf("4E5D4A");
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
    private static final int MOVE_CONFIRM_SOUND_SAMPLE_RATE_HZ = 22050;
    private static final int MOVE_CONFIRM_SOUND_DURATION_MS = 120;
    private static final float MOVE_CONFIRM_SOUND_FREQUENCY_HZ = 880f;
    private static final float MOVE_CONFIRM_SOUND_VOLUME = 0.22f;
    private static final short[] MOVE_CONFIRM_SOUND_SAMPLES = moveConfirmSoundSamples(
        MOVE_CONFIRM_SOUND_SAMPLE_RATE_HZ,
        MOVE_CONFIRM_SOUND_DURATION_MS,
        MOVE_CONFIRM_SOUND_FREQUENCY_HZ
    );

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
    private TextButton moveButton;
    private @Nullable Map<UnitType, Texture> unitIconTextures;
    private @Nullable Texture unidentifiedIconTexture;
    private @Nullable AudioDevice moveConfirmAudioDevice;

    @SuppressWarnings("NullAway.Init")
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
        var icons = Objects.requireNonNull(unitIconTextures, "unitIconTextures must be initialized");
        var unidentifiedIcon = Objects.requireNonNull(unidentifiedIconTexture, "unidentifiedIconTexture must be initialized");

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
            this::playMoveConfirmationSound,
            icons,
            unidentifiedIcon
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
        moveButton = new TextButton("Move", buttonStyle);
        moveButton.setDisabled(true);
        moveButton.setTouchable(Touchable.disabled);
        moveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (moveButton.isDisabled()) {
                    return;
                }
                mapPanel.toggleMoveMode();
                syncMoveButtonState();
            }
        });
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
        syncMoveButtonState();
        if (unitNameLabel != null && unitInfoSection != null) {
            syncUnitInfoPanel(mapPanel.getSelectedUnitId(), new UnitInfoView() {
                @Override
                public void showUnit(String id) {
                    unitNameLabel.setText(id);
                    unitInfoSection.setVisible(true);
                }

                @Override
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
        disposeMoveConfirmationAudio();
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

    static @Nullable UnitType visibleUnitType(Unit unit, Side activeSide) {
        Objects.requireNonNull(unit, "unit must not be null");
        Objects.requireNonNull(activeSide, "activeSide must not be null");
        return unit.side() == activeSide ? unit.type() : null;
    }

    static @Nullable String unitIdAtScreenPoint(List<UnitRenderPlacement> placements,
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

    static @Nullable String nextSelectedUnitId(List<Unit> activeUnits, @Nullable String currentId) {
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

    static @Nullable String nextUnassignedUnitId(List<Unit> activeUnits,
                                                 @Nullable String currentId,
                                                 Map<String, TileCoord> moveTargetsByUnit) {
        Objects.requireNonNull(moveTargetsByUnit, "moveTargetsByUnit must not be null");
        if (activeUnits.isEmpty()) {
            return null;
        }

        int currentIndex = -1;
        for (int i = 0; i < activeUnits.size(); i++) {
            if (activeUnits.get(i).id().equals(currentId)) {
                currentIndex = i;
                break;
            }
        }

        for (int offset = 1; offset <= activeUnits.size(); offset++) {
            int index = (currentIndex + offset + activeUnits.size()) % activeUnits.size();
            String candidateId = activeUnits.get(index).id();
            if (!moveTargetsByUnit.containsKey(candidateId)) {
                return candidateId;
            }
        }

        return null;
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

    static @Nullable TileCoord mapTileAtPanelPoint(float panelPointerX,
                                                   float panelPointerY,
                                                   float cameraX,
                                                   float cameraY,
                                                   float zoomLevel,
                                                   int mapWidthTiles,
                                                   int mapHeightTiles) {
        float worldX = cameraX + panelPointerX / zoomLevel;
        float worldY = cameraY + panelPointerY / zoomLevel;
        int tileX = (int) Math.floor(worldX / MapPanel.DRAW_TILE_SIZE);
        int rowFromBottom = (int) Math.floor(worldY / MapPanel.DRAW_TILE_SIZE);
        int tileY = mapHeightTiles - rowFromBottom - 1;
        if (tileX < 0 || tileY < 0 || tileX >= mapWidthTiles || tileY >= mapHeightTiles) {
            return null;
        }
        return new TileCoord(tileX, tileY);
    }

    static @Nullable MoveTargetAssignment moveTargetAssignmentForClick(boolean moveModeActive,
                                                                       @Nullable String selectedUnitId,
                                                                       @Nullable TileCoord activePreviewTile,
                                                                       @Nullable TileCoord clickedTile,
                                                                       boolean clickedTilePassable) {
        if (!moveModeActive
            || selectedUnitId == null
            || activePreviewTile == null
            || clickedTile == null
            || !clickedTilePassable
            || !clickedTile.equals(activePreviewTile)) {
            return null;
        }
        return new MoveTargetAssignment(selectedUnitId, clickedTile);
    }

    static boolean shouldPlayMoveConfirmationSound(@Nullable MoveTargetAssignment assignment) {
        return assignment != null;
    }

    private static short[] moveConfirmSoundSamples(int sampleRateHz,
                                                   int durationMs,
                                                   float frequencyHz) {
        int sampleCount = Math.max(1, sampleRateHz * durationMs / 1000);
        short[] samples = new short[sampleCount];
        for (int i = 0; i < sampleCount; i++) {
            float progress = (float) i / (float) sampleCount;
            float envelope = 1f - progress;
            double phase = 2d * Math.PI * frequencyHz * i / sampleRateHz;
            float value = (float) Math.sin(phase) * envelope;
            samples[i] = (short) (value * Short.MAX_VALUE);
        }
        return samples;
    }

    private void playMoveConfirmationSound() {
        AudioDevice audioDevice = getMoveConfirmationAudioDevice();
        if (audioDevice == null) {
            return;
        }
        try {
            audioDevice.setVolume(MOVE_CONFIRM_SOUND_VOLUME);
            audioDevice.writeSamples(MOVE_CONFIRM_SOUND_SAMPLES, 0, MOVE_CONFIRM_SOUND_SAMPLES.length);
        } catch (RuntimeException exception) {
            if (Gdx.app != null) {
                Gdx.app.error("BattlefieldScreen", "Failed to play move confirmation sound", exception);
            }
        }
    }

    private @Nullable AudioDevice getMoveConfirmationAudioDevice() {
        if (moveConfirmAudioDevice != null) {
            return moveConfirmAudioDevice;
        }
        if (Gdx.audio == null) {
            return null;
        }
        try {
            moveConfirmAudioDevice = Gdx.audio.newAudioDevice(MOVE_CONFIRM_SOUND_SAMPLE_RATE_HZ, false);
            return moveConfirmAudioDevice;
        } catch (RuntimeException exception) {
            if (Gdx.app != null) {
                Gdx.app.error("BattlefieldScreen", "Failed to initialize move confirmation audio device", exception);
            }
            return null;
        }
    }

    private void disposeMoveConfirmationAudio() {
        if (moveConfirmAudioDevice == null) {
            return;
        }
        moveConfirmAudioDevice.dispose();
        moveConfirmAudioDevice = null;
    }

    static @Nullable TileCoord movePreviewTile(boolean moveModeActive,
                                               @Nullable String selectedUnitId,
                                               @Nullable TileCoord hoveredTile,
                                               boolean hoveredTilePassable) {
        if (!moveModeActive || selectedUnitId == null || hoveredTile == null || !hoveredTilePassable) {
            return null;
        }
        return hoveredTile;
    }

    static boolean isPassableTerrainCode(int terrainCode) {
        return terrainCode != GeneratedTerrainData.TERRAIN_VOID && terrainCode != GeneratedTerrainData.TERRAIN_WATER;
    }

    static boolean shouldConsumeClickInMoveMode(boolean moveModeActive) {
        return moveModeActive;
    }

    interface UnitInfoView {
        void showUnit(String unitId);
        void hide();
    }

    static void syncUnitInfoPanel(@Nullable String selectedUnitId, UnitInfoView view) {
        if (selectedUnitId != null) {
            view.showUnit(selectedUnitId);
        } else {
            view.hide();
        }
    }

    private void syncMoveButtonState() {
        if (moveButton == null || mapPanel == null) {
            return;
        }
        boolean hasSelection = mapPanel.getSelectedUnitId() != null;
        moveButton.setDisabled(!hasSelection);
        moveButton.setTouchable(hasSelection ? Touchable.enabled : Touchable.disabled);
        moveButton.setText(mapPanel.isMoveModeActive() ? "Move (On)" : "Move");
    }

    static record UnitRenderPlacement(Unit unit, float screenX, float screenY, float drawSize) {
        UnitRenderPlacement {
            Objects.requireNonNull(unit, "unit must not be null");
        }
    }

    static record TileCoord(int tileX, int tileY) {
    }

    static record MoveTargetAssignment(String unitId, TileCoord tile) {
        MoveTargetAssignment {
            Objects.requireNonNull(unitId, "unitId must not be null");
            Objects.requireNonNull(tile, "tile must not be null");
        }
    }

    static record UnitIconPalette(Color fill, Color outline) {
        UnitIconPalette {
            Objects.requireNonNull(fill, "fill must not be null");
            Objects.requireNonNull(outline, "outline must not be null");
        }
    }

}
