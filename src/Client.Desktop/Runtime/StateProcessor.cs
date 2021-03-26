using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Client.Desktop.Runtime
{
    interface StateProcessor<T>
    {
        bool CanProcess(T state);
        T Process(T current);
    }
}
