package com.mygdx.game.resources;

import com.mygdx.game.Config;
import com.mygdx.game.view.TextureHolder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class WaterTexturesUtils {

  public static WaterTextures create(Texture terrain) {
    var ss = Config.SpriteSize;
    var CoastWithLandToTheNorth = new TextureHolder(terrain, new Rectangle(4 * ss, 0 * ss, ss, ss));
    var CoastWithLandToTheSouth = new TextureHolder(terrain, new Rectangle(10 * ss, 0 * ss, ss, ss));
    var CoastWithLandToTheWest = new TextureHolder(terrain, new Rectangle(6 * ss, 0 * ss, ss, ss));
    var CoastWithLandToTheEast = new TextureHolder(terrain, new Rectangle(18 * ss, 0 * ss, ss, ss));
    var CoastWithLandToTheNorthEast = new TextureHolder(terrain, new Rectangle(14 * ss, 0 * ss, ss, ss)); 
    var CoastWithLandToTheNorthWest = new TextureHolder(terrain, new Rectangle(6 * ss, 1 * ss, ss, ss));
    var CoastWithLandToTheSouthEast = new TextureHolder(terrain, new Rectangle(8 * ss, 0 * ss, ss, ss));
    var CoastWithLandToTheSouthWest = new TextureHolder(terrain, new Rectangle(0 * ss, 1 * ss, ss, ss)); 
    var Sea = new TextureHolder(terrain, new Rectangle(2 * ss, 0 * ss, ss, ss));
    return new WaterTextures(CoastWithLandToTheNorth,
      CoastWithLandToTheSouth,
      CoastWithLandToTheWest,
      CoastWithLandToTheEast,
      CoastWithLandToTheNorthEast,
      CoastWithLandToTheNorthWest,
      CoastWithLandToTheSouthEast,
      CoastWithLandToTheSouthWest,
      Sea);
  }
}
