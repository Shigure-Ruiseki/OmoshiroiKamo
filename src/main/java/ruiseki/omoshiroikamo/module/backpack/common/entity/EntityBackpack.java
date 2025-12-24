package ruiseki.omoshiroikamo.module.backpack.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.common.entity.EntityImmortalItem;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackHandler;

public class EntityBackpack extends EntityImmortalItem {

    public EntityBackpack(World world, Entity original, ItemStack stack, BackpackHandler handler) {
        super(world, original, stack);
        setImmortal(handler.canImportant());
    }
}
