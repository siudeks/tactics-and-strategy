package com.mygdx.game.resources;

import com.badlogic.gdx.math.Rectangle;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mygdx.game.Config;
import com.mygdx.game.runtime.GameComponentBase;
import com.mygdx.game.view.IntRectangle;
import com.mygdx.game.view.TextureHolder;

import lombok.Getter;

/**
 *  Extracts and holds textures for Water map
 *
 *  All textures are hold in one big texture, so need to have separated logic which knowledge
 *  where are located particular types of some terrain type.
 *  In that case, {@see WaterTextures> is responsible to load water tiles located in
 *  some known places in big texture.
 * 
 */
@Singleton
@Getter
public class WaterTextures extends GameComponentBase {

    private TerrainTexture terrainTexture;
    
    @Inject
    public WaterTextures(TerrainTexture terrainTexture) {
        this.terrainTexture = terrainTexture;
    }

    private TextureHolder landNorth;
    private TextureHolder landSouth;
    private TextureHolder landWest;
    private TextureHolder landEast;
    private TextureHolder landSouthEast;
    private TextureHolder landSouthWest;
    private TextureHolder landNorthEast;
    private TextureHolder landNorthWest;
    private TextureHolder sea;
    private TextureHolder blank;

    @Override
    public void useTextures() {
        var texture = terrainTexture.getTexture();
        var ss = Config.SpriteSize;
        landNorth = new TextureHolder(texture, IntRectangle.of(4 * ss, 0 * ss, ss, ss));
        landSouth = new TextureHolder(texture, IntRectangle.of(10 * ss, 0 * ss, ss, ss));
        landEast = new TextureHolder(texture, IntRectangle.of(6 * ss, 0 * ss, ss, ss));
        landWest = new TextureHolder(texture, IntRectangle.of(18 * ss, 0 * ss, ss, ss));
        landSouthEast = new TextureHolder(texture, IntRectangle.of(14 * ss, 0 * ss, ss, ss)); 
        landSouthWest = new TextureHolder(texture, IntRectangle.of(6 * ss, 1 * ss, ss, ss));
        landNorthEast = new TextureHolder(texture, IntRectangle.of(8 * ss, 0 * ss, ss, ss));
        landNorthWest = new TextureHolder(texture, IntRectangle.of(0 * ss, 1 * ss, ss, ss)); 
        sea = new TextureHolder(texture, IntRectangle.of(2 * ss, 0 * ss, ss, ss));
        blank = new TextureHolder(texture, IntRectangle.of(15 * ss, 8 * ss, ss, ss));
    }
}
