package game.engine;

import game.domain.CampaignState;
import game.domain.Order;
import game.domain.Side;
import java.util.List;

public record NoOpTurnPlanningStrategy() implements TurnPlanningStrategy {
    @Override
    public List<Order> planOrders(CampaignState state, Side side) {
        return List.of();
    }
}
