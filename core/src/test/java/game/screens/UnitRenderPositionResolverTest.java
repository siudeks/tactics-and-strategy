package game.screens;

import game.domain.CampaignState;
import game.domain.Side;
import game.domain.Unit;
import game.domain.UnitSize;
import game.domain.UnitType;
import game.engine.MovementPlayback;
import game.engine.MovementPlaybackOutcome;
import game.domain.TileCoordinate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnitRenderPositionResolverTest {

    @Test
    void computeVisibleUnitPlacements_interpolatesMovedUnitsAndKeepsOtherUnitsStable() {
        var movingUnit = new Unit("moving", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 3, 4);
        Unit staticUnit = new Unit("static", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 5, 5);
        var campaignState = new CampaignState(
            "test-campaign",
            "test-scenario",
            1,
            Side.ALLIES,
            List.of(movingUnit, staticUnit),
            List.of()
        );
        var movementState = new MovementPlaybackRenderState(
            List.of(
                new MovementPlayback("moving", new TileCoordinate(1, 1), new TileCoordinate(3, 4), MovementPlaybackOutcome.MOVED),
                new MovementPlayback("static", new TileCoordinate(5, 5), new TileCoordinate(5, 5), MovementPlaybackOutcome.SKIPPED)
            ),
            0.5f
        );

        var placements = BattlefieldScreen.computeVisibleUnitPlacements(
            campaignState,
            movementState,
            10,
            0f,
            0f,
            400f,
            400f,
            0f,
            0f,
            1f
        ).stream().collect(Collectors.toMap(placement -> placement.unit().id(), Function.identity()));

        var movingPlacement = Objects.requireNonNull(placements.get("moving"));
        BattlefieldScreen.UnitRenderPlacement staticPlacement = Objects.requireNonNull(placements.get("static"));

        assertEquals(32f, movingPlacement.screenX(), 0.0001f);
        assertEquals(88f, movingPlacement.screenY(), 0.0001f);
        assertEquals(80f, staticPlacement.screenX(), 0.0001f);
        assertEquals(48f, staticPlacement.screenY(), 0.0001f);
    }

    @Test
    void resolveTilePosition_returnsFinalTileWhenNoPlaybackStateExists() {
        var unit = new Unit("unit", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 4, 6);

        var position = UnitRenderPositionResolver.resolveTilePosition(unit, null, null);

        assertEquals(4f, position.tileX());
        assertEquals(6f, position.tileY());
    }

    @Test
    void resolveTilePosition_rtsPositionTakesPriorityOverPlaybackAndStatic() {
        var unit = new Unit("unit", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 4, 6);
        float[] rtsPos = {2.5f, 3.7f};
        var playback = new MovementPlaybackRenderState(
            List.of(new MovementPlayback("unit", new TileCoordinate(0, 0), new TileCoordinate(8, 8), MovementPlaybackOutcome.MOVED)),
            0.5f
        );

        var position = UnitRenderPositionResolver.resolveTilePosition(unit, rtsPos, playback);

        assertEquals(2.5f, position.tileX(), 0.0001f, "RTS position should override both playback and static");
        assertEquals(3.7f, position.tileY(), 0.0001f);
    }

    @Test
    void computeVisibleUnitPlacements_rtsPositionsOverrideStaticTileCoords() {
        var unit = new Unit("moving", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 0, 0);
        var campaignState = new CampaignState(
            "c", "s", 1, Side.ALLIES, List.of(unit), List.of()
        );
        // Unit is at tile (0,0) but RTS tracker says it's at (2.0, 0.0)
        var rtsPositions = Map.of("moving", new float[]{2f, 0f});

        var placements = BattlefieldScreen.computeVisibleUnitPlacements(
            campaignState,
            rtsPositions,
            null,
            10,
            0f, 0f, 400f, 400f,
            0f, 0f, 1f
        ).stream().collect(Collectors.toMap(p -> p.unit().id(), Function.identity()));

        var placement = Objects.requireNonNull(placements.get("moving"));
        // Expected screenX = 2 * DRAW_TILE_SIZE(16) = 32
        assertEquals(32f, placement.screenX(), 0.0001f, "Should render at RTS float position, not integer tile (0,0)");
    }
}