using Xunit;

namespace Game.View
{
    public sealed class WindowTest
    {
        public sealed class FocusOnUnit
        {
            /// <summary>
            /// * When Window is the first time initialized, should focus on a division
            /// </summary>
            [Fact]
            public void shouldFocusOnAnyDivision()
            {
                //var sut = new Window();
            }

            /// <summary>
            /// * When the first unit appear in list of units, window automatically will focus on the unit
            /// </summary>
            [Fact]
            public void shouldFocusOnFirstDivision()
            {
            }
        }
    }
}
