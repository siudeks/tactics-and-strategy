package game.screens;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattlefieldScreenSyncTest {

    private static final class FakeView implements BattlefieldScreen.UnitInfoView {
        @Nullable String shownId = null;
        boolean visible = false;

        @Override
        public void showUnit(String unitId) {
            shownId = unitId;
            visible = true;
        }

        @Override
        public void hide() {
            shownId = null;
            visible = false;
        }
    }

    // --- syncUnitInfoPanel ---

    @Test
    void syncUnitInfoPanel_showsUnitAndSetsId_whenUnitSelected() {
        FakeView view = new FakeView();

        BattlefieldScreen.syncUnitInfoPanel("tank-1", view);

        assertEquals("tank-1", view.shownId);
        assertTrue(view.visible);
    }

    @Test
    void syncUnitInfoPanel_hidesSection_whenNoUnitSelected() {
        FakeView view = new FakeView();
        view.showUnit("tank-1");  // precondition: was visible

        BattlefieldScreen.syncUnitInfoPanel(null, view);

        assertFalse(view.visible);
        assertNull(view.shownId);
    }

    @Test
    void syncUnitInfoPanel_updatesId_whenSelectionChanges() {
        FakeView view = new FakeView();

        BattlefieldScreen.syncUnitInfoPanel("unit-A", view);
        assertEquals("unit-A", view.shownId);

        BattlefieldScreen.syncUnitInfoPanel("unit-B", view);
        assertEquals("unit-B", view.shownId);
        assertTrue(view.visible);
    }
}
