package game.screens;

import com.badlogic.gdx.Application;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class AsyncAudio {

    private static final ExecutorService AUDIO_IO_EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
        var thread = new Thread(runnable, "audio-io");
        thread.setDaemon(true);
        return thread;
    });

    private AsyncAudio() {
    }

    static void runOnAudioIoThread(Runnable action) {
        AUDIO_IO_EXECUTOR.execute(action);
    }

    static void runOnAppThread(@Nullable Application application, Runnable action) {
        if (application != null) {
            application.postRunnable(action);
            return;
        }
        action.run();
    }
}