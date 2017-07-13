namespace Client.View
{
    public class LandUnitStrategy : ITileStrategy
    {
        private readonly TextureHolder texture;

        public LandUnitStrategy(TextureHolder landUnitTexture)
        {
            texture = landUnitTexture;
        }

        public bool CanExecute(LocationType[] neighbors)
        {
            return neighbors[Directions.NeighborThis] == LocationType.LandUnit;
        }

        public TextureHolder Execute(LocationType[] neighbors)
        {
            return texture;
        }
    }
}
