package ruiseki.omoshiroikamo.core.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IProgressTile extends ITile {

    float getProgress();

    @SideOnly(Side.CLIENT)
    void setProgress(float progress);
}
