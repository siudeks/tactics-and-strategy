package game.engine;

import game.domain.CampaignState;
import game.domain.Unit;
import game.domain.UnitType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

final class CombatPhaseExecutor implements TurnPhaseExecutor {
    CombatPhaseExecutor() {
    }

    @Override
    public TurnPhase phase() {
        return TurnPhase.COMBAT;
    }

    @Override
    public PhaseExecution execute(CampaignState state) {
        var survivingUnits = new HashSet<String>();
        for (Unit unit : state.units()) {
            survivingUnits.add(unit.id());
        }

        var units = List.copyOf(state.units());
        for (Unit unit : units) {
            if (!survivingUnits.contains(unit.id())) {
                continue;
            }

            var strongestEnemy = units.stream()
                .filter(enemy -> !enemy.id().equals(unit.id()))
                .filter(enemy -> enemy.side() != unit.side())
                .filter(enemy -> isAdjacent(unit, enemy))
                .sorted(Comparator
                    .comparingInt((Unit enemy) -> combatRank(enemy.type()))
                    .thenComparing(Unit::id))
                .findFirst();

            if (strongestEnemy.isEmpty()) {
                continue;
            }

            var enemy = strongestEnemy.orElseThrow();
            var unitStrength = combatRank(unit.type());
            var enemyStrength = combatRank(enemy.type());
            if (enemyStrength < unitStrength) {
                survivingUnits.remove(enemy.id());
            } else if (enemyStrength > unitStrength) {
                survivingUnits.remove(unit.id());
            } else if (enemy.id().compareTo(unit.id()) < 0) {
                survivingUnits.remove(unit.id());
            } else {
                survivingUnits.remove(enemy.id());
            }
        }

        var nextUnits = new ArrayList<Unit>(state.units().size());
        for (Unit unit : state.units()) {
            if (survivingUnits.contains(unit.id())) {
                nextUnits.add(unit);
            }
        }

        var nextState = new CampaignState(
            state.campaignId(),
            state.scenarioId(),
            state.turnNumber(),
            state.activeSide(),
            nextUnits,
            state.pendingOrders()
        );
        return new PhaseExecution(nextState, List.of());
    }

    private static boolean isAdjacent(Unit first, Unit second) {
        return Math.abs(first.tileX() - second.tileX()) + Math.abs(first.tileY() - second.tileY()) == 1;
    }

    private static int combatRank(UnitType type) {
        return switch (type) {
            case HQ -> 0;
            case ARTILLERY -> 1;
            case ANTI_TANK -> 2;
            case MEDIUM_TANK -> 3;
            case LIGHT_TANK -> 4;
            case INFANTRY_TANK -> 5;
            case RECCE -> 6;
            case MOTORISED_INFANTRY -> 7;
            case FOOT_INFANTRY -> 8;
            case SUPPORT_GROUP -> 9;
        };
    }
}