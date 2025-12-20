package ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTieredMBBlock;

public class BlockQuantumBeacon extends AbstractTieredMBBlock<TEQuantumBeacon> {

    protected BlockQuantumBeacon() {
        super(
            ModObject.blockQuantumBeacon.unlocalisedName,
            TEQuantumBeaconT1.class,
            TEQuantumBeaconT2.class,
            TEQuantumBeaconT3.class,
            TEQuantumBeaconT4.class,
            TEQuantumBeaconT5.class,
            TEQuantumBeaconT6.class);
        this.setLightLevel(0.2F);
    }

    public static BlockQuantumBeacon create() {
        return new BlockQuantumBeacon();
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
        list.add(new ItemStack(this, 1, 2));
        list.add(new ItemStack(this, 1, 3));
        list.add(new ItemStack(this, 1, 4));
        list.add(new ItemStack(this, 1, 5));
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockQuantumBeacon.class;
    }

    public static class ItemBlockQuantumBeacon extends ItemBlockOK {

        public ItemBlockQuantumBeacon(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

    }

}
