package ruiseki.omoshiroikamo.core.modcompat;

/**
 * Interface for external api compatibilities.
 * 
 * @see IExternalCompat
 * @author rubensworks
 *
 */
public interface IApiCompat extends IExternalCompat {

    /**
     * Get the unique mod ID.
     * 
     * @return The mod ID.
     */
    public String getApiID();

}
