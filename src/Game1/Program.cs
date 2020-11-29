using System;

namespace Client.Desktop
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
