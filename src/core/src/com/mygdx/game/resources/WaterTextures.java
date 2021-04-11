package com.mygdx.game.resources;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Config;
import com.mygdx.game.view.TextureHolder;

import lombok.Value;

/**
 *  Extracts and holds textures for Water map
 *
 *  All textures are hold in one big texture, so need to have separated logic which knowledge
 *  where are located particular types of some terrain type.
 *  In that case, {@see WaterTextures> is responsible to load water tiles located in
 *  some known places in big texture.
 * 
 */
@Value
public class WaterTextures {
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
