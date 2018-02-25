using Client.View;

namespace Microsoft.Xna.Framework.Graphics
{
    public static class SpriteBatchExtensions
    {
        public static void Draw(this SpriteBatch spriteBatch, Vector2 position, TextureHolder texture)
        {
            // we need to draw only real XnaTextureHolder implementation.
            switch (texture)
            {
                case XnaTextureHolder t:
                    spriteBatch.Draw(t.Texture2D, position, t.Source, Color.White);
                    break;
                default:
                    break;
            }
            
        }
    }
}
