package game.engine;

import game.domain.*;
import java.util.*;

public final class TurnEngine {
    private final DeterministicContext context;
    private final ScenarioDefinition scenarioDefinition;
    private final Map<TurnPhase, TurnPhaseExecutor> phaseExecutors;

    private TurnEngine(DeterministicContext context, ScenarioDefinition scenarioDefinition) {
        this.context = context;
        this.scenarioDefinition = scenarioDefinition;
        this.phaseExecutors = Map.of(
            TurnPhase.ISSUE_ORDERS, new IssueOrdersPhaseExecutor(),
            TurnPhase.SIMULTANEOUS_MOVE, new SimultaneousMovePhaseExecutor(this.scenarioDefinition),
            TurnPhase.COMBAT, new CombatPhaseExecutor(),
            TurnPhase.RETREAT, new RetreatPhaseExecutor(),
            TurnPhase.END_TURN, new EndTurnPhaseExecutor()
        );
    }

    public static TurnEngine fixedContext(DeterministicContext context, ScenarioDefinition scenarioDefinition) {
        return new TurnEngine(context, scenarioDefinition);
    }

    public TurnResult runOneTurn(CampaignState state) {
        var session = beginExecution(state);
        var stepResult = session.advance();
        while (!session.isComplete()) {
            stepResult = session.advance();
        }
        return stepResult.completedTurnResult().orElseThrow();
    }

    public TurnExecutionSession beginExecution(CampaignState state) {
        return new TurnExecutionSession(this, state);
    }

    PhaseExecution executePhase(TurnPhase phase, CampaignState state) {
        var executor = phaseExecutors.get(phase);
        if (executor == null) {
            throw new IllegalArgumentException("No executor registered for phase " + phase);
        }
        return executor.execute(state);
    }

    TurnResult buildTurnResult(CampaignState state, List<TurnPhase> phaseTrace, long startTime) {
        var elapsed = System.currentTimeMillis() - startTime;
        var snapshot = buildCanonicalSnapshot(state);
        return new TurnResult(state, phaseTrace, context.seed(), elapsed, snapshot);
    }

    static List<TurnPhase> phaseSequence() {
        return List.of(
            TurnPhase.ISSUE_ORDERS,
            TurnPhase.SIMULTANEOUS_MOVE,
            TurnPhase.COMBAT,
            TurnPhase.RETREAT,
            TurnPhase.END_TURN
        );
    }

    static boolean isValidMove(ScenarioDefinition scenarioDefinition, int x, int y) {
        if (x < 0 || y < 0 || x >= scenarioDefinition.mapWidth() || y >= scenarioDefinition.mapHeight()) {
            return false;
        }
        var terrain = scenarioDefinition.defaultTerrain();
        return terrain != TerrainType.VOID && terrain != TerrainType.WATER;
    }

    static Side flipSide(Side side) {
        return switch (side) {
            case ALLIES -> Side.AXIS;
            case AXIS -> Side.ALLIES;
            case NEUTRAL -> throw new IllegalStateException("Cannot flip NEUTRAL side in turn engine");
        };
    }

    static String buildCanonicalSnapshot(CampaignState state) {
        var sb = new StringBuilder();
        sb.append("turn=").append(state.turnNumber());
        sb.append(",side=").append(state.activeSide());
        sb.append(",units=[");
        state.units().stream()
            .sorted(Comparator.comparing(Unit::id))
            .forEach(u -> sb.append(u.id()).append("@").append(u.tileX()).append(",").append(u.tileY()).append(";"));
        sb.append("]");
        return sb.toString();
    }

    public static boolean areSemanticallyEquivalent(TurnResult a, TurnResult b) {
        if (Objects.equals(a, b)) return true;
        if (a == null || b == null) return false;
        return a.state().equals(b.state())
            && a.phaseTrace().equals(b.phaseTrace())
            && a.canonicalSnapshot().equals(b.canonicalSnapshot());
    }
}
