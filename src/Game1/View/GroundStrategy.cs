namespace Game.View
{
    public sealed class GroundStrategy : ITileStrategy
    {
        private readonly TextureHolder texture;

        public GroundStrategy(TextureHolder texture)
        {
            this.texture = texture;
        }
        public bool CanExecute(LocationType[] neighbors)
        {
            return neighbors[Directions.NeighborThis] == LocationType.Ground;
        }

        public TextureHolder Execute(LocationType[] neighbors)
        {
            return texture;
        }
    }
}
