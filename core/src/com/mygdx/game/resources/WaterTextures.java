package com.mygdx.game.resources;

import com.badlogic.gdx.math.Rectangle;
import com.google.inject.Singleton;
import com.mygdx.game.Config;
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
public class WaterTextures {
    public WaterTextures(TerrainTexture terrainTexture) {
        var texture = terrainTexture.getTexture();
        var ss = Config.SpriteSize;
        coastWithLandToTheNorth = new TextureHolder(texture, new Rectangle(4 * ss, 0 * ss, ss, ss));
        coastWithLandToTheSouth = new TextureHolder(texture, new Rectangle(10 * ss, 0 * ss, ss, ss));
        coastWithLandToTheWest = new TextureHolder(texture, new Rectangle(6 * ss, 0 * ss, ss, ss));
        coastWithLandToTheEast = new TextureHolder(texture, new Rectangle(18 * ss, 0 * ss, ss, ss));
        coastWithLandToTheNorthEast = new TextureHolder(texture, new Rectangle(14 * ss, 0 * ss, ss, ss)); 
        coastWithLandToTheNorthWest = new TextureHolder(texture, new Rectangle(6 * ss, 1 * ss, ss, ss));
        coastWithLandToTheSouthEast = new TextureHolder(texture, new Rectangle(8 * ss, 0 * ss, ss, ss));
        coastWithLandToTheSouthWest = new TextureHolder(texture, new Rectangle(0 * ss, 1 * ss, ss, ss)); 
        sea = new TextureHolder(texture, new Rectangle(2 * ss, 0 * ss, ss, ss));
    }
    private TextureHolder coastWithLandToTheNorth;
    private TextureHolder coastWithLandToTheSouth;
    private TextureHolder coastWithLandToTheWest;
    private TextureHolder coastWithLandToTheEast;
    private TextureHolder coastWithLandToTheNorthEast;
    private TextureHolder coastWithLandToTheNorthWest;
    private TextureHolder coastWithLandToTheSouthEast;
    private TextureHolder coastWithLandToTheSouthWest;
    private TextureHolder sea;
}
