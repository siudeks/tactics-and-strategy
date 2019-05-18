using Client.View;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace Client.Desktop.Native
{
    public class SpriteFactory
    {
        private GraphicsDevice device;
        public SpriteFactory(GraphicsDevice device)
        {
            this.device = device;
        }

        public TextureHolder CreateSelectorTexture()
        {
            var size = Config.SpriteSize + 2;
            var texture = device.CreateTexture(size, size);

            var colors = new Color[size * size];
            texture.GetData(colors);
            for (int x = 0; x < size; x++)
                for (int y = 0; y < size; y++)
                {
                    var color = Color.Transparent;
                    var i = y * size + x;
                    if (x == 0 || x == size - 1) color = Color.Red;
                    if (y == 0 || y == size - 1) color = Color.Red;

                    colors[i] = color;

                }
            texture.SetData(colors);
            return new TextureHolder(texture, new Rectangle(Point.Zero, new Point(size)));
        }
    }
}
