namespace Client.View
{

    public interface ITileStrategy
    {
        bool CanExecute(LocationType[] neighbors);

        TextureHolder Execute(LocationType[] neighbors);
    }
}
