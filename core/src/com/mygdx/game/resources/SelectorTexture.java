package com.mygdx.game.resources;

import com.mygdx.game.runtime.GameComponentBase;

public class SelectorTexture extends GameComponentBase {
  
}

    //     // non-testable method because Texture2D can't be created in unit tests.
    //     private static Texture2D CreateSelectorTexture(GraphicsDevice device)
    //     {
    //         var size = Config.SpriteSize + 2;
    //         var texture = device.CreateTexture(size, size);

    //         var colors = new Color[size * size];
    //         texture.GetData(colors);
    //         for (int x = 0; x < size; x++)
    //             for (int y = 0; y < size; y++)
    //             {
    //                 var color = Color.Transparent;
    //                 var i = y * size + x;
    //                 if (x == 0 || x == size - 1) color = Color.Red;
    //                 if (y == 0 || y == size - 1) color = Color.Red;

    //                 colors[i] = color;

    //             }
    //         texture.SetData(colors);
    //         return texture;
    //     }

    // }