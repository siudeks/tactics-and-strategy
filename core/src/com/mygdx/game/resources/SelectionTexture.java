package com.mygdx.game.resources;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.google.inject.Singleton;
import com.mygdx.game.Config;
import com.mygdx.game.runtime.GameComponentBase;
import com.mygdx.game.view.IntRectangle;
import com.mygdx.game.view.TextureHolder;

import lombok.Getter;

@Singleton
public class SelectionTexture extends GameComponentBase {

  @Getter
  private TextureHolder texture;

  @Override
  public void loadTextures() {
    var size = Config.SpriteSize + 2;
    var pixmap = new Pixmap(size, size, Format.RGBA8888);
    var buffer = pixmap.getPixels().asIntBuffer();
    for (int x = 0; x < size; x++) 
      for (int y = 0; y < size; y++) {
            var color = Color.CLEAR;
            var index = x * size + y;
            if (x == 0 || x == size - 1) color = Color.RED;
            if (y == 0 || y == size - 1) color = Color.RED;
            buffer.put(index, Color.rgba8888(color));
        }
    var pixmapTexture = new Texture(pixmap);
    this.texture = new TextureHolder(pixmapTexture, IntRectangle.of(0, 0, size, size));
  }
}
