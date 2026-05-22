package pl.tactics.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import pl.tactics.scenario.ScenarioEntry;
import pl.tactics.scenario.ScenarioLoader;

import java.util.List;

public class MainMenuScreen extends ScreenAdapter {

    private static final Color BG = Color.BLACK;
    private static final Color COLOR_TITLE = Color.WHITE;
    private static final Color COLOR_ITEM = Color.WHITE;
    private static final Color COLOR_HIGHLIGHT = Color.valueOf("FFFF00");
    private static final Color COLOR_SUBTITLE = Color.valueOf("AAAAAA");

    private static final float MARGIN_LEFT = 32f;
    private static final float LINE_HEIGHT = 20f;

    private final Game game;

    private SpriteBatch batch;
    private BitmapFont font;
    private List<ScenarioEntry> entries;
    private int selectedIndex = 0;

    public MainMenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.5f);

        entries = ScenarioLoader.listAvailableScenarios();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.UP) {
                    selectedIndex = Math.max(0, selectedIndex - 1);
                    return true;
                }
                if (keycode == Input.Keys.DOWN) {
                    selectedIndex = Math.min(entries.size() - 1, selectedIndex + 1);
                    return true;
                }
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
                    launchSelected();
                    return true;
                }
                int digit = keycode - Input.Keys.NUM_1;
                if (digit >= 0 && digit < entries.size()) {
                    selectedIndex = digit;
                    launchSelected();
                    return true;
                }
                return false;
            }
        });
    }

    private void launchSelected() {
        ScenarioEntry entry = entries.get(selectedIndex);
        game.setScreen(new BattlefieldScreen(game, ScenarioLoader.loadFromResource(entry.resourcePath())));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(BG);

        float screenHeight = Gdx.graphics.getHeight();
        float y = screenHeight - 40f;

        batch.begin();

        font.setColor(COLOR_TITLE);
        font.draw(batch, "DESERT RATS", MARGIN_LEFT, y);
        y -= LINE_HEIGHT;

        font.setColor(COLOR_SUBTITLE);
        font.draw(batch, "The North Africa Campaign", MARGIN_LEFT, y);
        y -= LINE_HEIGHT * 1.5f;

        font.setColor(COLOR_ITEM);
        font.draw(batch, "SELECT SCENARIO:", MARGIN_LEFT, y);
        y -= LINE_HEIGHT * 1.2f;

        for (int i = 0; i < entries.size(); i++) {
            String line = (i + 1) + ") " + entries.get(i).name().toUpperCase();
            font.setColor(i == selectedIndex ? COLOR_HIGHLIGHT : COLOR_ITEM);
            font.draw(batch, line, MARGIN_LEFT, y);
            y -= LINE_HEIGHT;
        }

        y -= LINE_HEIGHT;
        font.setColor(COLOR_SUBTITLE);
        font.draw(batch, "UP/DOWN: wybor   ENTER: start   1-8: szybki start", MARGIN_LEFT, y);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}

