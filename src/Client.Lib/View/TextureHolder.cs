namespace Client.View
{
    /// <summary>
    /// TextureHolder represents part of a texture. 
    /// 
    /// Because of Monogame/XNA limitations and to test applicatio nlogic without
    /// creating textures in unit test - so TextureHolder is a natural replacement of Texture2D.
    /// </summary>
    public class TextureHolder
    {
        /// <summary>
        /// Parameterless constructor used in tests to allow create an instance and make assertion.
        /// </summary>
        public TextureHolder() { }
    }
}