namespace Client.View
{
    public sealed class CoastWithLandToTheNorthStrategy : ITileStrategy
    {
        private readonly TextureHolder texture;

        public CoastWithLandToTheNorthStrategy(TextureHolder texture)
        {
            this.texture = texture;
        }

        public bool CanExecute(LocationType[] neighbors)
        {
            if (neighbors[Directions.NeighborWest] != LocationType.Water) return false;
            if (neighbors[Directions.NeighborEast] != LocationType.Water) return false;
            if (neighbors[Directions.NeighborNorth] != LocationType.Water) return false;
            if (neighbors[Directions.NeighborSouth] == LocationType.Water) return false;
            if (neighbors[Directions.NeighborThis] != LocationType.Water) return false;

            return true;

        }

        public TextureHolder Execute(LocationType[] neighbors)
        {
            return texture;
        }
    }
}
