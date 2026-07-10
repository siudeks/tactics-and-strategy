package game.engine;

import game.domain.CampaignState;
import game.domain.Order;
import game.domain.Side;
import java.util.List;

@FunctionalInterface
public interface TurnPlanningStrategy {
    List<Order> planOrders(CampaignState state, Side side);
}
