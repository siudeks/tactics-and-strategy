using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace Client.View
{
    public sealed class XnaTextureHolder : TextureHolder
    {
        public readonly Texture2D Texture2D;
        public readonly Rectangle Source;
        public XnaTextureHolder(Texture2D texture, Rectangle source)
        {
            this.Texture2D = texture;
            this.Source = source;
        }
    }
}
