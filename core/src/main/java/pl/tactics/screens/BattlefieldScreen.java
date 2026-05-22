package pl.tactics.screens;

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
import pl.tactics.engine.GameRuntime;
import pl.tactics.scenario.ScenarioLoader;
import pl.tactics.terrain.TerrainMapDefinition;
import pl.tactics.terrain.TerrainTileAtlas;

public class BattlefieldScreen extends ScreenAdapter {

    public interface PaletteSurface {
        void setPaletteMode(TerrainMapDefinition.PaletteMode mode);
    }
    private static final Color BG = Color.valueOf("1E232B");
    private static final Color MAP_BG = Color.valueOf("181814");
    private static final Color PANEL_BG = Color.valueOf("2C3038");
    private static final Color STATUS_BG = Color.valueOf("1F242B");
    private static final Color GRID = Color.valueOf("4E5D4A");
    private static final Color UNIT_FILL = Color.valueOf("E5D44E");
    private static final Color UNIT_OUTLINE = Color.valueOf("243B8F");

    private Stage stage;
    private BitmapFont font;
    private Texture whiteTexture;
    private MapPanel mapPanel;
    private GameRuntime gameRuntime;

    @Override
    public void show() {
        gameRuntime = new GameRuntime(ScenarioLoader.loadBootstrapScenario());
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

        mapPanel = new MapPanel(whiteTexture);

        Table topArea = new Table();
        topArea.add(mapPanel).grow().pad(8f);
        topArea.add(createCommandPanel(labelStyle, buttonStyle, base.tint(PANEL_BG), mapPanel)).width(300f).growY().pad(8f, 0f, 8f, 8f);

        Label status = new Label(runtimeStatusSummary(), labelStyle);
        status.setAlignment(1);
        Table statusBar = new Table();
        statusBar.setBackground(base.tint(STATUS_BG));
        statusBar.add(status).left().padLeft(12f);

        root.add(topArea).grow().row();
        root.add(statusBar).growX().height(42f);

        stage.addActor(root);
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
        panel.add(new Label("Jednostka: Alpha", labelStyle)).left().row();
        panel.add(paletteButton).row();
        panel.add(new TextButton("Ruch", buttonStyle)).row();
        panel.add(new TextButton("Atak", buttonStyle)).row();
        panel.add(new TextButton("Obrona", buttonStyle)).row();
        panel.add(new TextButton("Patrol", buttonStyle)).row();
        panel.add().growY().row();

        return panel;
    }

