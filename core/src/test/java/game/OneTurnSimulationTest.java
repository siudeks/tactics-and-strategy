package game;

import org.junit.jupiter.api.Test;
import game.domain.CampaignState;
import game.domain.Order;
import game.domain.OrderType;
import game.domain.ScenarioDefinition;
import game.domain.Side;
import game.domain.UnitId;
import game.domain.TerrainType;
import game.domain.TileCoordinate;
import game.domain.Unit;
import game.domain.UnitSize;
import game.domain.UnitType;
import game.engine.DeterministicContext;
import game.engine.RuntimePhase;
import game.engine.TurnEngine;
import game.engine.TurnPhase;
import game.engine.TurnResult;
import game.platform.ScenarioLoader;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OneTurnSimulationTest {

    private TurnEngine engine() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        return TurnEngine.fixedContext(
            DeterministicContext.withSeed(42L),
            loaded.scenarioDefinition()
        );
    }

    @Test
    void oneTurn_phaseTraceContainsAllFivePhases() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        var result = engine().runOneTurn(loaded.campaignState());

        var expected = List.of(
            TurnPhase.ISSUE_ORDERS,
            TurnPhase.SIMULTANEOUS_MOVE,
            TurnPhase.COMBAT,
            TurnPhase.RETREAT,
            TurnPhase.END_TURN
        );
        assertEquals(expected, result.phaseTrace());
    }

    @Test
    void oneTurn_canonicalSnapshotIsNotEmpty() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        var result = engine().runOneTurn(loaded.campaignState());

        assertNotNull(result.canonicalSnapshot());
        assertFalse(result.canonicalSnapshot().isEmpty());
    }

    @Test
    void oneTurn_resultStateHasSameUnitCount() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        var initialUnitCount = loaded.campaignState().units().size();
        var result = engine().runOneTurn(loaded.campaignState());

        assertEquals(initialUnitCount, result.state().units().size());
    }

    @Test
    void oneTurn_stepwiseSessionMatchesMonolithicRun() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        var startState = loaded.campaignState();
        var engine = engine();

        var session = engine.beginExecution(startState);
        TurnResult stepwiseResult = null;
        while (!session.isComplete()) {
            var stepResult = session.advance();
            assertEquals(stepResult.phase(), RuntimePhase.fromTurnPhase(stepResult.phase().turnPhase()));
            if (stepResult.phase() == RuntimePhase.SIMULTANEOUS_MOVE) {
                assertEquals(startState.units().size(), stepResult.movementPlayback().size());
            }
            if (stepResult.turnCompleted()) {
                stepwiseResult = stepResult.completedTurnResult().orElseThrow();
            }
        }

        var monolithicResult = engine.runOneTurn(startState);
        assertNotNull(stepwiseResult);
        assertTrue(TurnEngine.areSemanticallyEquivalent(stepwiseResult, monolithicResult));
    }

    @Test
    void oneTurn_contestedDestination_isDeterministicAcrossRepeatedRuns() {
        var scenarioDefinition = new ScenarioDefinition(
            "stack-det",
            "Stack Determinism",
            10,
            10,
            TerrainType.SAND,
            List.of()
        );
        var units = List.of(
            new Unit("alpha", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 1, 1),
            new Unit("bravo", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 3, 1),
            new Unit("charlie", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 2, 3)
        );
        var orders = List.of(
            Order.of(UUID.fromString("00000000-0000-0000-0000-0000000000a1"), UnitId.of("alpha"), Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2)),
            Order.of(UUID.fromString("00000000-0000-0000-0000-0000000000b1"), UnitId.of("bravo"), Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2)),
            Order.of(UUID.fromString("00000000-0000-0000-0000-0000000000c1"), UnitId.of("charlie"), Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2))
        );
        var startState = new CampaignState("c1", "stack-det", 1, Side.ALLIES, units, orders);
        var engine = TurnEngine.fixedContext(DeterministicContext.withSeed(42L), scenarioDefinition);

        var baseline = engine.runOneTurn(startState);
        for (int i = 0; i < 20; i++) {
            var next = engine.runOneTurn(startState);
            assertTrue(TurnEngine.areSemanticallyEquivalent(baseline, next));
        }
    }
}
