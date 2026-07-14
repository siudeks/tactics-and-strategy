package game.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable wrapper over pending orders with deterministic MOVE-order policies.
 */
public record OrderBook(List<Order> orders) {

    public record MoveUpsertResult(OrderBook orderBook, boolean replacedExisting) {
        public MoveUpsertResult {
            Objects.requireNonNull(orderBook, "orderBook must not be null");
        }
    }

    public OrderBook {
        orders = List.copyOf(Objects.requireNonNull(orders, "orders must not be null"));
    }

    /**
     * Returns a deterministic materialized view of active MOVE orders keyed by unit id.
     * If input contains duplicates for one unit, the last order wins.
     */
    public Map<String, Order> activeMoveOrdersByUnit() {
        Map<String, Order> moveOrders = new LinkedHashMap<>();
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
        Objects.requireNonNull(unitId, "unitId must not be null");
        Objects.requireNonNull(side, "side must not be null");

        boolean replacedExisting = false;
        List<Order> next = new ArrayList<>(orders.size() + 1);
        for (Order existing : orders) {
            if (existing.type() == OrderType.MOVE && existing.unitId().equals(unitId)) {
                replacedExisting = true;
                continue;
            }
            next.add(existing);
        }
        next.add(new Order("move-" + unitId, unitId, side, OrderType.MOVE, targetX, targetY));
        return new MoveUpsertResult(new OrderBook(next), replacedExisting);
    }

    public List<Order> asPendingOrders() {
        return orders;
    }
}