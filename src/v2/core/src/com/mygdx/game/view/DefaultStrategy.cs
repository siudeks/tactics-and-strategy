namespace Client.View
{
    // should be invoked at the end of strategies
    public class DefaultStrategy : ITileStrategy
    {
        private readonly TextureHolder texture;
        private object p;

        public DefaultStrategy(TextureHolder texture)
        {
            this.texture = texture;
        }

        public bool CanExecute(LocationType[] neighbors)
        {
            return true;
        }

        public TextureHolder Execute(LocationType[] neighbors)
        {
            return texture;
        }
    }
}
