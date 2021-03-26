using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Client.Desktop.Runtime
{
    class WindowMoveProcessor : IUpdateable, IGameComponent
    {

        private IEnumerable<StateProcessor<WindowsState>> strategies = [];

        public event EventHandler<EventArgs> EnabledChanged;
        public event EventHandler<EventArgs> UpdateOrderChanged;

        public bool Enabled => true;

        public int UpdateOrder => 0;

        public void Initialize()
        {
        }


        public void Update(GameTime gameTime)
        {
        }
    }
}
