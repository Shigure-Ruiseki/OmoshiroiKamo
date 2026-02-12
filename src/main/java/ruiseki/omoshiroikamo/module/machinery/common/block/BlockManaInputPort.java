package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.AbstractManaPortTE;
import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.input.TEManaInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.input.TEManaInputPortT1;
import vazkii.botania.api.mana.IManaBlock;
import vazkii.botania.api.wand.IWandHUD;

public class BlockManaInputPort extends AbstractPortBlock<TEManaInputPort> implements IWandHUD {

    protected BlockManaInputPort() {
        super(ModObject.blockModularManaInput.unlocalisedName, TEManaInputPortT1.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockManaInputPort create() {
        return new BlockManaInputPort();
    }

    @Override
    public void registerPortOverlays(IIconRegister reg) {
        IconRegistry.addIcon(
            "overlay_manainput_1",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_manainput_1"));
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockManaInputPort.class;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0));
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof IManaBlock handler) {
            tooltip.add(WailaUtils.getManaToolTip(handler));
        }
    }

    @Override
    public void renderHUD(Minecraft mc, ScaledResolution res, World world, int x, int y, int z) {
        ((AbstractManaPortTE) world.getTileEntity(x, y, z)).renderHUD(mc, res);
    }

    public static class ItemBlockManaInputPort extends AbstractPortItemBlock {

        public ItemBlockManaInputPort(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

        @Override
        public IIcon getOverlayIcon(int tier) {
            return IconRegistry.getIcon("overlay_manainput_" + tier);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            // TODO: Add tooltips
        }
    }

    @Override
    public Type getPortType() {
        return Type.MANA;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.INPUT;
    }
}
