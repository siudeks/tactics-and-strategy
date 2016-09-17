using Client.View;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace Client.Domain
{
    /// <summary>
    /// Extracts and holds textures for Water map
    /// </summary>
    /// <remarks>
    /// All textures are hold in one big texture, so need to have separated logic which knowledge
    /// where are located particular types of some terrain type.
    /// In that case, <see cref="WaterTextures"/> is responsible to load water tiles located in
    /// some known places in big texture.
    /// </remarks>
    public sealed class WaterTextures
    {
        public TextureHolder CoastWithLandToTheNorth { get; private set; } = new TextureHolder();
        public TextureHolder CoastWithLandToTheSouth { get; private set; } = new TextureHolder();
        public TextureHolder CoastWithLandToTheWest { get; private set; } = new TextureHolder();
        public TextureHolder CoastWithLandToTheEast { get; private set; } = new TextureHolder();
        public TextureHolder CoastWithLandToTheNorthEast { get; private set; } = new TextureHolder();
        public TextureHolder CoastWithLandToTheNorthWest { get; private set; } = new TextureHolder();
        public TextureHolder CoastWithLandToTheSouthEast { get; private set; } = new TextureHolder();
        public TextureHolder CoastWithLandToTheSouthWest { get; private set; } = new TextureHolder();
        public TextureHolder Sea { get; private set; } = new TextureHolder();

        public WaterTextures()
        {
        }

        public WaterTextures(Texture2D terrain)
        {
            var spriteSize = new Point(32, 32);
            var ss = Config.SpriteSize;
            CoastWithLandToTheNorth = new TextureHolder(terrain, new Rectangle(new Point(4 * ss, 0 * ss), spriteSize));
            CoastWithLandToTheSouth = new TextureHolder(terrain, new Rectangle(new Point(10 * ss, 0 * ss), spriteSize));
            CoastWithLandToTheWest = new TextureHolder(terrain, new Rectangle(new Point(6 * ss, 0 * ss), spriteSize));
            CoastWithLandToTheEast = new TextureHolder(terrain, new Rectangle(new Point(18 * ss, 0 * ss), spriteSize));
            CoastWithLandToTheNorthEast = new TextureHolder(terrain, new Rectangle(new Point(14 * ss, 0 * ss), spriteSize)); 
            CoastWithLandToTheNorthWest = new TextureHolder(terrain, new Rectangle(new Point(6 * ss, 1 * ss), spriteSize));
            CoastWithLandToTheSouthEast = new TextureHolder(terrain, new Rectangle(new Point(8 * ss, 0 * ss), spriteSize));
            CoastWithLandToTheSouthWest = new TextureHolder(terrain, new Rectangle(new Point(0 * ss, 1 * ss), spriteSize)); 
            Sea = new TextureHolder(terrain, new Rectangle(new Point(2 * ss, 0 * ss), spriteSize));
        }
    }
}
