using Autofac;
using Client.Runtime;
using Microsoft.Xna.Framework;
using System;
using System.Reactive.Subjects;

namespace Client.Desktop.Runtime
{
    public class DiModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<WindowMoveProcessor>().As<IGameComponent>();
            builder.RegisterType<PointerObserver>().As<IGameComponent>();
            builder.RegisterType<PointerDrawer>().As<IGameComponent, IBatchDrawer>().SingleInstance();

            var pointerStateStream = new BehaviorSubject<PointerState>(new PointerState());
            builder.RegisterInstance(pointerStateStream).As<IObserver<PointerState>, IObservable<PointerState>> ();
        }
    }
}
