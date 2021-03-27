using Client.Domain;
using System.Reactive.Subjects;
using Xunit;

namespace Client.Runtime
{
    public sealed class PointerDrawerShould
    {
        [Fact]
        public void ChangeSpriteColor()
        {
            var stream = new Subject<PointerState>();
            var tested = new PointerDrawer(stream);
            stream.OnNext(new PointerState { Position = new GeoPoint { X = 0, Y = 0 } });

            //tested.
            
        }
    }
}
