using System;

namespace Game.Desktop
{
    public static class Program
    {
        [STAThread]
        static void Main()
        {
            using (var game = new GameApp())
                game.Run();
        }
    }
}
