package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import game.domain.CampaignState;
import game.domain.Side;
import game.domain.Unit;
import game.scenario.LoadedScenario;
import game.platform.ScenarioLoader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CampaignInitializationTest {

    private CampaignState campaignState;

    @BeforeEach
    void setUp() {
        LoadedScenario loaded = ScenarioLoader.loadBootstrapScenario();
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
        List<Unit> alliesUnits = campaignState.units().stream()
            .filter(u -> u.side() == Side.ALLIES)
            .toList();
        assertEquals(2, alliesUnits.size());
    }

    @Test
    void initialCampaignState_axisUnitsPresent() {
        List<Unit> axisUnits = campaignState.units().stream()
            .filter(u -> u.side() == Side.AXIS)
            .toList();
        assertEquals(2, axisUnits.size());
    }
}
