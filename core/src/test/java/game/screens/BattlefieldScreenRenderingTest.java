package game.screens;

import org.junit.jupiter.api.Test;
import game.domain.CampaignState;
import game.domain.Order;
import game.domain.OrderType;
import game.domain.ScenarioDefinition;
import game.domain.Side;
import game.domain.TerrainType;
import game.domain.Unit;
import game.domain.UnitSize;
import game.domain.UnitType;
import game.engine.GameRuntime;
import game.scenario.LoadedScenario;
import game.scenario.ScenarioLoader;

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
            0f,
            1f
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
            16f,
            1f
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
            0f,
            1f
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
            0f,
            1f
        ).getFirst();

        assertAll(
            () -> assertPlacement(initialPlacement, 16f, 112f, Side.ALLIES),
            () -> assertPlacement(updatedPlacement, 48f, 64f, Side.ALLIES),
            () -> assertEquals("moving-unit", updatedPlacement.unit().id())
        );
    }

    @Test
    void computeVisibleUnitPlacements_scalesCoordinatesAndSizeForZoom() {
        CampaignState campaignState = new CampaignState(
            "test-campaign",
            "test-scenario",
            1,
            Side.ALLIES,
            List.of(unit("zoomed", Side.ALLIES, 2, 3)),
            List.of()
        );

        BattlefieldScreen.UnitRenderPlacement placement = BattlefieldScreen.computeVisibleUnitPlacements(
            campaignState,
            10,
            10f,
            20f,
            300f,
            300f,
            16f,
            32f,
            2f
        ).getFirst();

        assertAll(
            () -> assertEquals(42f, placement.screenX()),
            () -> assertEquals(116f, placement.screenY()),
            () -> assertEquals(64f, placement.drawSize())
        );
    }

    @Test
    void clampZoomLevel_clampsToBounds() {
        assertAll(
            () -> assertEquals(0.5f, BattlefieldScreen.clampZoomLevel(0.2f, 0.5f, 3f)),
            () -> assertEquals(3f, BattlefieldScreen.clampZoomLevel(4f, 0.5f, 3f)),
            () -> assertEquals(1.2f, BattlefieldScreen.clampZoomLevel(1.2f, 0.5f, 3f))
        );
    }

    @Test
    void zoomStepFactor_matchesScrollDirection() {
        assertAll(
            () -> assertEquals(1f, BattlefieldScreen.zoomStepFactor(0f, 0.1f), 0.0001f),
            () -> assertEquals(1f / 1.1f, BattlefieldScreen.zoomStepFactor(1f, 0.1f), 0.0001f),
            () -> assertEquals(1.1f, BattlefieldScreen.zoomStepFactor(-1f, 0.1f), 0.0001f)
        );
    }

    @Test
    void cameraAfterZoom_keepsWorldPointUnderCursorStable() {
        float cameraAfterZoom = BattlefieldScreen.cameraAfterZoom(40f, 100f, 1f, 2f);
        float worldBefore = 40f + 100f / 1f;
        float worldAfter = cameraAfterZoom + 100f / 2f;

        assertEquals(worldBefore, worldAfter, 0.0001f);
    }

    @Test
    void isViewportReadyForCameraCentering_requiresPositivePanelSize() {
        assertAll(
            () -> assertEquals(false, BattlefieldScreen.isViewportReadyForCameraCentering(0f, 300f)),
            () -> assertEquals(false, BattlefieldScreen.isViewportReadyForCameraCentering(300f, 0f)),
            () -> assertEquals(false, BattlefieldScreen.isViewportReadyForCameraCentering(-1f, 300f)),
            () -> assertEquals(true, BattlefieldScreen.isViewportReadyForCameraCentering(300f, 200f))
        );
    }

    @Test
    void isUnitFullyVisibleInViewport_returnsTrue_whenUnitFitsInWorldViewport() {
        Unit unit = unit("visible", Side.ALLIES, 2, 3);

        boolean fullyVisible = BattlefieldScreen.isUnitFullyVisibleInViewport(
            unit,
            10,
            0f,
            0f,
            300f,
            300f,
            1f
        );

        assertEquals(true, fullyVisible);
    }

    @Test
    void isUnitFullyVisibleInViewport_returnsFalse_whenUnitExceedsViewportEdge() {
        Unit unit = unit("edge", Side.ALLIES, 0, 8);

        boolean fullyVisible = BattlefieldScreen.isUnitFullyVisibleInViewport(
            unit,
            10,
            5f,
            5f,
            20f,
            20f,
            1f
        );

        assertEquals(false, fullyVisible);
    }

    @Test
    void centeredCameraPosition_centersUnitAndClampsToMapBounds() {
        assertAll(
            () -> assertEquals(74f, BattlefieldScreen.centeredCameraPosition(124f, 100f, 1f, 300f)),
            () -> assertEquals(0f, BattlefieldScreen.centeredCameraPosition(20f, 100f, 1f, 300f)),
            () -> assertEquals(200f, BattlefieldScreen.centeredCameraPosition(280f, 100f, 1f, 300f))
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
        return new Unit(id, side, UnitType.MEDIUM_TANK, UnitSize.BATTALION, tileX, tileY);
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