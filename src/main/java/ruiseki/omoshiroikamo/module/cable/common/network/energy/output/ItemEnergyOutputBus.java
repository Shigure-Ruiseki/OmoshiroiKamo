package ruiseki.omoshiroikamo.module.cable.common.network.energy.output;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.cable.ICable;
import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.cable.ICablePartItem;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.item.ItemOK;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.IEnergyPart;

public class ItemEnergyOutputBus extends ItemOK implements ICablePartItem {

    public ItemEnergyOutputBus() {
        super(ModObject.itemEnergyOutputBus.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public ICablePart createPart() {
        return new EnergyOutputBus();
    }

    @Override
    public Class<? extends ICablePart> getBasePartType() {
        return IEnergyPart.class;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof ICable cable)) return false;

        ForgeDirection dir = ForgeDirection.getOrientation(side);

        if (cable.hasPart(dir)) return false;

        ICablePart part = createPart();
        cable.setPart(dir, part);

        if (!player.capabilities.isCreativeMode) {
            stack.stackSize--;
        }

        return true;
    }
}
