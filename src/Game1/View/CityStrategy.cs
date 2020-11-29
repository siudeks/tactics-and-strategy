namespace Game.View
{
    public sealed class CityStrategy : ITileStrategy
    {
        private readonly TextureHolder texture;

        public CityStrategy(TextureHolder texture)
        {
            this.texture = texture;
        }

        public bool CanExecute(LocationType[] neighbors)
        {
            return neighbors[Directions.NeighborThis] == LocationType.City;

        }

        public TextureHolder Execute(LocationType[] neighbors)
        {
            return texture;
        }
    }
}
