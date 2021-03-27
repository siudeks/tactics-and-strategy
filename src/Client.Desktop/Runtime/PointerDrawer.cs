using Client.Desktop.Runtime;
using Client.Resources;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System;
using System.Diagnostics;
using System.Reactive.Disposables;

namespace Client.Runtime
{
    /// <summary>
    /// Allows to mark a GeoPoint as selected.
    /// 
    /// TODO Move to this class logic about marking GeoPoint 'selected' on UI.
    /// </summary>
    public sealed class PointerDrawer: IGameComponent, IBatchDrawer, IDisposable
    {
        private readonly IObservable<PointerState> pointerStream;
        private readonly CompositeDisposable componentDisposables = new CompositeDisposable();
        private readonly SelectionSprite sprite;
        private PointerState state;

        public PointerDrawer(IObservable<PointerState> pointerStream, SelectionSprite sprite)
        {
            this.pointerStream = pointerStream;
            this.sprite = sprite;
        }

        public void Initialize()
        {
            componentDisposables.Add(pointerStream.Subscribe(OnNewPointerState));
        }

        public void Dispose()
        {
            componentDisposables.Dispose();
        }

        private void OnNewPointerState(PointerState state)
        {
            this.state = state;
        }

        public void OnDraw(SpriteBatch spriteBatch)
        {
            Debug.WriteLine("Position: " + state.Position.X + "," + state.Position.Y);
            var selectionPoint = state.Position;
            var cameraSelectionPoint = new Point(selectionPoint.X * Config.SpriteSize, selectionPoint.Y * Config.SpriteSize);
            var selectionPosition = new Vector2(cameraSelectionPoint.X - 1, cameraSelectionPoint.Y - 1);
            spriteBatch.Draw(sprite.Texture, selectionPosition);

        }
    }
}
