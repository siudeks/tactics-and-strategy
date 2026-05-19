package pl.tactics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class BattlefieldScreen extends ScreenAdapter {
    private static final Color BG = Color.valueOf("1E232B");
    private static final Color MAP_BG = Color.valueOf("6A7A63");
    private static final Color PANEL_BG = Color.valueOf("2C3038");
    private static final Color STATUS_BG = Color.valueOf("1F242B");
    private static final Color GRID = Color.valueOf("4E5D4A");
    private static final Color UNIT = Color.valueOf("D87C5D");

    private Stage stage;
    private BitmapFont font;
    private Texture whiteTexture;

    @Override
    public void show() {
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

        Table topArea = new Table();
        topArea.add(new MapPanel(whiteTexture)).grow().pad(8f);
        topArea.add(createCommandPanel(labelStyle, buttonStyle, base.tint(PANEL_BG))).width(300f).growY().pad(8f, 0f, 8f, 8f);

        Label status = new Label("Status: Jednostka Alpha gotowa | Paliwo 100% | Łączność OK", labelStyle);
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
                                     Drawable background) {
        Table panel = new Table();
        panel.setBackground(background);
        panel.defaults().growX().pad(8f);

        panel.add(new Label("Panel rozkazow", labelStyle)).left().padTop(10f).row();
        panel.add(new Label("Jednostka: Alpha", labelStyle)).left().row();
        panel.add(new TextButton("Ruch", buttonStyle)).row();
        panel.add(new TextButton("Atak", buttonStyle)).row();
        panel.add(new TextButton("Obrona", buttonStyle)).row();
        panel.add(new TextButton("Patrol", buttonStyle)).row();
        panel.add().growY().row();

        return panel;
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
        private final Texture pixel;

        private MapPanel(Texture pixel) {
            this.pixel = pixel;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            float x = getX();
            float y = getY();
            float w = getWidth();
            float h = getHeight();

            batch.setColor(MAP_BG);
            batch.draw(pixel, x, y, w, h);

            float cell = Math.max(24f, Math.min(w, h) / 16f);
            batch.setColor(GRID);
            for (float xx = x; xx <= x + w; xx += cell) {
                batch.draw(pixel, xx, y, 1f, h);
            }
            for (float yy = y; yy <= y + h; yy += cell) {
                batch.draw(pixel, x, yy, w, 1f);
            }

            float unitSize = 18f;
            batch.setColor(UNIT);
            batch.draw(pixel, x + w * 0.45f, y + h * 0.58f, unitSize, unitSize);
            batch.draw(pixel, x + w * 0.64f, y + h * 0.38f, unitSize, unitSize);

            batch.setColor(Color.WHITE);
        }
    }
}
