package game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import game.scenario.ScenarioEntry;
import game.platform.ScenarioLoader;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class MainMenuScreen extends ScreenAdapter {

    private static final Color BG = Color.BLACK;
    private static final Color COLOR_TITLE = Color.WHITE;
    private static final Color COLOR_ITEM = Color.WHITE;
    private static final Color COLOR_HIGHLIGHT = Color.valueOf("FFFF00");
    private static final Color COLOR_SUBTITLE = Color.valueOf("AAAAAA");

    private static final float MARGIN_LEFT = 32f;
    private static final float LINE_HEIGHT = 20f;
    private static final int MENU_MUSIC_SAMPLE_RATE_HZ = 22050;
    private static final float MENU_MUSIC_VOLUME = 0.11f;
    private static final byte[] MENU_MUSIC_WAV_BYTES = menuMusicWavBytes(MENU_MUSIC_SAMPLE_RATE_HZ);

    private final Game game;

    private SpriteBatch batch;
    private BitmapFont font;
    private List<ScenarioEntry> entries;
    private int selectedIndex = 0;
    private int menuMusicLoadGeneration = 0;
    private @Nullable Music menuMusic;
    private @Nullable Path menuMusicFile;

    @SuppressWarnings("NullAway.Init")
    public MainMenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.5f);

        entries = ScenarioLoader.listAvailableScenarios();
        startMenuMusic();

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
                var digit = keycode - Input.Keys.NUM_1;
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
        var entry = entries.get(selectedIndex);
        stopMenuMusic();
        game.setScreen(new BattlefieldScreen(game, ScenarioLoader.loadFromResource(entry.resourcePath())));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(BG);

        var screenHeight = Gdx.graphics.getHeight();
        var y = screenHeight - 40f;

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
            var line = menuScenarioLine(i, entries.get(i));
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
    public void hide() {
        stopMenuMusic();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stopMenuMusic();
        batch.dispose();
        font.dispose();
    }

    private void startMenuMusic() {
        stopMenuMusic();
        if (Gdx.audio == null) {
            return;
        }

        var requestId = menuMusicLoadGeneration;
        AsyncAudio.runOnAudioIoThread(() -> prepareMenuMusicFile(requestId));
    }

    private void prepareMenuMusicFile(int requestId) {
        try {
            var preparedMusicFile = Files.createTempFile("tactics-and-strategy-menu-music", ".wav");
            Files.write(preparedMusicFile, MENU_MUSIC_WAV_BYTES);
            AsyncAudio.runOnAppThread(Gdx.app,
                () -> initializePreparedMenuMusic(requestId, preparedMusicFile));
        } catch (IOException | RuntimeException exception) {
            AsyncAudio.runOnAppThread(Gdx.app, () -> {
                if (requestId == menuMusicLoadGeneration) {
                    logMenuMusicError("Failed to initialize menu music", exception);
                }
            });
        }
    }

    private void initializePreparedMenuMusic(int requestId, Path preparedMusicFile) {
        if (requestId != menuMusicLoadGeneration) {
            deleteMenuMusicFile(preparedMusicFile);
            return;
        }

        var audio = Gdx.audio;
        if (audio == null) {
            deleteMenuMusicFile(preparedMusicFile);
            return;
        }

        try {
            var fileHandle = Gdx.files.absolute(preparedMusicFile.toString());
            var preparedMusic = audio.newMusic(fileHandle);
            preparedMusic.setLooping(true);
            preparedMusic.setVolume(MENU_MUSIC_VOLUME);
            if (requestId != menuMusicLoadGeneration) {
                preparedMusic.stop();
                preparedMusic.dispose();
                deleteMenuMusicFile(preparedMusicFile);
                return;
            }
            menuMusicFile = preparedMusicFile;
            menuMusic = preparedMusic;
            preparedMusic.play();
        } catch (RuntimeException exception) {
            logMenuMusicError("Failed to initialize menu music", exception);
            deleteMenuMusicFile(preparedMusicFile);
        }
    }

    private void stopMenuMusic() {
        menuMusicLoadGeneration++;
        if (menuMusic != null) {
            menuMusic.stop();
            menuMusic.dispose();
            menuMusic = null;
        }
        if (menuMusicFile != null) {
            deleteMenuMusicFile(menuMusicFile);
            menuMusicFile = null;
        }
    }

    private void deleteMenuMusicFile(Path musicFilePath) {
        try {
            Files.deleteIfExists(musicFilePath);
        } catch (IOException exception) {
            logMenuMusicError("Failed to delete temporary menu music file", exception);
        }
    }

    private void logMenuMusicError(String message, Exception exception) {
        if (Gdx.app != null) {
            Gdx.app.error("MainMenuScreen", message, exception);
        }
    }

    static short[] menuMusicSamples(int sampleRateHz) {
        var noteDurationsMs = new int[] {180, 180, 180, 220, 180, 180, 180, 320};
        var noteFrequenciesHz = new float[] {392f, 493.88f, 587.33f, 659.25f, 587.33f, 493.88f, 440f, 392f};
        var totalSamples = 0;
        for (int noteDurationMs : noteDurationsMs) {
            totalSamples += Math.max(1, sampleRateHz * noteDurationMs / 1000);
        }

        var samples = new short[totalSamples];
        var sampleOffset = 0;
        for (int noteIndex = 0; noteIndex < noteDurationsMs.length; noteIndex++) {
            var noteSampleCount = Math.max(1, sampleRateHz * noteDurationsMs[noteIndex] / 1000);
            var frequencyHz = noteFrequenciesHz[noteIndex];
            for (int i = 0; i < noteSampleCount; i++) {
                var progress = (float) i / (float) noteSampleCount;
                var attack = Math.min(1f, progress * 12f);
                var release = Math.min(1f, (1f - progress) * 12f);
                var envelope = Math.min(attack, release);
                var phase = 2d * Math.PI * frequencyHz * i / sampleRateHz;
                var value = (float) Math.sin(phase) * envelope * MENU_MUSIC_VOLUME;
                samples[sampleOffset + i] = (short) (value * Short.MAX_VALUE);
            }
            sampleOffset += noteSampleCount;
        }
        return samples;
    }

    static byte[] menuMusicWavBytes(int sampleRateHz) {
        var samples = menuMusicSamples(sampleRateHz);
        var dataSize = samples.length * Short.BYTES;

        try (var outputStream = new ByteArrayOutputStream(44 + dataSize)) {
            writeAscii(outputStream, "RIFF");
            writeLittleEndianInt(outputStream, 36 + dataSize);
            writeAscii(outputStream, "WAVE");
            writeAscii(outputStream, "fmt ");
            writeLittleEndianInt(outputStream, 16);
            writeLittleEndianShort(outputStream, (short) 1);
            writeLittleEndianShort(outputStream, (short) 1);
            writeLittleEndianInt(outputStream, sampleRateHz);
            writeLittleEndianInt(outputStream, sampleRateHz * Short.BYTES);
            writeLittleEndianShort(outputStream, (short) Short.BYTES);
            writeLittleEndianShort(outputStream, (short) 16);
            writeAscii(outputStream, "data");
            writeLittleEndianInt(outputStream, dataSize);

            for (short sample : samples) {
                writeLittleEndianShort(outputStream, sample);
            }
            return outputStream.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to build menu music wav bytes", exception);
        }
    }

    private static void writeAscii(ByteArrayOutputStream outputStream, String value) throws IOException {
        outputStream.write(value.getBytes(StandardCharsets.US_ASCII));
    }

    private static void writeLittleEndianInt(ByteArrayOutputStream outputStream, int value) throws IOException {
        outputStream.write(value & 0xFF);
        outputStream.write((value >>> 8) & 0xFF);
        outputStream.write((value >>> 16) & 0xFF);
        outputStream.write((value >>> 24) & 0xFF);
    }

    private static void writeLittleEndianShort(ByteArrayOutputStream outputStream, short value) throws IOException {
        outputStream.write(value & 0xFF);
        outputStream.write((value >>> 8) & 0xFF);
    }

    public static String menuScenarioLine(int zeroBasedIndex, ScenarioEntry entry) {
        return (zeroBasedIndex + 1) + ") " + entry.name().toUpperCase(Locale.ROOT);
    }
}

