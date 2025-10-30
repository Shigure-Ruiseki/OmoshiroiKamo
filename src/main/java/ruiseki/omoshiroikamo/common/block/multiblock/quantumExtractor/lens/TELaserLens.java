package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.lens;

import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTE;

public class TELaserLens extends AbstractTE {

    public TELaserLens(int meta) {
        this.meta = meta;
    }

    public TELaserLens() {
        this(0);
    }

    @Override
    public String getMachineName() {
        return ModObject.blockLaserLens.unlocalisedName;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    protected boolean processTasks(boolean redstoneCheckPassed) {
        return false;
    }

    @Override
    public void writeCommon(NBTTagCompound root) {

    }

    @Override
    public void readCommon(NBTTagCompound root) {

    }
}
