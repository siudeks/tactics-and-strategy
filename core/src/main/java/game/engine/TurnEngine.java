package game.engine;

import game.domain.*;
import java.util.*;
import java.util.stream.Collectors;

public final class TurnEngine {
    private final DeterministicContext context;
    private final ScenarioDefinition scenarioDefinition;

    private TurnEngine(DeterministicContext context, ScenarioDefinition scenarioDefinition) {
        this.context = Objects.requireNonNull(context);
        this.scenarioDefinition = Objects.requireNonNull(scenarioDefinition);
    }

    public static TurnEngine fixedContext(DeterministicContext context, ScenarioDefinition scenarioDefinition) {
        return new TurnEngine(context, scenarioDefinition);
    }

    public TurnResult runOneTurn(CampaignState state) {
        long startTime = System.currentTimeMillis();
        List<TurnPhase> phaseTrace = new ArrayList<>();

        // Phase 1: ISSUE_ORDERS (no-op for v0 - orders already in state.pendingOrders())
        phaseTrace.add(TurnPhase.ISSUE_ORDERS);

        // Phase 2: SIMULTANEOUS_MOVE - apply all MOVE orders
        List<Unit> updatedUnits = applyMoveOrders(state);
        phaseTrace.add(TurnPhase.SIMULTANEOUS_MOVE);

        // Phase 3: COMBAT (no-op for v0)
        phaseTrace.add(TurnPhase.COMBAT);

        // Phase 4: RETREAT (no-op for v0)
        phaseTrace.add(TurnPhase.RETREAT);

        // Phase 5: END_TURN - increment turn, flip side, clear orders
        Side nextSide = flipSide(state.activeSide());
        CampaignState newState = new CampaignState(
            state.campaignId(),
            state.scenarioId(),
            state.turnNumber() + 1,
            nextSide,
            updatedUnits,
            List.of()  // clear pending orders
        );
        phaseTrace.add(TurnPhase.END_TURN);

        String snapshot = buildCanonicalSnapshot(newState);
        long elapsed = System.currentTimeMillis() - startTime;

        return new TurnResult(newState, phaseTrace, context.seed(), elapsed, snapshot);
    }

    private List<Unit> applyMoveOrders(CampaignState state) {
        // Build map of unitId -> target from MOVE orders
        Map<String, int[]> moveTargets = new HashMap<>();
        for (Order order : state.pendingOrders()) {
            if (order.type() == OrderType.MOVE) {
                moveTargets.put(order.unitId(), new int[]{order.targetX(), order.targetY()});
            }
        }

        List<Unit> result = new ArrayList<>();
        for (Unit unit : state.units()) {
            int[] target = moveTargets.get(unit.id());
            if (target != null && isValidMove(target[0], target[1])) {
                result.add(new Unit(unit.id(), unit.side(), unit.type(), unit.size(), target[0], target[1]));
            } else {
                result.add(unit);
            }
        }
        return result;
    }

    private boolean isValidMove(int x, int y) {
        if (x < 0 || y < 0 || x >= scenarioDefinition.mapWidth() || y >= scenarioDefinition.mapHeight()) {
            return false;
        }
        TerrainType terrain = scenarioDefinition.defaultTerrain();
        return terrain != TerrainType.VOID && terrain != TerrainType.WATER;
    }

    private Side flipSide(Side side) {
        return switch (side) {
            case ALLIES -> Side.AXIS;
            case AXIS -> Side.ALLIES;
            case NEUTRAL -> throw new IllegalStateException("Cannot flip NEUTRAL side in turn engine");
        };
    }

    private String buildCanonicalSnapshot(CampaignState state) {
        StringBuilder sb = new StringBuilder();
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
        if (a == b) return true;
        if (a == null || b == null) return false;
        return a.state().equals(b.state())
            && a.phaseTrace().equals(b.phaseTrace())
            && a.canonicalSnapshot().equals(b.canonicalSnapshot());
    }
}