    public String runtimeStatusSummary() {
        return String.format("Scenariusz: %s | Tura: %d | Strona aktywna: %s",
            "desert-rats-bootstrap",
            gameRuntime.getTurnNumber(),
            gameRuntime.getActiveSideCode());
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

    private static final class MapPanel extends Actor {
        private static final float DRAW_TILE_SIZE = 16f;
        private static final float UNIT_SIZE_IN_TILES = 2f;

        private final Texture pixel;
        private final TerrainMapDefinition mapDefinition;
        private final TerrainTileAtlas tileAtlas;

        private boolean debugGridOverlay;
        private float cameraX;
        private float cameraY;
        private float lastDragX;
        private float lastDragY;

        private final int mapWidthTiles;
        private final int mapHeightTiles;
        private final float mapWorldWidth;
        private final float mapWorldHeight;

        private MapPanel(Texture pixel) {
            this.pixel = pixel;
            this.mapDefinition = new TerrainMapDefinition();
            this.tileAtlas = new TerrainTileAtlas(mapDefinition);
            this.mapWidthTiles = mapDefinition.getWidthTiles();
            this.mapHeightTiles = mapDefinition.getHeightTiles();
            this.mapWorldWidth = mapWidthTiles * DRAW_TILE_SIZE;
            this.mapWorldHeight = mapHeightTiles * DRAW_TILE_SIZE;

            setTouchable(Touchable.enabled);
            this.debugGridOverlay = false;
            addInputHandling();
        }

        private void addInputHandling() {
            addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    lastDragX = x;
                    lastDragY = y;
                    if (getStage() != null) {
                        getStage().setKeyboardFocus(MapPanel.this);
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
                public boolean keyDown(InputEvent event, int keycode) {
                    if (keycode == com.badlogic.gdx.Input.Keys.G) {
                        debugGridOverlay = !debugGridOverlay;
                        return true;
                    }
                    if (keycode == com.badlogic.gdx.Input.Keys.P) {
                        togglePalette();
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

            drawUnit(batch, x, y);

            batch.setColor(Color.WHITE);
        }

        private void drawTerrain(Batch batch, float panelX, float panelY, float panelW, float panelH) {
            int startCol = MathUtils.clamp((int) Math.floor(cameraX / DRAW_TILE_SIZE), 0, mapWidthTiles - 1);
            int endCol = MathUtils.clamp((int) Math.ceil((cameraX + panelW) / DRAW_TILE_SIZE), 0, mapWidthTiles - 1);

            batch.setColor(Color.WHITE);
            for (int rowTop = 0; rowTop < mapHeightTiles; rowTop++) {
                float worldY = (mapHeightTiles - rowTop - 1) * DRAW_TILE_SIZE;
                float screenY = panelY + worldY - cameraY;
                if (screenY + DRAW_TILE_SIZE < panelY || screenY > panelY + panelH) {
                    continue;
                }

                for (int col = startCol; col <= endCol; col++) {
                    float worldX = col * DRAW_TILE_SIZE;
                    float screenX = panelX + worldX - cameraX;

                    int tileId = mapDefinition.getMapTileId(col, rowTop);
                    batch.draw(tileAtlas.getRegion(tileId), screenX, screenY, DRAW_TILE_SIZE, DRAW_TILE_SIZE);
                }
            }
        }

        private void drawDebugGrid(Batch batch, float panelX, float panelY, float panelW, float panelH) {
            batch.setColor(GRID);

            int startCol = MathUtils.clamp((int) Math.floor(cameraX / DRAW_TILE_SIZE), 0, mapWidthTiles - 1);
            int endCol = MathUtils.clamp((int) Math.ceil((cameraX + panelW) / DRAW_TILE_SIZE), 0, mapWidthTiles);
            for (int col = startCol; col <= endCol; col++) {
                float lineX = panelX + col * DRAW_TILE_SIZE - cameraX;
                batch.draw(pixel, lineX, panelY, 1f, panelH);
            }

            for (int rowTop = 0; rowTop <= mapHeightTiles; rowTop++) {
                float worldY = (mapHeightTiles - rowTop) * DRAW_TILE_SIZE;
                float lineY = panelY + worldY - cameraY;
                if (lineY < panelY || lineY > panelY + panelH) {
                    continue;
                }
                batch.draw(pixel, panelX, lineY, panelW, 1f);
            }
        }

        private void drawUnit(Batch batch, float panelX, float panelY) {
            float unitWorldX = mapWorldWidth * 0.5f;
            float unitWorldY = mapWorldHeight * 0.5f;
            float unitDrawSize = DRAW_TILE_SIZE * UNIT_SIZE_IN_TILES;
            float unitX = panelX + unitWorldX - cameraX;
            float unitY = panelY + unitWorldY - cameraY;

            drawUnitIcon(batch, unitX, unitY, unitDrawSize);
        }

        private void clampCamera() {
            float maxCameraX = Math.max(0f, mapWorldWidth - getWidth());
            float maxCameraY = Math.max(0f, mapWorldHeight - getHeight());
            cameraX = MathUtils.clamp(cameraX, 0f, maxCameraX);
            cameraY = MathUtils.clamp(cameraY, 0f, maxCameraY);
        }

        public void dispose() {
            tileAtlas.dispose();
        }

        private void drawUnitIcon(Batch batch, float x, float y, float size) {
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
                        batch.setColor(UNIT_OUTLINE);
                        drawCell(batch, x, y, pixel, gx, gy);
                    } else if (v == 1) {
                        batch.setColor(UNIT_FILL);
                        drawCell(batch, x, y, pixel, gx, gy);
                    } else if (v == 2) {
                        batch.setColor(UNIT_OUTLINE);
                        drawCell(batch, x, y, pixel, gx, gy);
                    } else if (v == 3) {
                        batch.setColor(UNIT_OUTLINE);
                        drawCell(batch, x, y, pixel, gx, gy);
                    } else if (v == 4) {
                        batch.setColor(UNIT_OUTLINE);
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
