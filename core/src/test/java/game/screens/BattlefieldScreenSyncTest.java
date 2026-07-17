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
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattlefieldScreenSyncTest {

    private static final class FakeView implements BattlefieldScreen.UnitInfoView {
        @Nullable String shownId = null;
        boolean visible = false;

        @Override
        public void showUnit(String unitId) {
            shownId = unitId;
            visible = true;
        }

        @Override
        public void hide() {
            shownId = null;
            visible = false;
        }
    }

    // --- syncUnitInfoPanel ---

    @Test
    void syncUnitInfoPanel_showsUnitAndSetsId_whenUnitSelected() {
        var view = new FakeView();

        BattlefieldScreen.syncUnitInfoPanel(UnitId.of("tank-1"), view);

        assertEquals("tank-1", view.shownId);
        assertTrue(view.visible);
    }

    @Test
    void syncUnitInfoPanel_hidesSection_whenNoUnitSelected() {
        var view = new FakeView();
        view.showUnit("tank-1");  // precondition: was visible

        BattlefieldScreen.syncUnitInfoPanel(UnitId.none(), view);

        assertFalse(view.visible);
        assertNull(view.shownId);
    }

    @Test
    void syncUnitInfoPanel_updatesId_whenSelectionChanges() {
        var view = new FakeView();

        BattlefieldScreen.syncUnitInfoPanel(UnitId.of("unit-A"), view);
        assertEquals("unit-A", view.shownId);

        BattlefieldScreen.syncUnitInfoPanel(UnitId.of("unit-B"), view);
        assertEquals("unit-B", view.shownId);
        assertTrue(view.visible);
    }

    @Test
    void phaseOverlayLabel_mapsRuntimePhasesToUserFacingLabels() {
        assertEquals("ISSUE ORDERS", BattlefieldScreen.phaseOverlayLabel(game.engine.TurnPhase.ISSUE_ORDERS));
        assertEquals("SIMULTANEOUS MOVE", BattlefieldScreen.phaseOverlayLabel(game.engine.TurnPhase.SIMULTANEOUS_MOVE));
        assertEquals("COMBAT", BattlefieldScreen.phaseOverlayLabel(game.engine.TurnPhase.COMBAT));
        assertEquals("RETREAT", BattlefieldScreen.phaseOverlayLabel(game.engine.TurnPhase.RETREAT));
        assertEquals("END TURN", BattlefieldScreen.phaseOverlayLabel(game.engine.TurnPhase.END_TURN));
    }

    @Test
    void phaseOverlayDimColor_returnsConfiguredOverlayColor() {
        var dimColor = BattlefieldScreen.phaseOverlayDimColor();

        assertEquals(0f, dimColor.r);
        assertEquals(0f, dimColor.g);
        assertEquals(0f, dimColor.b);
        assertTrue(dimColor.a > 0f);
    }

    @Test
    void runtimeStatusSummary_reflectsCurrentRuntimeTurnAndActiveSide() {
        var runtime = new GameRuntime(game.platform.ScenarioLoader.loadBootstrapScenario());

        assertEquals(1, runtime.getTurnNumber());
        assertEquals("ALLIES", runtime.getActiveSideCode());
    }

    @Test
    void endTurnButton_click_isBlockedDuringMovementPlayback() {
        var game = new RecordingGame();
        var screen = new BattlefieldScreen(game, loadedScenarioWithPendingMove("moving-unit", 1, 1, 3, 4));
        var runtime = new GameRuntime(loadedScenarioWithPendingMove("moving-unit", 1, 1, 3, 4));

        setField(screen, "gameRuntime", runtime);

        var phasePlaybackController = readField(screen, "phasePlaybackController", BattlefieldPhasePlaybackController.class);
        phasePlaybackController.start(runtime);
        phasePlaybackController.advance(runtime, 6.1f);

        assertEquals(InteractionLockState.MOVEMENT_PLAYBACK, phasePlaybackController.interactionLockState());

        assertDoesNotThrow(() -> invokeEndTurn(screen));
        assertEquals(InteractionLockState.MOVEMENT_PLAYBACK, phasePlaybackController.interactionLockState());
    }

    private static void invokeEndTurn(BattlefieldScreen screen) throws ReflectiveOperationException {
        var endTurn = BattlefieldScreen.class.getDeclaredMethod("endTurn");
        endTurn.setAccessible(true);
        endTurn.invoke(screen);
    }

    private static <T> T readField(Object target, String fieldName, Class<T> fieldType) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return fieldType.cast(field.get(target));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static LoadedScenario loadedScenarioWithPendingMove(String unitId,
                                                                int startTileX,
                                                                int startTileY,
                                                                int targetTileX,
                                                                int targetTileY) {
        var unit = new Unit(unitId, Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, startTileX, startTileY);
        var scenarioDefinition = new ScenarioDefinition(
            "screen-runtime",
            "Screen Runtime",
            10,
            10,
            TerrainType.SAND,
            List.of(unit)
        );
        var campaignState = new CampaignState(
            "test-campaign",
            "screen-runtime",
            1,
            Side.ALLIES,
            List.of(unit),
            List.of(Order.of(UUID.fromString("00000000-0000-0000-0000-0000000000f3"), UnitId.of(unitId), Side.ALLIES, OrderType.MOVE, new TileCoordinate(targetTileX, targetTileY)))
        );
        return new LoadedScenario(scenarioDefinition, campaignState);
    }

    private static final class RecordingGame extends com.badlogic.gdx.Game {
        @Override
        public void create() {
        }
    }
}
