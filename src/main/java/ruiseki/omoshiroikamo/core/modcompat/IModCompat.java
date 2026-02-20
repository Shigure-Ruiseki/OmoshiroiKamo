package ruiseki.omoshiroikamo.core.modcompat;

/**
 * Interface for external mod compatibilities.
 * 
 * @see IExternalCompat
 * @author rubensworks
 *
 */
public interface IModCompat extends IExternalCompat {

    /**
     * Get the unique mod ID.
     * 
     * @return The mod ID.
     */
    public String getModID();

}
