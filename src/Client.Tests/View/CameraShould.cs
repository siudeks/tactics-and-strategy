using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using Microsoft.Xna.Framework;

namespace Client.View
{
    [TestClass]
    public sealed class CameraShould
    {
        /// <summary>
        /// Converts locations in pixels
        /// in Monogame, left top corner represents point (0, 0) what is natural
        /// for developers but not especially with math - I decided to use left 
        /// </summary>
        [TestMethod]
        public void ConvertCoordinates()
        {
            var camera = new Camera(100, 200, 10);
            Assert.AreEqual(new Point(0, 200 - 10), camera.View(new Point(0, 0)));
            Assert.AreEqual(new Point(3, 200 - 13), camera.View(new Point(3, 3)));
        }
    }
}
