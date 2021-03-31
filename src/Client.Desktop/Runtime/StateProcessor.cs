
namespace Client.Desktop.Runtime
{
    interface StateProcessor<T>
    {
        bool CanProcess(T state);
        T Process(T current);
    }
}
