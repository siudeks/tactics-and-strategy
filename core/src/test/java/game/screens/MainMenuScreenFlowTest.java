package game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import game.platform.ScenarioLoader;
import game.scenario.LoadedScenario;
import game.scenario.ScenarioEntry;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MainMenuScreenFlowTest {

    @Test
    void menuMusicSamples_returnsNonEmptyPcmData() {
        var samples = MainMenuScreen.menuMusicSamples(22050);

        assertNotNull(samples);
        assertTrue(samples.length > 0);
    }

    @Test
    void menuMusicWavBytes_containsWaveHeader() {
        var wavBytes = MainMenuScreen.menuMusicWavBytes(22050);

        assertNotNull(wavBytes);
        assertTrue(wavBytes.length > 44);
        assertEquals('R', wavBytes[0]);
        assertEquals('I', wavBytes[1]);
        assertEquals('F', wavBytes[2]);
        assertEquals('F', wavBytes[3]);
        assertEquals('W', wavBytes[8]);
        assertEquals('A', wavBytes[9]);
        assertEquals('V', wavBytes[10]);
        assertEquals('E', wavBytes[11]);
    }

    @Test
    void launchSelected_routesThroughKeyboardShortcutsScreenBeforeBattlefield() {
        var game = new RecordingGame();
        var menuScreen = new MainMenuScreen(game);
        var entries = ScenarioLoader.listAvailableScenarios();

        injectEntries(menuScreen, entries);

        invokeLaunchSelected(menuScreen);

        var shortcutsScreen = assertInstanceOf(ScenarioKeyboardShortcutsScreen.class, game.capturedScreen);
        var loadedScenarioFromShortcuts = shortcutsScreen.loadedScenario();

        assertEquals(
            entries.getFirst().name(),
            loadedScenarioFromShortcuts.scenarioDefinition().name()
        );
        assertEquals(entries.getFirst().resourcePath(), scenarioResourcePathFor(loadedScenarioFromShortcuts.scenarioDefinition().id()));

        assertTrue(shortcutsScreen.handleKeyDown(com.badlogic.gdx.Input.Keys.ENTER));

        var battlefieldScreen = assertInstanceOf(BattlefieldScreen.class, game.capturedScreen);
        var loadedScenario = extractLoadedScenario(battlefieldScreen);

        assertEquals(
            entries.getFirst().name(),
            loadedScenario.scenarioDefinition().name()
        );
        assertEquals(entries.getFirst().resourcePath(), scenarioResourcePathFor(loadedScenario.scenarioDefinition().id()));
    }

    @Test
    void shortcutsScreen_escapeKey_startsBattlefieldWithSameScenario() {
        var game = new RecordingGame();
        var scenario = ScenarioLoader.loadFromResource(ScenarioLoader.listAvailableScenarios().getFirst().resourcePath());
        var shortcutsScreen = new ScenarioKeyboardShortcutsScreen(game, scenario);

        assertTrue(shortcutsScreen.handleKeyDown(com.badlogic.gdx.Input.Keys.ESCAPE));

        var battlefieldScreen = assertInstanceOf(BattlefieldScreen.class, game.capturedScreen);
        var loadedScenario = extractLoadedScenario(battlefieldScreen);

        assertEquals(scenario.scenarioDefinition().id(), loadedScenario.scenarioDefinition().id());
    }

    private static void invokeLaunchSelected(MainMenuScreen menuScreen) {
        try {
            var launchSelected = MainMenuScreen.class.getDeclaredMethod("launchSelected");
            launchSelected.setAccessible(true);
            launchSelected.invoke(menuScreen);
        } catch (NoSuchMethodException | IllegalAccessException exception) {
            fail(exception);
        } catch (InvocationTargetException exception) {
            var cause = exception.getCause();
            fail(cause != null ? cause : exception);
        }
    }

    private static void injectEntries(MainMenuScreen menuScreen, List<ScenarioEntry> entries) {
        try {
            var entriesField = MainMenuScreen.class.getDeclaredField("entries");
            entriesField.setAccessible(true);
            entriesField.set(menuScreen, entries);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            fail(exception);
        }
    }

    private static LoadedScenario extractLoadedScenario(BattlefieldScreen battlefieldScreen) {
        try {
            var loadedScenarioField = BattlefieldScreen.class.getDeclaredField("loadedScenario");
            loadedScenarioField.setAccessible(true);
            return (LoadedScenario) loadedScenarioField.get(battlefieldScreen);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            fail(exception);
            throw new IllegalStateException(exception);
        }
    }

    private static String scenarioResourcePathFor(String scenarioId) {
        return ScenarioLoader.listAvailableScenarios().stream()
            .filter(entry -> entry.resourcePath().contains(scenarioId))
            .findFirst()
            .orElseThrow()
            .resourcePath();
    }

    private static final class RecordingGame extends Game {
        private @Nullable Screen capturedScreen;

        @Override
        public void create() {
        }

        @Override
        public void setScreen(Screen screen) {
            this.capturedScreen = screen;
        }
    }
}