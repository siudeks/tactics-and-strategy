package game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import game.scenario.LoadedScenario;
import game.scenario.ScenarioEntry;
import game.scenario.ScenarioLoader;

import java.util.List;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MainMenuScreenFlowTest {

    @Test
    void menuMusicSamples_returnsNonEmptyPcmData() {
        short[] samples = MainMenuScreen.menuMusicSamples(22050);

        assertNotNull(samples);
        assertTrue(samples.length > 0);
    }

    @Test
    void menuMusicWavBytes_containsWaveHeader() {
        byte[] wavBytes = MainMenuScreen.menuMusicWavBytes(22050);

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
    void launchSelected_handsSelectedScenarioToBattlefieldScreen() {
        RecordingGame game = new RecordingGame();
        MainMenuScreen menuScreen = new MainMenuScreen(game);
        List<ScenarioEntry> entries = ScenarioLoader.listAvailableScenarios();

        injectEntries(menuScreen, entries);

        invokeLaunchSelected(menuScreen);

        BattlefieldScreen battlefieldScreen = assertInstanceOf(BattlefieldScreen.class, game.capturedScreen);
        LoadedScenario loadedScenario = extractLoadedScenario(battlefieldScreen);

        assertEquals(
            entries.getFirst().name(),
            loadedScenario.scenarioDefinition().name()
        );
        assertEquals(entries.getFirst().resourcePath(), scenarioResourcePathFor(loadedScenario.scenarioDefinition().id()));
    }

    private static void invokeLaunchSelected(MainMenuScreen menuScreen) {
        try {
            Method launchSelected = MainMenuScreen.class.getDeclaredMethod("launchSelected");
            launchSelected.setAccessible(true);
            launchSelected.invoke(menuScreen);
        } catch (NoSuchMethodException | IllegalAccessException exception) {
            fail(exception);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            fail(cause != null ? cause : exception);
        }
    }

    private static void injectEntries(MainMenuScreen menuScreen, List<ScenarioEntry> entries) {
        try {
            Field entriesField = MainMenuScreen.class.getDeclaredField("entries");
            entriesField.setAccessible(true);
            entriesField.set(menuScreen, entries);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            fail(exception);
        }
    }

    private static LoadedScenario extractLoadedScenario(BattlefieldScreen battlefieldScreen) {
        try {
            Field loadedScenarioField = BattlefieldScreen.class.getDeclaredField("loadedScenario");
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