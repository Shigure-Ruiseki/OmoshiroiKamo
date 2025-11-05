package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.core;

import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTE;

public class TELaserCore extends AbstractTE {

    @Override
    public String getMachineName() {
        return ModObject.blockLaserCore.unlocalisedName;
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
