package pl.tactics.engine;

import pl.tactics.domain.CampaignState;
import pl.tactics.scenario.LoadedScenario;
import pl.tactics.terrain.TerrainMapDefinition;

import java.util.Objects;

public final class GameRuntime {

    /**
     * Bridge interface for UI components to interact with GameRuntime's palette state.
     */
    public interface Bridge {
        TerrainMapDefinition.PaletteMode getPaletteMode();
        void setPaletteMode(TerrainMapDefinition.PaletteMode mode);
    }

    /**
     * Result of simulating one turn.
     */
    public record TurnSimulationResult(TurnResult turnResult) {
        public TurnSimulationResult {
            Objects.requireNonNull(turnResult, "turnResult must not be null");
        }
    }

    private LoadedScenario loadedScenario;
    private final TurnEngine engine;
    private TerrainMapDefinition.PaletteMode currentPaletteMode;

    public GameRuntime(LoadedScenario loadedScenario) {
        this.loadedScenario = Objects.requireNonNull(loadedScenario, "loadedScenario must not be null");
        DeterministicContext ctx = DeterministicContext.withSeed(0L);
        this.engine = TurnEngine.fixedContext(ctx, loadedScenario.scenarioDefinition());
        this.currentPaletteMode = TerrainMapDefinition.PaletteMode.IMPROVED;
    }

    public TurnSimulationResult simulateOneTurn() {
        TurnResult result = engine.runOneTurn(loadedScenario.campaignState());
        // Update internal campaign state to the result state
        loadedScenario = new LoadedScenario(
            loadedScenario.scenarioDefinition(),
            result.state()
        );
        return new TurnSimulationResult(result);
    }

    public int getTurnNumber() {
        return loadedScenario.campaignState().turnNumber();
    }

    public CampaignState getCurrentCampaignState() {
        return loadedScenario.campaignState();
    }

    public String getActiveSideCode() {
        return loadedScenario.campaignState().activeSide().name();
    }

    public TerrainMapDefinition.PaletteMode getPaletteMode() {
        return currentPaletteMode;
    }

    public void setPaletteMode(TerrainMapDefinition.PaletteMode mode) {
        this.currentPaletteMode = Objects.requireNonNull(mode, "mode must not be null");
    }

    public void togglePaletteMode() {
        currentPaletteMode = (currentPaletteMode == TerrainMapDefinition.PaletteMode.ORIGINAL)
            ? TerrainMapDefinition.PaletteMode.IMPROVED
            : TerrainMapDefinition.PaletteMode.ORIGINAL;
    }

    /**
     * Returns this GameRuntime as a Bridge implementation.
     * GameRuntime implements the Bridge contract directly.
     */
    public Bridge asBridge() {
        return new Bridge() {
            @Override
            public TerrainMapDefinition.PaletteMode getPaletteMode() {
                return GameRuntime.this.getPaletteMode();
            }

            @Override
            public void setPaletteMode(TerrainMapDefinition.PaletteMode mode) {
                GameRuntime.this.setPaletteMode(mode);
            }
        };
    }
}
