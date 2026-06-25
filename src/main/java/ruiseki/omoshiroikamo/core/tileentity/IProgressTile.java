package ruiseki.omoshiroikamo.core.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.tileentity.ITile;

public interface IProgressTile extends ITile {

    float getProgress();

    @SideOnly(Side.CLIENT)
    void setProgress(float progress);
}
