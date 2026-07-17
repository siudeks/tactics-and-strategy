package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import game.domain.CampaignState;
import game.domain.Side;
import game.platform.ScenarioLoader;

import static org.junit.jupiter.api.Assertions.*;

class CampaignInitializationTest {

    private CampaignState campaignState;

    @BeforeEach
    void setUp() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        campaignState = loaded.campaignState();
    }

    @Test
    void initialCampaignState_hasCorrectTurnNumber() {
        assertEquals(1, campaignState.turnNumber());
    }

    @Test
    void initialCampaignState_hasCorrectActiveSide() {
        assertEquals(Side.ALLIES, campaignState.activeSide());
    }

    @Test
    void initialCampaignState_hasNoPendingOrders() {
        assertTrue(campaignState.pendingOrders().isEmpty());
    }

    @Test
    void initialCampaignState_alliesUnitsPresent() {
        var alliesUnits = campaignState.units().stream()
            .filter(u -> u.side() == Side.ALLIES)
            .toList();
        assertEquals(2, alliesUnits.size());
    }

    @Test
    void initialCampaignState_axisUnitsPresent() {
        var axisUnits = campaignState.units().stream()
            .filter(u -> u.side() == Side.AXIS)
            .toList();
        assertEquals(2, axisUnits.size());
    }
}
