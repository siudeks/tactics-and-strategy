using Game.Domain;
using Game.View;
using Xunit;

namespace Game
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
