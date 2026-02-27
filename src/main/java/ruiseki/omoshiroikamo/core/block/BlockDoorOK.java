package ruiseki.omoshiroikamo.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public class BlockDoorOK extends BlockDoor implements IBlock {

    public final String name;

    protected BlockDoorOK(String name) {
        super(Material.wood);
        this.name = name;
        setBlockName(name);
        disableStats();
        setHardness(3.0F);
        setStepSound(soundTypeWood);
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockDoor.class, name);
    }

    @Override
    public Block getBlock() {
        return this;
    }

    @Override
    public boolean isHasSubtypes() {
        return false;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.field_150017_a = new IIcon[2];
        this.field_150016_b = new IIcon[2];
        this.field_150017_a[0] = reg.registerIcon(LibResources.PREFIX_MOD + this.getTextureName() + "_upper");
        this.field_150016_b[0] = reg.registerIcon(LibResources.PREFIX_MOD + this.getTextureName() + "_lower");
        this.field_150017_a[1] = new IconFlipped(this.field_150017_a[0], true, false);
        this.field_150016_b[1] = new IconFlipped(this.field_150016_b[0], true, false);
    }

    public static class ItemBlockDoor extends ItemBlock {

        public ItemBlockDoor(Block block) {
            super(block);
        }

        @Override
        public void registerIcons(IIconRegister register) {
            itemIcon = register.registerIcon(LibResources.PREFIX_MOD + field_150939_a.getTextureName());
        }

        @Override
        public IIcon getIconFromDamage(int meta) {
            return itemIcon;
        }

        @Override
        public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ) {
            if (side != 1) return false;
            y++;
            if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack)) {
                if (!field_150939_a/* blockInstance */.canPlaceBlockAt(world, x, y, z)) return false;
                ItemDoor.placeDoorBlock(
                    world,
                    x,
                    y,
                    z,
                    MathHelper.floor_double((player.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 3,
                    field_150939_a); // blockInstance

                return true;
            }
            return false;
        }
    }
}
