package com.mygdx.game.resources;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Config;
import com.mygdx.game.view.TextureHolder;

import lombok.Builder;
import lombok.Value;

/**
 *  Extracts and holds textures for Water map
 *
 *  All textures are hold in one big texture, so need to have separated logic which knowledge
 *  where are located particular types of some terrain type.
 *  In that case, {@see WaterTextures> is responsible to load water tiles located in
 *  some known places in big texture.
 */
@Value
@Builder
public final class WaterTextures {
    @Builder.Default
    private TextureHolder CoastWithLandToTheNorth = new TextureHolder();
    @Builder.Default
    private TextureHolder CoastWithLandToTheSouth = new TextureHolder();
    @Builder.Default
    private TextureHolder CoastWithLandToTheWest = new TextureHolder();
    @Builder.Default
    private TextureHolder CoastWithLandToTheEast = new TextureHolder();
    @Builder.Default
    private TextureHolder CoastWithLandToTheNorthEast = new TextureHolder();
    @Builder.Default
    private TextureHolder CoastWithLandToTheNorthWest = new TextureHolder();
    @Builder.Default
    private TextureHolder CoastWithLandToTheSouthEast = new TextureHolder();
    @Builder.Default
    private TextureHolder CoastWithLandToTheSouthWest = new TextureHolder();
    @Builder.Default
    private TextureHolder Sea = new TextureHolder();

    public WaterTextures() {
    }

    public WaterTextures(Texture terrain)
    {
        var ss = Config.SpriteSize;
        CoastWithLandToTheNorth = new TextureHolder(terrain, new Rectangle(4 * ss, 0 * ss, ss, ss));
        CoastWithLandToTheSouth = new TextureHolder(terrain, new Rectangle(10 * ss, 0 * ss, ss, ss));
        CoastWithLandToTheWest = new TextureHolder(terrain, new Rectangle(6 * ss, 0 * ss, ss, ss));
        CoastWithLandToTheEast = new TextureHolder(terrain, new Rectangle(18 * ss, 0 * ss, ss, ss));
        CoastWithLandToTheNorthEast = new TextureHolder(terrain, new Rectangle(14 * ss, 0 * ss, ss, ss)); 
        CoastWithLandToTheNorthWest = new TextureHolder(terrain, new Rectangle(6 * ss, 1 * ss, ss, ss));
        CoastWithLandToTheSouthEast = new TextureHolder(terrain, new Rectangle(8 * ss, 0 * ss, ss, ss));
        CoastWithLandToTheSouthWest = new TextureHolder(terrain, new Rectangle(0 * ss, 1 * ss, ss, ss)); 
        Sea = new TextureHolder(terrain, new Rectangle(2 * ss, 0 * ss, ss, ss));
    }
}

