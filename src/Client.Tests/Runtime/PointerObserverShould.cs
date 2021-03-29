using Microsoft.Xna.Framework;
using System.Reactive.Subjects;
using System.Threading.Tasks;
using System.Reactive.Linq;
using Client.Domain;
using Xunit;
using FluentAssertions;

namespace Client.Runtime
{
    public sealed class PointerObserverShould
    {
        [Fact(Timeout = 1000)]
        public async Task EmitPointerStateWhenUpdated()
        {
            var points = new ReplaySubject<PointerState>();
            var observer = new PointerObserver(points);

            observer.Update(new GameTime(), Point.Zero);
            new PointerState { Position = new GeoPoint() }.Should().BeEquivalentTo(await points.FirstAsync());

            observer.Update(new GameTime(), new Point(Config.SpriteSize, Config.SpriteSize));
            new PointerState { Position = new GeoPoint { X = 1, Y = 1 } }.Should().BeEquivalentTo(await points.Skip(1).FirstAsync());
        }
    }
}
