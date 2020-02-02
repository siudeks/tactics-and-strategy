using Client.Domain;
using Client.View;
using Xunit;

namespace Client
{
    /// <summary>
    /// UnitSelector allows to select provided Unit
    /// </summary>
    public sealed class UnitSelectorSpec
    {
        [Fact]
        public void ShouldSelectUnit()
        {
            //var map = new Map();
            //map.Include(new LandUnitEntity());

            //var window = new Window(new Domain.WaterTextures(), new TextureHolder(), new NoOpStrategy(), new ITileStrategy[0]);
            //var selector =
        }

        class NoOpStrategy : ITileStrategy
        {
            public bool CanExecute(LocationType[] neighbors) => false;

            public TextureHolder Execute(LocationType[] neighbors) => new TextureHolder();
        }
    }
}
