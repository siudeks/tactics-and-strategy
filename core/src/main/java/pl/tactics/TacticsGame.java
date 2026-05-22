package pl.tactics;

import com.badlogic.gdx.Game;
import pl.tactics.screens.MainMenuScreen;

public class TacticsGame extends Game {
    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }
}
