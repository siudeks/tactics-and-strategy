package game.platform;

import com.badlogic.gdx.Game;
import game.screens.MainMenuScreen;

public class TacticsGame extends Game {
    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }
}
