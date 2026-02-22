package ruiseki.omoshiroikamo.module.ids.core.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.omoshiroikamo.core.item.ItemOK;
import ruiseki.omoshiroikamo.module.ids.common.part.IPart;
import ruiseki.omoshiroikamo.module.ids.common.part.IPartContainer;
import ruiseki.omoshiroikamo.module.ids.common.part.IPartState;

/**
 * An item that can place parts.
 * 
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ItemPart<P extends IPart<P, S>, S extends IPartState<P>> extends ItemOK {

    private final IPart<P, S> part;

    public ItemPart(String name, IPart<P, S> part) {
        super(name);
        this.part = part;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.getTileEntity(x, y, z) instanceof IPartContainer partContainer) {
            if (!partContainer.hasPart(ForgeDirection.values()[side])) {
                partContainer.setPart(ForgeDirection.values()[side], getPart());
                System.out.println("Setting part " + getPart());
            } else {
                System.out.println("Side occupied!");
            }
            return true;
        } else {
            return super.onItemUse(stack, playerIn, world, x, y, z, side, hitX, hitY, hitZ);
        }
    }
}
