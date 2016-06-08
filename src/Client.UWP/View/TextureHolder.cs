using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace Client.View
{
    public class TextureHolder
    {
        /// <summary>
        /// Parameterless constructor used in tests to allow create an instance and make assertion.
        /// </summary>
        public TextureHolder() { }

        public readonly Texture2D Texture2D;
        public readonly Rectangle Source;
        public TextureHolder(Texture2D texture, Rectangle source)
        {
            this.Texture2D = texture;
            this.Source = source;
        }
    }
}