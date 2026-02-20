package ruiseki.omoshiroikamo.module.chickens.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.item.ItemOK;

public class ItemSolidXp extends ItemOK {

    public ItemSolidXp() {
        super(ModObject.itemSolidXp);
        setTextureName("chicken/solid_xp");
    }

    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityXPOrb(world, location.posX, location.posY, location.posZ, 25 * itemstack.stackSize);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        ItemStack itemStackIn = player.getHeldItem();

        if (!player.capabilities.isCreativeMode) {
            itemStackIn.stackSize--;
        }

        world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!world.isRemote) {
            EntityXPOrb entityexp = new EntityXPOrb(world, player.posX, player.posY + 1, player.posZ, 25);
            world.spawnEntityInWorld(entityexp);
        }
        return true;
    }
}
