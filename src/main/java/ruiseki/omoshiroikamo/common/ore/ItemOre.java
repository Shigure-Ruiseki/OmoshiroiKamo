package ruiseki.omoshiroikamo.common.ore;

import static org.apache.commons.lang3.StringUtils.capitalize;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ore.OreEntry;
import ruiseki.omoshiroikamo.api.ore.OreRegistry;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemOre extends ItemOK implements IAdvancedTooltipProvider {

    @SideOnly(Side.CLIENT)
    protected IIcon crushedOverlay, crushed, washed, enriched;

    public ItemOre() {
        super(ModObject.itemOre);
        setMaxDamage(0);
    }

    public static void registerOre() {
        for (OreEntry entry : OreRegistry.all()) {
            String oreName = entry.getUnlocalizedName();
            registerMaterialOreDict(oreName, entry.getMeta());
        }
    }

    private static void registerMaterialOreDict(String name, int meta) {
        OreDictionary.registerOre("crushed" + capitalize(name), ModItems.ORE.newItemStack(1, LibResources.BASE + meta));
        OreDictionary.registerOre("washed" + capitalize(name), ModItems.ORE.newItemStack(1, LibResources.META1 + meta));
        OreDictionary
            .registerOre("enriched" + capitalize(name), ModItems.ORE.newItemStack(1, LibResources.META1 + meta));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getItemDamage();
        int baseMeta = meta % LibResources.META1;
        OreEntry ore = OreRegistry.fromMeta(baseMeta);
        String base = super.getUnlocalizedName(stack);

        String type;
        if (meta >= LibResources.META2) {
            type = "enriched";
        } else if (meta >= LibResources.META1) {
            type = "washed";
        } else {
            type = "crushed";
        }

        String or = ore.getUnlocalizedName();
        return base + "." + type + "." + or;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (OreEntry entry : OreRegistry.all()) {
            int meta = entry.getMeta();
            // crushed
            list.add(new ItemStack(this, 1, meta));
            // washed
            list.add(new ItemStack(this, 1, LibResources.META1 + meta));
            // enriched
            list.add(new ItemStack(this, 1, LibResources.META2 + meta));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        int meta = stack.getItemDamage();
        if (meta >= LibResources.META2) {
            return enriched;
        }
        if (meta >= LibResources.META1) {
            return washed;
        }

        return (pass == 0) ? crushed : crushedOverlay;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        int meta = stack.getItemDamage();

        if (meta < LibResources.META1 && renderPass == 1) {
            return 0xFFFFFF;
        }

        int baseMeta = meta % LibResources.META1;
        OreEntry mat = OreRegistry.fromMeta(baseMeta);
        return mat.getColor();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister reg) {
        crushed = reg.registerIcon(LibResources.PREFIX_MOD + "base_crushed");
        crushedOverlay = reg.registerIcon(LibResources.PREFIX_MOD + "crushed_overlay");
        washed = reg.registerIcon(LibResources.PREFIX_MOD + "base_washed");
        enriched = reg.registerIcon(LibResources.PREFIX_MOD + "base_enriched");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        int meta = stack.getItemDamage();
        if (meta >= LibResources.META1) {
            return stack;
        }

        MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, true);
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            if (tryWash(stack, world, mop.blockX, mop.blockY, mop.blockZ, meta)) {
                return stack;
            }
        }

        if (tryWash(stack, world, player.posX, player.boundingBox.minY, player.posZ, meta)) {
            return stack;
        }

        return super.onItemRightClick(stack, world, player);
    }

    private boolean tryWash(ItemStack stack, World world, double x, double y, double z, int meta) {
        if (meta >= LibResources.META1) {
            return false;
        }

        Block block = world.getBlock((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
        if (block.getMaterial() != Material.water) {
            return false;
        }

        if (!world.isRemote) {
            stack.setItemDamage(LibResources.META1 + (meta % LibResources.META1));
            world.playSoundEffect(x, y, z, "game.neutral.swim", 0.4F, 1.0F);
            for (int i = 0; i < 5; i++) {
                world.spawnParticle(
                    "splash",
                    x + 0.5 + (world.rand.nextDouble() - 0.5),
                    y + 0.5,
                    z + 0.5 + (world.rand.nextDouble() - 0.5),
                    0,
                    0.1,
                    0);
            }
        }
        return true;
    }

    @Override
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {}

    @Override
    public void addBasicEntries(ItemStack itemstack, EntityPlayer player, List<String> list, boolean advanced) {
        int meta = itemstack.getItemDamage();

        if (meta < LibResources.META1) {
            list.add("§7Right-click or stand in water to wash");
        } else if (meta < LibResources.META2) {
            list.add("§7Washed from crushed ore");
        }
    }

    @Override
    public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {

    }
}
