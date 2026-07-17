package game.engine;

import game.domain.CampaignState;
import game.domain.Order;
import game.domain.Side;
import game.domain.Unit;
import game.domain.UnitSize;
import game.domain.UnitType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CombatPhaseExecutorTest {

    @Test
    void execute_adjacentEnemyUnits_resolvesCombatDeterministically() {
        var units = List.of(
            new Unit("alpha", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 1, 1),
            new Unit("bravo", Side.AXIS, UnitType.LIGHT_TANK, UnitSize.BATTALION, 2, 1)
        );
        var state = new CampaignState("campaign", "combat-test", 1, Side.ALLIES, units, List.<Order>of());
        var executor = new CombatPhaseExecutor();

        var execution = executor.execute(state);

        assertEquals(List.of("bravo"), execution.state().units().stream().map(Unit::id).toList());
    }
}
