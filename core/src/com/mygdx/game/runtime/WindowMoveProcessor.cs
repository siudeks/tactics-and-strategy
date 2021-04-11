using Client.Domain;
using Microsoft.Xna.Framework;
using System;

namespace Client.Desktop.Runtime
{
    class WindowMoveProcessor : IUpdateable, IGameComponent
    {

        // private IEnumerable<StateProcessor<WindowsState>> strategies = [];

        public event EventHandler<EventArgs> EnabledChanged;
        public event EventHandler<EventArgs> UpdateOrderChanged;

        public IntendedMapCentre IntendedMapCentre { get; set; }

        public bool Enabled => true;

        public int UpdateOrder => 0;

        public void Initialize()
        {
        }


        private int totalSeconds = 0;
        public void Update(GameTime gameTime)
        {
            var currentTotalSeconds = (int) gameTime.TotalGameTime.TotalSeconds;
            if (currentTotalSeconds == totalSeconds) return;

            totalSeconds = currentTotalSeconds;
            OnUpdate();
        }

        private void OnUpdate()
        {
            IntendedMapCentre.X = IntendedMapCentre.X + 1;
            IntendedMapCentre.Y = IntendedMapCentre.Y + 1;
        }
    }
}
