package ruiseki.omoshiroikamo.common.item.cow;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import com.enderio.core.common.util.BlockCoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.entity.cow.EntityCowsCow;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.ModCompatInformation;
import vazkii.botania.common.core.helper.ItemNBTHelper;

public class ItemCowSpawnEgg extends ItemOK {

    @SideOnly(Side.CLIENT)
    protected IIcon baseIcon, overlayIcon;

    public ItemCowSpawnEgg() {
        super(ModObject.itemCowSpawnEgg);
        setHasSubtypes(true);
    }

    public static ItemCowSpawnEgg create() {
        return new ItemCowSpawnEgg();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        for (CowsRegistryItem cow : CowsRegistry.INSTANCE.getItems()) {
            list.add(new ItemStack(item, 1, cow.getId()));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses(int metadata) {
        return 2;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        return pass == 0 ? baseIcon : overlayIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        return baseIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister register) {
        baseIcon = register.registerIcon(LibResources.PREFIX_MOD + "spawn_egg");
        overlayIcon = register.registerIcon(LibResources.PREFIX_MOD + "spawn_egg_overlay");
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        CowsRegistryItem cowsRegistryItem = CowsRegistry.INSTANCE.getByType(stack.getItemDamage());
        return LibMisc.LANG.localize("entity." + cowsRegistryItem.getEntityName() + ".name");
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        CowsRegistryItem cowsRegistryItem = CowsRegistry.INSTANCE.getByType(stack.getItemDamage());
        return renderPass == 0 ? cowsRegistryItem.getBgColor() : cowsRegistryItem.getFgColor();
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            BlockCoord blockCoord = correctPosition(new BlockCoord(x, y, z), side);
            activate(stack, world, blockCoord, stack.getItemDamage());
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return true;
    }

    private static BlockCoord correctPosition(BlockCoord pos, int side) {
        final int[] offsetsXForSide = new int[] { 0, 0, 0, 0, -1, 1 };
        final int[] offsetsYForSide = new int[] { -1, 1, 0, 0, 0, 0 };
        final int[] offsetsZForSide = new int[] { 0, 0, -1, 1, 0, 0 };

        int posX = pos.x + offsetsXForSide[side];
        int posY = pos.y + offsetsYForSide[side];
        int posZ = pos.z + offsetsZForSide[side];

        return new BlockCoord(posX, posY, posZ);
    }

    private void activate(ItemStack stack, World worldIn, BlockCoord pos, int metadata) {
        EntityCowsCow entity = new EntityCowsCow(worldIn);

        entity.setPosition(pos.x + 0.5, pos.y, pos.z + 0.5);
        entity.onSpawnWithEgg(null);
        entity.addRandomTraits();
        entity.setType(metadata);

        if (stack.hasTagCompound()) {
            NBTTagCompound entityNBT = new NBTTagCompound();
            entity.writeEntityToNBT(entityNBT);

            NBTTagCompound stackNBT = ItemNBTHelper.getNBT(stack);
            for (String key : stackNBT.func_150296_c()) {
                NBTBase value = stackNBT.getTag(key);
                entityNBT.setTag(key, value.copy());
            }

            entity.readEntityFromNBT(entityNBT);
        }

        worldIn.spawnEntityInWorld(entity);
    }

    @Override
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        CowsRegistryItem cowsRegistryItem = CowsRegistry.INSTANCE.getByType(itemstack.getItemDamage());

        if (cowsRegistryItem == null) {
            return;
        }

        list.add(
            new ChatComponentTranslation(LibResources.TOOLTIP + "spawn_egg.tier", cowsRegistryItem.getTier())
                .getFormattedText());
        FluidStack fluidStack = cowsRegistryItem.createMilkFluid();

        if (fluidStack != null && fluidStack.getFluid() != null) {
            list.add(
                new ChatComponentTranslation(
                    LibResources.TOOLTIP + "spawn_egg.milkfluid",
                    fluidStack.getLocalizedName()).getFormattedText());
        } else {
            list.add(
                new ChatComponentTranslation(
                    LibResources.TOOLTIP + "spawn_egg.nomilkfluid",
                    fluidStack.getLocalizedName()).getFormattedText());
        }

        if (cowsRegistryItem.getSpawnType() != SpawnType.NONE) {
            EnumChatFormatting format = cowsRegistryItem.getSpawnType() == SpawnType.NORMAL ? EnumChatFormatting.GREEN
                : cowsRegistryItem.getSpawnType() == SpawnType.HELL ? EnumChatFormatting.RED
                    : cowsRegistryItem.getSpawnType() == SpawnType.SNOW ? EnumChatFormatting.AQUA
                        : EnumChatFormatting.WHITE;
            list.add(
                new ChatComponentTranslation(LibResources.TOOLTIP + "spawn_egg.spawnType").getFormattedText() + ": "
                    + EnumChatFormatting.RESET
                    + format
                    + cowsRegistryItem.getSpawnType()
                        .toString());
        }

        if (!cowsRegistryItem.isBreedable()) {
            list.add(
                EnumChatFormatting.RED
                    + new ChatComponentTranslation(LibResources.TOOLTIP + "spawn_egg.notbreedable").getFormattedText());
        } else {
            if (cowsRegistryItem.getParent1() != null && cowsRegistryItem.getParent2() != null) {
                String parent1 = new ChatComponentTranslation(
                    cowsRegistryItem.getParent1()
                        .getDisplayName()).getFormattedText();

                String parent2 = new ChatComponentTranslation(
                    cowsRegistryItem.getParent2()
                        .getDisplayName()).getFormattedText();

                list.add(
                    EnumChatFormatting.YELLOW
                        + new ChatComponentTranslation(LibResources.TOOLTIP + "spawn_egg.breedable").getFormattedText()
                        + ": "
                        + EnumChatFormatting.RESET
                        + EnumChatFormatting.ITALIC
                        + EnumChatFormatting.GOLD
                        + parent1
                        + EnumChatFormatting.RESET
                        + " & "
                        + EnumChatFormatting.ITALIC
                        + EnumChatFormatting.GOLD
                        + parent2);
            }

        }

        if (ModCompatInformation.TOOLTIP.containsKey(cowsRegistryItem.getId())) {
            ModCompatInformation info = ModCompatInformation.TOOLTIP.get(cowsRegistryItem.getId());
            list.addAll(info.getToolTip());
        }
    }
}
