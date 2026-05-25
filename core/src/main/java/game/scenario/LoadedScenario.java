package game.scenario;

import game.domain.CampaignState;
import game.domain.ScenarioDefinition;
import java.util.Objects;

public record LoadedScenario(
    ScenarioDefinition scenarioDefinition,
    CampaignState campaignState
) {
    public LoadedScenario {
        Objects.requireNonNull(scenarioDefinition, "scenarioDefinition must not be null");
        Objects.requireNonNull(campaignState, "campaignState must not be null");
    }
}
