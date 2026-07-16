package game;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import game.platform.ScenarioLoader;
import game.scenario.ScenarioEntry;
import game.screens.MainMenuScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainMenuScenarioListingSteps {

    private List<ScenarioEntry> availableScenarios = List.of();
    private List<String> displayedScenarioLines = List.of();

    @Given("the main menu scenario list is available")
    public void theMainMenuScenarioListIsAvailable() {
        availableScenarios = ScenarioLoader.listAvailableScenarios();
    }

    @When("I inspect scenario entries displayed in the main menu")
    public void iInspectScenarioEntriesDisplayedInTheMainMenu() {
        var lines = new ArrayList<String>(availableScenarios.size());
        for (int i = 0; i < availableScenarios.size(); i++) {
            lines.add(MainMenuScreen.menuScenarioLine(i, availableScenarios.get(i)));
        }
        displayedScenarioLines = List.copyOf(lines);
    }

    @Then("all available scenarios should be shown in order")
    public void allAvailableScenariosShouldBeShownInOrder() {
        var expectedLines = new ArrayList<String>(availableScenarios.size());
        for (int i = 0; i < availableScenarios.size(); i++) {
            var entry = availableScenarios.get(i);
            expectedLines.add((i + 1) + ") " + entry.name().toUpperCase(Locale.ROOT));
        }

        assertEquals(expectedLines, displayedScenarioLines);
    }
}