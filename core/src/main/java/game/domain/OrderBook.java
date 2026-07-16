package game.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable wrapper over pending orders with deterministic MOVE-order policies.
 */
public record OrderBook(List<Order> orders) {

    public record MoveUpsertResult(OrderBook orderBook, boolean replacedExisting) {
    }

    public OrderBook {
        orders = List.copyOf(orders);
    }

    /**
     * Returns a deterministic materialized view of active MOVE orders keyed by unit id.
     * If input contains duplicates for one unit, the last order wins.
     */
    public Map<String, Order> activeMoveOrdersByUnit() {
        var moveOrders = new LinkedHashMap<String, Order>();
        for (Order order : orders) {
            if (order.type() == OrderType.MOVE) {
                moveOrders.put(order.unitId(), order);
            }
        }
        return Map.copyOf(moveOrders);
    }

    /**
     * Replaces current MOVE order for a unit (if present) using deterministic last-write-wins.
     */
    public MoveUpsertResult upsertMove(String unitId, Side side, int targetX, int targetY) {
        return upsertMove(unitId, side, TileCoordinate.of(targetX, targetY));
    }

    /**
     * Replaces current MOVE order for a unit (if present) using deterministic last-write-wins.
     */
    public MoveUpsertResult upsertMove(String unitId, Side side, TileCoordinate target) {
        var replacedExisting = false;
        var next = new ArrayList<Order>(orders.size() + 1);
        for (Order existing : orders) {
            if (existing.type() == OrderType.MOVE && existing.unitId().equals(unitId)) {
                replacedExisting = true;
                continue;
            }
            next.add(existing);
        }
        next.add(Order.of("move-" + unitId, unitId, side, OrderType.MOVE, target));
        return new MoveUpsertResult(new OrderBook(next), replacedExisting);
    }

    public List<Order> asPendingOrders() {
        return orders;
    }
}