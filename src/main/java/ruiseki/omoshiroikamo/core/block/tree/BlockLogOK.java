package ruiseki.omoshiroikamo.core.block.tree;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.block.IBlock;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public class BlockLogOK extends BlockLog implements IBlock {

    private final String name;

    public BlockLogOK(String name) {
        super();
        this.name = name;
        this.setBlockName(name);
        this.setHardness(2.0F);
        this.setStepSound(soundTypeWood);
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, name);
    }

    @Override
    public Block getBlock() {
        return this;
    }

    @Override
    public boolean isHasSubtypes() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0));
    }

    public BlockLogOK setTextureName(String texture) {
        this.textureName = texture;
        return this;
    }

    @Override
    public String getTextureName() {
        return textureName == null ? name : textureName;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.field_150167_a = new IIcon[1];
        this.field_150166_b = new IIcon[1];
        this.field_150167_a = new IIcon[] { reg.registerIcon(LibResources.PREFIX_MOD + getTextureName()) };
        this.field_150166_b = new IIcon[] { reg.registerIcon(LibResources.PREFIX_MOD + getTextureName() + "_top") };
    }
}
