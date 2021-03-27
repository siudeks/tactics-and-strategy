using Microsoft.Xna.Framework.Graphics;

namespace Client.Resources
{
    public interface ITextureConsumer
    {
        void OnLoaded(Texture2D texture, TextureItem item);
        void LoadFinished();
    }

    public enum TextureItem
    { 
        TERRAIN,
        DESERT_RATES
    }

}
