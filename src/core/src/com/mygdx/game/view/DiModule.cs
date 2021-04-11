using Autofac;
using Client.Desktop.Runtime;
using Client.Resources;
using Microsoft.Xna.Framework;

namespace Client.View
{
    public class DiModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<Window>().As<IGameComponent, IBatchDrawer, ITextureConsumer>().SingleInstance().PropertiesAutowired();
        }
    }
}
