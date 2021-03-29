using Autofac;

namespace Client.Domain
{
    class DiModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterInstance(new IntendedMapCentre()).AsSelf();
        }
    }
}
