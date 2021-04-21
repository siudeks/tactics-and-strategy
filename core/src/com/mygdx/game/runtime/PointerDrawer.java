package com.mygdx.game.runtime;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mygdx.game.Config;
import com.mygdx.game.extensions.SpriteBatchUtils;
import com.mygdx.game.resources.SelectionTexture;
import com.mygdx.game.view.Vector2;

/**
 * Allows to mark a GeoPoint as selected.
 * <p>
 * TODO Move to this class logic about marking GeoPoint 'selected' on UI.
 */
@Singleton
public final class PointerDrawer extends GameComponentBase {

    @Inject
    PointerState pointerState;

    @Inject
    SelectionTexture selectionTexture;

    @Override
    public void OnDraw(SpriteBatch spriteBatch) {
        var maybePosition = pointerState.getPosition();
        if (!maybePosition.isDefined()) return;
        var position = maybePosition.get();
        
        var x = position.x * Config.SpriteSize;
        var y = position.y * Config.SpriteSize;
        var selectionPosition = new Vector2(x - 1, y - 1);
        SpriteBatchUtils.draw(spriteBatch, selectionPosition, selectionTexture.getTexture());
    }
}
