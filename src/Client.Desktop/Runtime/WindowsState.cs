using System;
using System.Collections.Generic;
using System.Text;

namespace Client.Desktop.Runtime
{
    interface WindowsState
    {
    }

    public record WindowStateMoving : WindowsState
    {
        int SpeedX { get; init; }
        int SpeedY { get; init; }
    }

    public record WindowStateStandbay : WindowsState
    {
    }

}
