package com.mygdx.game.extensions;

namespace Microsoft.Xna.Framework.Graphics
{
    public static class GraphicDeviceExtensions
    {
        public static Texture2D CreateTexture(this GraphicsDevice device, int width, int height)
        {
            return new Texture2D(device, width, height);
        }
    }
}

