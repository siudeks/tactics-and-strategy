package game.screens;

import game.domain.CampaignState;
import game.domain.Side;
import game.domain.Unit;
import game.domain.UnitSize;
import game.domain.UnitType;
import game.engine.MovementPlayback;
import game.engine.MovementPlaybackOutcome;
import game.engine.TileCoordinate;
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
        Unit movingUnit = new Unit("moving", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 3, 4);
        Unit staticUnit = new Unit("static", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 5, 5);
        CampaignState campaignState = new CampaignState(
            "test-campaign",
            "test-scenario",
            1,
            Side.ALLIES,
            List.of(movingUnit, staticUnit),
            List.of()
        );
        MovementPlaybackRenderState movementState = new MovementPlaybackRenderState(
            List.of(
                new MovementPlayback("moving", new TileCoordinate(1, 1), new TileCoordinate(3, 4), MovementPlaybackOutcome.MOVED),
                new MovementPlayback("static", new TileCoordinate(5, 5), new TileCoordinate(5, 5), MovementPlaybackOutcome.SKIPPED)
            ),
            0.5f
        );

        Map<String, BattlefieldScreen.UnitRenderPlacement> placements = BattlefieldScreen.computeVisibleUnitPlacements(
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

        BattlefieldScreen.UnitRenderPlacement movingPlacement = Objects.requireNonNull(placements.get("moving"));
        BattlefieldScreen.UnitRenderPlacement staticPlacement = Objects.requireNonNull(placements.get("static"));

        assertEquals(32f, movingPlacement.screenX(), 0.0001f);
        assertEquals(88f, movingPlacement.screenY(), 0.0001f);
        assertEquals(80f, staticPlacement.screenX(), 0.0001f);
        assertEquals(48f, staticPlacement.screenY(), 0.0001f);
    }

    @Test
    void resolveTilePosition_returnsFinalTileWhenNoPlaybackStateExists() {
        Unit unit = new Unit("unit", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 4, 6);

        UnitRenderPositionResolver.RenderTilePosition position = UnitRenderPositionResolver.resolveTilePosition(unit, null, null);

        assertEquals(4f, position.tileX());
        assertEquals(6f, position.tileY());
    }
}