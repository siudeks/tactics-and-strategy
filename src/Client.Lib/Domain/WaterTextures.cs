using Client.View;

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
    public class WaterTextures
    {
        public TextureHolder CoastWithLandToTheNorth { get; protected set; } = new TextureHolder();
        public TextureHolder CoastWithLandToTheSouth { get; protected set; } = new TextureHolder();
        public TextureHolder CoastWithLandToTheWest { get; protected set; } = new TextureHolder();
        public TextureHolder CoastWithLandToTheEast { get; protected set; } = new TextureHolder();
        public TextureHolder CoastWithLandToTheNorthEast { get; protected set; } = new TextureHolder();
        public TextureHolder CoastWithLandToTheNorthWest { get; protected set; } = new TextureHolder();
        public TextureHolder CoastWithLandToTheSouthEast { get; protected set; } = new TextureHolder();
        public TextureHolder CoastWithLandToTheSouthWest { get; protected set; } = new TextureHolder();
        public TextureHolder Sea { get; protected set; } = new TextureHolder();

        public WaterTextures()
        {
        }
    }
}
