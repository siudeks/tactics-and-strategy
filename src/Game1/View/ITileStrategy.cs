namespace Game.View
{

    /// <summary>
    /// 
    /// </summary>
    public interface ITileStrategy
    {
        bool CanExecute(LocationType[] neighbors);

        TextureHolder Execute(LocationType[] neighbors);
    }
}
