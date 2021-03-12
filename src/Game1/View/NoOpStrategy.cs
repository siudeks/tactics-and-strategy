namespace Game.View
{
    // should be invoked at the end of strategies
    public class NoOpStrategy : ITileStrategy
    {
        private readonly TextureHolder texture;

        public NoOpStrategy(TextureHolder texture)
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
