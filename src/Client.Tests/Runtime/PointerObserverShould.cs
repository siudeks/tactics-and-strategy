using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using Microsoft.Xna.Framework;
using System.Reactive.Subjects;
using System.Threading.Tasks;
using System.Reactive.Linq;
using Client.Domain;

namespace Client.Runtime
{
    [TestClass]
    public sealed class PointerObserverShould
    {
        [TestMethod, Timeout(1000)]
        public async Task EmitPointerStateWhenUpdated()
        {
            var points = new ReplaySubject<PointerState>();
            var observer = new PointerObserver(points);
            observer.Update(new GameTime(), Point.Zero);

            var expected = await points.FirstAsync();
            Assert.AreEqual(expected, new PointerState { Position = new GeoPoint() });
        }
    }
}
