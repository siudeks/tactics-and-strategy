package game;

import game.domain.Order;
import game.domain.OrderBook;
import game.domain.OrderType;
import game.domain.Side;
import game.domain.TileCoordinate;
import game.domain.UnitId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderBookTest {

    @Test
    void upsertMove_addsOrderWhenNoMoveForUnitExists() {
        var book = new OrderBook(List.of());
        var unitId = UnitId.of("u1");

        var result = book.upsertMove(unitId, Side.ALLIES, 2, 3);

        assertFalse(result.replacedExisting());
        var orders = result.orderBook().asPendingOrders();
        assertEquals(1, orders.size());
        assertInstanceOf(UUID.class, orders.get(0).id());
        assertEquals(unitId, orders.get(0).unitId());
        assertEquals(OrderType.MOVE, orders.get(0).type());
        assertEquals(2, orders.get(0).target().x());
        assertEquals(3, orders.get(0).target().y());
    }

    @Test
    void upsertMove_replacesOnlyExistingMoveForSameUnit_withDeterministicLastWriteWins() {
        var u1 = UnitId.of("u1");
        var u2 = UnitId.of("u2");
        var book = new OrderBook(List.of(
            Order.of(UUID.fromString("00000000-0000-0000-0000-000000000001"), u1, Side.ALLIES, OrderType.HOLD, new TileCoordinate(0, 0)),
            Order.of(UUID.fromString("00000000-0000-0000-0000-000000000002"), u1, Side.ALLIES, OrderType.MOVE, new TileCoordinate(1, 1)),
            Order.of(UUID.fromString("00000000-0000-0000-0000-000000000003"), u2, Side.ALLIES, OrderType.MOVE, new TileCoordinate(5, 5))
        ));

        var result = book.upsertMove(u1, Side.ALLIES, 7, 8);

        assertTrue(result.replacedExisting());
        var orders = result.orderBook().asPendingOrders();
        assertEquals(3, orders.size());
        var u1MoveCount = orders.stream()
            .filter(o -> o.type() == OrderType.MOVE && o.unitId().equals(u1))
            .count();
        assertEquals(1, u1MoveCount);
        var last = orders.get(orders.size() - 1);
        assertEquals(u1, last.unitId());
        assertEquals(OrderType.MOVE, last.type());
        assertEquals(7, last.target().x());
        assertEquals(8, last.target().y());
    }

    @Test
    void activeMoveOrdersByUnit_prefersLastMoveForDuplicateUnitIds() {
        var u1 = UnitId.of("u1");
        var u2 = UnitId.of("u2");
        var book = new OrderBook(List.of(
            Order.of(UUID.fromString("00000000-0000-0000-0000-000000000004"), u1, Side.ALLIES, OrderType.MOVE, new TileCoordinate(1, 1)),
            Order.of(UUID.fromString("00000000-0000-0000-0000-000000000005"), u2, Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2)),
            Order.of(UUID.fromString("00000000-0000-0000-0000-000000000006"), u1, Side.ALLIES, OrderType.MOVE, new TileCoordinate(4, 5))
        ));

        var activeMoves = book.activeMoveOrdersByUnit();

        assertEquals(2, activeMoves.size());
        var u1Move = activeMoves.get(u1);
        assertNotNull(u1Move);
        assertEquals(4, u1Move.target().x());
        assertEquals(5, u1Move.target().y());
        var u2Move = activeMoves.get(u2);
        assertNotNull(u2Move);
        assertEquals(2, u2Move.target().x());
        assertEquals(2, u2Move.target().y());
    }
}