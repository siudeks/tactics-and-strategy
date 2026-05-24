package pl.tactics.engine;

import pl.tactics.domain.CampaignState;
import pl.tactics.scenario.LoadedScenario;

import java.util.Objects;

public final class GameRuntime {

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

    public GameRuntime(LoadedScenario loadedScenario) {
        this.loadedScenario = Objects.requireNonNull(loadedScenario, "loadedScenario must not be null");
        DeterministicContext ctx = DeterministicContext.withSeed(0L);
        this.engine = TurnEngine.fixedContext(ctx, loadedScenario.scenarioDefinition());
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
}
