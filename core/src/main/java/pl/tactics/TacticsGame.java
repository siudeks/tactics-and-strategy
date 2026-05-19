package pl.tactics;

import com.badlogic.gdx.Game;
import pl.tactics.screens.BattlefieldScreen;

public class TacticsGame extends Game {
    @Override
    public void create() {
        setScreen(new BattlefieldScreen());
    }
}
