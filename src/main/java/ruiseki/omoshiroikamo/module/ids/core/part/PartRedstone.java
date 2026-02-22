package ruiseki.omoshiroikamo.module.ids.core.part;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import lombok.Data;
import ruiseki.omoshiroikamo.core.datastructure.DimPos;
import ruiseki.omoshiroikamo.module.ids.common.part.EnumPartType;
import ruiseki.omoshiroikamo.module.ids.common.part.IPartContainer;
import ruiseki.omoshiroikamo.module.ids.common.part.IPartState;
import ruiseki.omoshiroikamo.module.ids.common.part.read.IPartRedstoneReader;
import ruiseki.omoshiroikamo.module.ids.common.part.write.IPartRedstoneWriter;

/**
 * A redstone I/O part.
 * 
 * @author rubensworks
 */
public class PartRedstone implements IPartRedstoneReader<PartRedstone, PartRedstone.PartRedstoneState>,
    IPartRedstoneWriter<PartRedstone, PartRedstone.PartRedstoneState> {

    @Override
    public EnumPartType getType() {
        return Parts.REDSTONE;
    }

    @Override
    public void toNBT(NBTTagCompound tag, PartRedstoneState partState) {
        System.out.println("WRITE");
        // TODO: abstract IPartState writing with parts of the TE NBTPersist annotation (this will require another
        // abstraction for that writing).
    }

    @Override
    public PartRedstoneState fromNBT(NBTTagCompound tag) {
        System.out.println("READ");
        return getDefaultState(); // TODO: abstract IPartState reading
    }

    @Override
    public PartRedstoneState getDefaultState() {
        return PartRedstoneState.of(0);
    }

    @Override
    public int getRedstoneLevel(IPartContainer partContainer, ForgeDirection side) {
        DimPos dimPos = partContainer.getPosition();
        return dimPos.getWorld()
            .getIndirectPowerLevelTo(
                dimPos.getBlockPos()
                    .getX(),
                dimPos.getBlockPos()
                    .getY(),
                dimPos.getBlockPos()
                    .getZ(),
                side.ordinal());
    }

    @Override
    public void setRedstoneLevel(IPartContainer partContainer, ForgeDirection side, int level) {
        partContainer.setPartState(side, PartRedstoneState.of(level));
    }

    @Data(staticConstructor = "of")
    public static class PartRedstoneState implements IPartState<PartRedstone> {

        private final int redstoneLevel;

    }

}
