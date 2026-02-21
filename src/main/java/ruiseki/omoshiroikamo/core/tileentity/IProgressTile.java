package ruiseki.omoshiroikamo.core.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IProgressTile extends IOKTile {

    float getProgress();

    /**
     * Client-only. Called to set clientside progress for syncing/rendering purposes.
     *
     * @param progress The % progress.
     */
    @SideOnly(Side.CLIENT)
    void setProgress(float progress);
}
