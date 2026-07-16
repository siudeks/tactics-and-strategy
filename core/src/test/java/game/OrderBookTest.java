package game;

import game.domain.Order;
import game.domain.OrderBook;
import game.domain.OrderType;
import game.domain.Side;
import game.domain.TileCoordinate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderBookTest {

    @Test
    void upsertMove_addsOrderWhenNoMoveForUnitExists() {
        OrderBook book = new OrderBook(List.of());

        OrderBook.MoveUpsertResult result = book.upsertMove("u1", Side.ALLIES, 2, 3);

        assertFalse(result.replacedExisting());
        List<Order> orders = result.orderBook().asPendingOrders();
        assertEquals(1, orders.size());
        assertEquals("u1", orders.get(0).unitId());
        assertEquals(OrderType.MOVE, orders.get(0).type());
        assertEquals(2, orders.get(0).target().x());
        assertEquals(3, orders.get(0).target().y());
    }

    @Test
    void upsertMove_replacesOnlyExistingMoveForSameUnit_withDeterministicLastWriteWins() {
        OrderBook book = new OrderBook(List.of(
            Order.of("hold-u1", "u1", Side.ALLIES, OrderType.HOLD, new TileCoordinate(0, 0)),
            Order.of("move-u1", "u1", Side.ALLIES, OrderType.MOVE, new TileCoordinate(1, 1)),
            Order.of("move-u2", "u2", Side.ALLIES, OrderType.MOVE, new TileCoordinate(5, 5))
        ));

        OrderBook.MoveUpsertResult result = book.upsertMove("u1", Side.ALLIES, 7, 8);

        assertTrue(result.replacedExisting());
        List<Order> orders = result.orderBook().asPendingOrders();
        assertEquals(3, orders.size());
        long u1MoveCount = orders.stream()
            .filter(o -> o.type() == OrderType.MOVE && o.unitId().equals("u1"))
            .count();
        assertEquals(1, u1MoveCount);
        Order last = orders.get(orders.size() - 1);
        assertEquals("u1", last.unitId());
        assertEquals(OrderType.MOVE, last.type());
        assertEquals(7, last.target().x());
        assertEquals(8, last.target().y());
    }

    @Test
    void activeMoveOrdersByUnit_prefersLastMoveForDuplicateUnitIds() {
        OrderBook book = new OrderBook(List.of(
            Order.of("move-u1-a", "u1", Side.ALLIES, OrderType.MOVE, new TileCoordinate(1, 1)),
            Order.of("move-u2", "u2", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2)),
            Order.of("move-u1-b", "u1", Side.ALLIES, OrderType.MOVE, new TileCoordinate(4, 5))
        ));

        var activeMoves = book.activeMoveOrdersByUnit();

        assertEquals(2, activeMoves.size());
        Order u1Move = activeMoves.get("u1");
        assertNotNull(u1Move);
        assertEquals(4, u1Move.target().x());
        assertEquals(5, u1Move.target().y());
        Order u2Move = activeMoves.get("u2");
        assertNotNull(u2Move);
        assertEquals(2, u2Move.target().x());
        assertEquals(2, u2Move.target().y());
    }
}