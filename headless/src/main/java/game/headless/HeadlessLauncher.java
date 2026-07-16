package game.headless;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

public class HeadlessLauncher {
    public static void main(String[] args) {
        var config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new ApplicationAdapter() {
            @Override
            public void create() {
                Gdx.app.log("Headless", "Tryb devcontainer uruchomiony poprawnie.");
                Gdx.app.log("Headless", "Aby zobaczyc UI, uruchom: ./gradlew lwjgl3:run");
            }

            @Override
            public void render() {
                Gdx.app.exit();
            }
        }, config);
    }
}
