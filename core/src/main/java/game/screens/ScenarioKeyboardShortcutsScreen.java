package game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import game.scenario.LoadedScenario;

public final class ScenarioKeyboardShortcutsScreen extends ScreenAdapter {

    private static final Color BG = Color.BLACK;
    private static final Color COLOR_TITLE = Color.WHITE;
    private static final Color COLOR_SECTION = Color.valueOf("FFFF00");
    private static final Color COLOR_ITEM = Color.WHITE;
    private static final Color COLOR_HINT = Color.valueOf("AAAAAA");

    private static final float MARGIN_LEFT = 32f;
    private static final float LINE_HEIGHT = 20f;

    private static final String[] BATTLEFIELD_SHORTCUTS = {
        "SPACE         : pauza / wznow zegar gry",
        "L             : zmiana warstwy debug terenu",
        "G             : pokaz/ukryj siatke debug",
        "ENTER         : wykonaj akcje tury",
        "TAB           : wybierz kolejna jednostke",
        "ESC           : wyczysc zaznaczenie jednostki",
        "M             : wlacz/wylacz tryb ruchu",
        "CTRL + SCROLL : przybliz/oddal kamere"
    };

    private final Game game;
    private final LoadedScenario loadedScenario;

    private SpriteBatch batch;
    private BitmapFont font;

    @SuppressWarnings("NullAway.Init")
    ScenarioKeyboardShortcutsScreen(Game game, LoadedScenario loadedScenario) {
        this.game = game;
        this.loadedScenario = loadedScenario;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.5f);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                return handleKeyDown(keycode);
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(BG);

        var screenHeight = Gdx.graphics.getHeight();
        var y = screenHeight - 40f;

        batch.begin();

        font.setColor(COLOR_TITLE);
        font.draw(batch, "PRZED BITWA - SKROTY KLAWIATUROWE", MARGIN_LEFT, y);
        y -= LINE_HEIGHT * 1.3f;

        font.setColor(COLOR_HINT);
        font.draw(batch, "Scenariusz: " + loadedScenario.scenarioDefinition().name(), MARGIN_LEFT, y);
        y -= LINE_HEIGHT * 1.6f;

        font.setColor(COLOR_SECTION);
        font.draw(batch, "POLE BITWY", MARGIN_LEFT, y);
        y -= LINE_HEIGHT;

        y = drawShortcutLines(BATTLEFIELD_SHORTCUTS, y);
        y -= LINE_HEIGHT;

        font.setColor(COLOR_HINT);
        font.draw(batch, "ENTER lub ESC: start bitwy", MARGIN_LEFT, y);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    boolean handleKeyDown(int keycode) {
        if (!isStartBattleKey(keycode)) {
            return false;
        }
        startBattlefield();
        return true;
    }

    static boolean isStartBattleKey(int keycode) {
        return keycode == Input.Keys.ENTER
            || keycode == Input.Keys.NUMPAD_ENTER
            || keycode == Input.Keys.ESCAPE;
    }

    private void startBattlefield() {
        game.setScreen(new BattlefieldScreen(game, loadedScenario));
    }

    LoadedScenario loadedScenario() {
        return loadedScenario;
    }

    private float drawShortcutLines(String[] lines, float y) {
        font.setColor(COLOR_ITEM);
        for (String line : lines) {
            font.draw(batch, line, MARGIN_LEFT, y);
            y -= LINE_HEIGHT;
        }
        return y;
    }
}
