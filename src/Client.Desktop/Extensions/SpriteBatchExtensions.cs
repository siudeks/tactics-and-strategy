using Client.View;

namespace Microsoft.Xna.Framework.Graphics
{
    public static class SpriteBatchExtensions
    {
        public static void Draw(this SpriteBatch spriteBatch, Vector2 position, TextureHolder texture)
        {
            spriteBatch.Draw(texture.Texture2D, position, texture.Source, Color.White);
        }
    }
}
