using Client.Domain;
using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System.Reactive.Subjects;

namespace Client.Runtime
{
    [TestClass]
    public sealed class PointerDrawerShould
    {
        [TestMethod]
        public void ChangeSpriteColor()
        {
            var stream = new Subject<PointerState>();
            var tested = new PointerDrawer(stream);
            stream.OnNext(new PointerState { Position = new GeoPoint { X = 0, Y = 0 } });

            //tested.
            
        }
    }
}
