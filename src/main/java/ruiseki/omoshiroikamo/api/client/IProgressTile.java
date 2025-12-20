package ruiseki.omoshiroikamo.api.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.block.IOKTile;

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
