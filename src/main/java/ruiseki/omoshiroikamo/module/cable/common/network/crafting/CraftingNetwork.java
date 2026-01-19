package ruiseki.omoshiroikamo.module.cable.common.network.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.item.ItemStackKeyPool;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractCableNetwork;

public class CraftingNetwork extends AbstractCableNetwork<ICraftingNet> {

    private final CraftingIndex craftingIndex = new CraftingIndex();

    public CraftingNetwork() {
        super(ICraftingNet.class);
        craftingIndex.addCraftable(ItemStackKeyPool.get(new ItemStack(Blocks.crafting_table)));
    }

    public CraftingIndex getIndex() {
        return craftingIndex;
    }
}
