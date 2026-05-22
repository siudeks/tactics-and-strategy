package pl.tactics.screens;

import org.junit.jupiter.api.Test;
import pl.tactics.domain.CampaignState;
import pl.tactics.domain.Order;
import pl.tactics.domain.OrderType;
import pl.tactics.domain.ScenarioDefinition;
import pl.tactics.domain.Side;
import pl.tactics.domain.TerrainType;
import pl.tactics.domain.Unit;
import pl.tactics.domain.UnitSize;
import pl.tactics.domain.UnitType;
import pl.tactics.engine.GameRuntime;
import pl.tactics.scenario.LoadedScenario;
import pl.tactics.scenario.ScenarioLoader;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BattlefieldScreenRenderingTest {

    @Test
    void computeVisibleUnitPlacements_convertsBootstrapUnitsFromTileToScreenCoordinates() {
        LoadedScenario loadedScenario = ScenarioLoader.loadBootstrapScenario();

        List<BattlefieldScreen.UnitRenderPlacement> placements = BattlefieldScreen.computeVisibleUnitPlacements(
            loadedScenario.campaignState(),
            loadedScenario.scenarioDefinition().mapHeight(),
            0f,
            0f,
            200f,
            200f,
            0f,
            0f
        );

        Map<String, BattlefieldScreen.UnitRenderPlacement> placementsById = placements.stream()
            .collect(Collectors.toMap(placement -> placement.unit().id(), Function.identity()));

        assertEquals(4, placements.size());
        assertAll(
            () -> assertPlacement(placementsById.get("allies-armor-1"), 16f, 112f, Side.ALLIES),
            () -> assertPlacement(placementsById.get("allies-inf-1"), 32f, 112f, Side.ALLIES),
            () -> assertPlacement(placementsById.get("axis-armor-1"), 128f, 0f, Side.AXIS),
            () -> assertPlacement(placementsById.get("axis-inf-1"), 112f, 0f, Side.AXIS)
        );
    }

    @Test
    void computeVisibleUnitPlacements_appliesCameraOffsetsAndCullsUnitsOutsideViewport() {
        CampaignState campaignState = new CampaignState(
            "test-campaign",
            "test-scenario",
            1,
            Side.ALLIES,
            List.of(
                unit("visible", Side.ALLIES, 4, 4),
                unit("culled", Side.AXIS, 0, 0)
            ),
            List.of()
        );

        List<BattlefieldScreen.UnitRenderPlacement> placements = BattlefieldScreen.computeVisibleUnitPlacements(
            campaignState,
            10,
            100f,
            50f,
            80f,
            80f,
            32f,
            16f
        );

        assertEquals(1, placements.size());
        assertPlacement(placements.getFirst(), 132f, 98f, Side.ALLIES);
    }

    @Test
    void computeVisibleUnitPlacements_usesUpdatedRuntimeCampaignStateAfterTurnSimulation() {
        GameRuntime runtime = new GameRuntime(loadedScenarioWithPendingMove("moving-unit", 1, 1, 3, 4));

        BattlefieldScreen.UnitRenderPlacement initialPlacement = BattlefieldScreen.computeVisibleUnitPlacements(
            runtime.getCurrentCampaignState(),
            10,
            0f,
            0f,
            200f,
            200f,
            0f,
            0f
        ).getFirst();

        runtime.simulateOneTurn();

        BattlefieldScreen.UnitRenderPlacement updatedPlacement = BattlefieldScreen.computeVisibleUnitPlacements(
            runtime.getCurrentCampaignState(),
            10,
            0f,
            0f,
            200f,
            200f,
            0f,
            0f
        ).getFirst();

        assertAll(
            () -> assertPlacement(initialPlacement, 16f, 112f, Side.ALLIES),
            () -> assertPlacement(updatedPlacement, 48f, 64f, Side.ALLIES),
            () -> assertEquals("moving-unit", updatedPlacement.unit().id())
        );
    }

    private static void assertPlacement(BattlefieldScreen.UnitRenderPlacement placement,
                                        float expectedX,
                                        float expectedY,
                                        Side expectedSide) {
        assertAll(
            () -> assertEquals(expectedX, placement.screenX()),
            () -> assertEquals(expectedY, placement.screenY()),
            () -> assertEquals(32f, placement.drawSize()),
            () -> assertEquals(expectedSide, placement.unit().side())
        );
    }

    private static Unit unit(String id, Side side, int tileX, int tileY) {
        return new Unit(id, side, UnitType.ARMOR, UnitSize.BATTALION, tileX, tileY);
    }

    private static LoadedScenario loadedScenarioWithPendingMove(String unitId,
                                                                int startTileX,
                                                                int startTileY,
                                                                int targetTileX,
                                                                int targetTileY) {
        Unit unit = unit(unitId, Side.ALLIES, startTileX, startTileY);
        ScenarioDefinition scenarioDefinition = new ScenarioDefinition(
            "render-runtime",
            "Render Runtime",
            10,
            10,
            TerrainType.SAND,
            List.of(unit)
        );
        CampaignState campaignState = new CampaignState(
            "test-campaign",
            "render-runtime",
            1,
            Side.ALLIES,
            List.of(unit),
            List.of(new Order("move-1", unitId, Side.ALLIES, OrderType.MOVE, targetTileX, targetTileY))
        );
        return new LoadedScenario(scenarioDefinition, campaignState);
    }
}