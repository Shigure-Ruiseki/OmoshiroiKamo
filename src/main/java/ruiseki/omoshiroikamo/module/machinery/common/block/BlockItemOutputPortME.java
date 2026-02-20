package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortME;

/**
 * ME Item Output Port - outputs items directly to AE2 ME Network.
 * Requires Applied Energistics 2 to be loaded.
 * Uses JSON model with base + overlay textures via GTNHLib.
 */
public class BlockItemOutputPortME extends AbstractPortBlock<TEItemOutputPortME> {

    protected BlockItemOutputPortME() {
        super(ModObject.blockModularItemOutputME.unlocalisedName, TEItemOutputPortME.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockItemOutputPortME create() {
        return new BlockItemOutputPortME();
    }

    @Override
    public String getOverlayPrefix() {
        return "overlay_itemoutput_me";
    }

    @Override
    public void registerPortOverlays(IIconRegister reg) {
        IconRegistry.addIcon(
            "overlay_itemoutput_me",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_itemoutput_me"));
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockItemOutputPortME.class;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0));
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        dropStacks(world, x, y, z);
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TEItemOutputPortME me) {
            me.dropCachedItems();
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        TileEntity te = accessor.getTileEntity();
        if (te instanceof TEItemOutputPortME meTile) {
            if (meTile.isActive()) {
                tooltip.add("§a" + StatCollector.translateToLocal("waila.me.online"));
            } else if (meTile.isPowered()) {
                tooltip.add("§c" + StatCollector.translateToLocal("waila.me.no_channel"));
            } else {
                tooltip.add("§c" + StatCollector.translateToLocal("waila.me.offline"));
            }
        }
    }

    public static class ItemBlockItemOutputPortME extends AbstractPortItemBlock {

        public ItemBlockItemOutputPortME(Block block) {
            super(block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            return super.getUnlocalizedName() + ".me";
        }

        @Override
        public IIcon getOverlayIcon(int tier) {
            return IconRegistry.getIcon("overlay_itemoutput_me");
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            list.add("§7" + StatCollector.translateToLocal("tooltip.me_output.desc"));
            list.add("§e" + StatCollector.translateToLocal("tooltip.me_output.wip"));
            list.add("§c" + StatCollector.translateToLocal("tooltip.me_output.lag_warning"));
        }
    }

    @Override
    public Type getPortType() {
        return Type.ITEM;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }
}
