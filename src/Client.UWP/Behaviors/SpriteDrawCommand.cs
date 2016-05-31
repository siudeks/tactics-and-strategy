using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using System.Diagnostics;

namespace Client.Behaviors
{
    /// <summary>
    /// Declares operation context to draw a sprite.
    /// </summary>
    [DebuggerDisplay("X:{X} Y:{Y}")]
    public struct SpriteDrawCommand
    {
        public int X;
        public int Y;
    }
}
