using Autofac;
using Client.Resources;
using Client.Runtime;
using Microsoft.Xna.Framework;
using System;
using System.Reactive.Subjects;

namespace Client.View
{
    public class DiModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<Window>().As<IGameComponent, Window, ITextureConsumer>().SingleInstance();
        }
    }
}
