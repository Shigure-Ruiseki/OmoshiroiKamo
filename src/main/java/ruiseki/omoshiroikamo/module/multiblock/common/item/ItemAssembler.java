package ruiseki.omoshiroikamo.module.multiblock.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.gtnewhorizon.structurelib.alignment.constructable.ConstructableUtility;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.IMBBlock;
import ruiseki.omoshiroikamo.core.common.util.TooltipUtils;
import ruiseki.omoshiroikamo.core.item.ItemOK;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public class ItemAssembler extends ItemOK {

    public ItemAssembler() {
        super(ModObject.itemAssembler);
        setMaxStackSize(1);
        setNoRepair();
        setTextureName("multiblock/assembler");
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        return ConstructableUtility.handle(stack, player, world, x, y, z, side);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, int x, int y, int z,
        EntityLivingBase entity) {

        if (isValidSolarBlock(blockIn)) {
            return true;
        }

        return super.onBlockDestroyed(stack, worldIn, blockIn, x, y, z, entity);
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack itemStack) {
        return isValidSolarBlock(block);
    }

    @Override
    public float getDigSpeed(ItemStack itemstack, Block block, int metadata) {
        if (isValidSolarBlock(block)) {
            return 50.0F;
        }
        return 0f;
    }

    private boolean isValidSolarBlock(Block block) {
        return block instanceof IMBBlock;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        TooltipUtils builder = TooltipUtils.builder();
        builder.addLang(LibResources.TOOLTIP + "assembler.l1");
        builder.addLang(LibResources.TOOLTIP + "assembler.l2");
        list.addAll(builder.build());
    }
}
