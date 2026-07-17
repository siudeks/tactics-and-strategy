package game.screens;

import game.domain.CampaignState;
import game.domain.Order;
import game.domain.OrderType;
import game.domain.ScenarioDefinition;
import game.domain.Side;
import game.domain.TerrainType;
import game.domain.TileCoordinate;
import game.domain.Unit;
import game.domain.UnitId;
import game.domain.UnitSize;
import game.domain.UnitType;
import game.engine.GameRuntime;
import game.scenario.LoadedScenario;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattlefieldPhasePlaybackControllerTest {

    @Test
    void advance_sequencesNotificationsMovementPlaybackAndTurnCompletion() {
        var runtime = new GameRuntime(loadedScenarioWithPendingMove("moving-unit", 1, 1, 3, 4));
        var controller = new BattlefieldPhasePlaybackController(1.0f, 0.5f, 0f);

        controller.start(runtime);

        assertEquals(InteractionLockState.TURN_COMMIT, controller.interactionLockState());
        assertTrue(runtime.hasActiveTurnExecution());

        controller.advance(runtime, 0f);
        assertEquals(InteractionLockState.PHASE_NOTIFICATION, controller.interactionLockState());
        assertEquals("ISSUE ORDERS", overlayLabel(controller));

        controller.advance(runtime, 1.0f);
        assertEquals("SIMULTANEOUS MOVE", overlayLabel(controller));

        controller.advance(runtime, 1.0f);
        assertEquals(InteractionLockState.MOVEMENT_PLAYBACK, controller.interactionLockState());
        var movementState = controller.movementPlaybackRenderState();
        assertNotNull(movementState);
        assertTrue(movementState.playback().stream().anyMatch(playback -> playback.unitId().equals(UnitId.of("moving-unit")) && playback.moved()));
        assertEquals(0f, movementState.progress());

        controller.advance(runtime, 0.25f);
        var midPlayback = controller.movementPlaybackRenderState();
        assertNotNull(midPlayback);
        assertEquals(0.5f, midPlayback.progress(), 0.0001f);

        controller.advance(runtime, 10f);
        assertFalse(controller.isActive());
        assertEquals(InteractionLockState.NONE, controller.interactionLockState());
        assertTrue(controller.consumeTurnCompletedThisFrame());
        assertFalse(runtime.hasActiveTurnExecution());
        assertFalse(controller.consumeTurnCompletedThisFrame());
    }

    @Test
    void phaseOverlayRenderContract_isVisibleOnlyDuringPhaseNotifications() {
        var runtime = new GameRuntime(loadedScenarioWithPendingMove("moving-unit", 1, 1, 3, 4));
        var controller = new BattlefieldPhasePlaybackController(1.0f, 0.5f, 0f);

        controller.start(runtime);
        controller.advance(runtime, 0f);

        assertEquals("ISSUE ORDERS", overlayLabel(controller));

        controller.advance(runtime, 2.0f);

        assertTrue(controller.phaseOverlayRenderContract().isEmpty());
    }

    private static String overlayLabel(BattlefieldPhasePlaybackController controller) {
        var renderContract = controller.phaseOverlayRenderContract().orElse(null);
        assertNotNull(renderContract);
        return renderContract.label();
    }

    private static LoadedScenario loadedScenarioWithPendingMove(String unitId,
                                                                int startTileX,
                                                                int startTileY,
                                                                int targetTileX,
                                                                int targetTileY) {
        var unit = new Unit(unitId, Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, startTileX, startTileY);
        var scenarioDefinition = new ScenarioDefinition(
            "controller-runtime",
            "Controller Runtime",
            10,
            10,
            TerrainType.SAND,
            List.of(unit)
        );
        var campaignState = new CampaignState(
            "test-campaign",
            "controller-runtime",
            1,
            Side.ALLIES,
            List.of(unit),
            List.of(Order.of(UUID.fromString("00000000-0000-0000-0000-0000000000f1"), UnitId.of(unitId), Side.ALLIES, OrderType.MOVE, new TileCoordinate(targetTileX, targetTileY)))
        );
        return new LoadedScenario(scenarioDefinition, campaignState);
    }
}