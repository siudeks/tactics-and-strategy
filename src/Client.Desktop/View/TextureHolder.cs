using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace Client.View
{
    /// <summary>
    /// TextureHolder represents part of a texture. Because of Monogame/XNA limitations, can' create texture
    /// in unit tests, so TextureHolder is natural replacement of Texture2D.
    /// THe calss can be extended for purpose of Dependency Injection to mark individual types types of textured
    /// </summary>
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