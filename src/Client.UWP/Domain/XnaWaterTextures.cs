using Client.View;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace Client.Domain
{
    public sealed class XnaWaterTextures : WaterTextures
    {
        public XnaWaterTextures(Texture2D terrain)
        {
            var spriteSize = new Point(32, 32);
            var ss = Config.SpriteSize;
            CoastWithLandToTheNorth = new XnaTextureHolder(terrain, new Rectangle(new Point(4 * ss, 0 * ss), spriteSize));
            CoastWithLandToTheSouth = new XnaTextureHolder(terrain, new Rectangle(new Point(10 * ss, 0 * ss), spriteSize));
            CoastWithLandToTheWest = new XnaTextureHolder(terrain, new Rectangle(new Point(6 * ss, 0 * ss), spriteSize));
            CoastWithLandToTheEast = new XnaTextureHolder(terrain, new Rectangle(new Point(18 * ss, 0 * ss), spriteSize));
            CoastWithLandToTheNorthEast = new XnaTextureHolder(terrain, new Rectangle(new Point(14 * ss, 0 * ss), spriteSize));
            CoastWithLandToTheNorthWest = new XnaTextureHolder(terrain, new Rectangle(new Point(6 * ss, 1 * ss), spriteSize));
            CoastWithLandToTheSouthEast = new XnaTextureHolder(terrain, new Rectangle(new Point(8 * ss, 0 * ss), spriteSize));
            CoastWithLandToTheSouthWest = new XnaTextureHolder(terrain, new Rectangle(new Point(0 * ss, 1 * ss), spriteSize));
            Sea = new XnaTextureHolder(terrain, new Rectangle(new Point(2 * ss, 0 * ss), spriteSize));
        }

    }
}
