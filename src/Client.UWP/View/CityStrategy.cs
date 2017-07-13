using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Client.View
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
