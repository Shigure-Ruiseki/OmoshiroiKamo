package ruiseki.omoshiroikamo.module.cows.common.item;

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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.ItemNBTHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.item.ItemOK;
import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.CowConfig;
import ruiseki.omoshiroikamo.core.compat.ModCompatInformation;
import ruiseki.omoshiroikamo.module.cows.common.block.TEStall;
import ruiseki.omoshiroikamo.module.cows.common.entity.EntityCowsCow;

public class ItemCowSpawnEgg extends ItemOK {

    @SideOnly(Side.CLIENT)
    protected IIcon baseIcon;

    public ItemCowSpawnEgg() {
        super(ModObject.COW_SPAWN_EGG.name);
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        for (CowsRegistryItem cow : CowsRegistry.INSTANCE.getItems()) {
            list.add(new ItemStack(item, 1, cow.getId()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        return baseIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return baseIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        baseIcon = register.registerIcon(Reference.PREFIX_MOD + "cow/cow_displayer");
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        CowsRegistryItem cowsRegistryItem = CowsRegistry.INSTANCE.getByType(stack.getItemDamage());
        return LangHelpers.localize("entity." + cowsRegistryItem.getEntityName() + ".name");
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        CowsRegistryItem cowsRegistryItem = CowsRegistry.INSTANCE.getByType(stack.getItemDamage());
        return cowsRegistryItem.getBgColor();
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            BlockPos pos = correctPosition(new BlockPos(x, y, z), side);
            activate(stack, world, pos);
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return true;
    }

    private static BlockPos correctPosition(BlockPos pos, int side) {
        final int[] offsetsXForSide = new int[] { 0, 0, 0, 0, -1, 1 };
        final int[] offsetsYForSide = new int[] { -1, 1, 0, 0, 0, 0 };
        final int[] offsetsZForSide = new int[] { 0, 0, -1, 1, 0, 0 };

        int posX = pos.x + offsetsXForSide[side];
        int posY = pos.y + offsetsYForSide[side];
        int posZ = pos.z + offsetsZForSide[side];

        return new BlockPos(posX, posY, posZ);
    }

    private void activate(ItemStack stack, World worldIn, BlockPos pos) {
        EntityCowsCow entity = new EntityCowsCow(worldIn);

        entity.setPosition(pos.x + 0.5, pos.y, pos.z + 0.5);
        entity.onSpawnWithEgg(null);

        if (CowConfig.useTrait) {
            entity.addRandomTraits();
        }

        entity.setType(stack.getItemDamage());

        if (stack.hasTagCompound()) {
            NBTTagCompound entityNBT = new NBTTagCompound();
            entity.writeEntityToNBT(entityNBT);

            NBTTagCompound stackNBT = ItemNBTHelpers.getNBT(stack);
            for (String key : stackNBT.func_150296_c()) {
                NBTBase value = stackNBT.getTag(key);
                entityNBT.setTag(key, value.copy());
            }

            entity.readEntityFromNBT(entityNBT);
        }

        worldIn.spawnEntityInWorld(entity);
    }

    public boolean onItemUseOnTile(TEStall tile, ItemStack stack, EntityPlayer player, World world) {
        if (tile == null || world.isRemote) {
            return false;
        }
        if (tile.hasCow()) {
            return false;
        }
        EntityCowsCow cow = new EntityCowsCow(world);
        cow.setPosition(tile.xCoord + 0.5, tile.yCoord, tile.zCoord + 0.5);
        cow.onSpawnWithEgg(null);
        cow.setType(stack.getItemDamage());

        if (CowConfig.useTrait) {
            cow.addRandomTraits();
        }

        if (stack.hasTagCompound()) {
            NBTTagCompound entityNBT = new NBTTagCompound();
            cow.writeEntityToNBT(entityNBT);

            NBTTagCompound stackNBT = ItemNBTHelpers.getNBT(stack);
            for (String key : stackNBT.func_150296_c()) {
                NBTBase value = stackNBT.getTag(key);
                entityNBT.setTag(key, value.copy());
            }

            cow.readEntityFromNBT(entityNBT);
        }

        tile.setCow(cow);

        if (!player.capabilities.isCreativeMode) {
            stack.stackSize--;
        }

        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List<String> list, boolean flag) {
        CowsRegistryItem cow = CowsRegistry.INSTANCE.getByType(itemstack.getItemDamage());
        if (cow == null) {
            return;
        }

        FluidStack milk = cow.createMilkFluid();
        SpawnType spawnType = cow.getSpawnType();

        list.add(LangHelpers.localize(Reference.TOOLTIP + "spawn_egg.tier", cow.getTier()));

        if (milk != null && milk.getFluid() != null) {
            list.add(LangHelpers.localize(Reference.TOOLTIP + "spawn_egg.milkfluid", milk.getLocalizedName()));
        } else {
            list.add(LangHelpers.localize(Reference.TOOLTIP + "spawn_egg.nomilkfluid"));
        }

        EnumChatFormatting valueColor;
        switch (spawnType) {
            case NORMAL:
                valueColor = EnumChatFormatting.GREEN;
                break;
            case HELL:
                valueColor = EnumChatFormatting.RED;
                break;
            case SNOW:
                valueColor = EnumChatFormatting.AQUA;
                break;
            default:
                valueColor = EnumChatFormatting.WHITE;
                break;
        }

        EnumChatFormatting labelColor = EnumChatFormatting.GRAY;
        String labelKey = Reference.TOOLTIP + "spawn_egg.spawnType";
        String label = new ChatComponentTranslation(labelKey).getFormattedText();
        list.add(labelColor + label + ": " + valueColor + String.valueOf(spawnType) + EnumChatFormatting.RESET);

        if (!cow.isBreedable()) {
            list.add(EnumChatFormatting.RED + LangHelpers.localize(Reference.TOOLTIP + "spawn_egg.notbreedable"));
        }

        if (cow.isBreedable() && cow.getParent1() != null && cow.getParent2() != null) {
            String parent1 = new ChatComponentTranslation(
                cow.getParent1()
                    .getDisplayName()).getFormattedText();
            String parent2 = new ChatComponentTranslation(
                cow.getParent2()
                    .getDisplayName()).getFormattedText();

            String breedableLabel = new ChatComponentTranslation(Reference.TOOLTIP + "spawn_egg.breedable")
                .getFormattedText();

            list.add(
                EnumChatFormatting.YELLOW + breedableLabel
                    + ": "
                    + EnumChatFormatting.GOLD
                    + parent1
                    + " & "
                    + parent2
                    + EnumChatFormatting.RESET);
        }

        if (ModCompatInformation.TOOLTIP.containsKey(cow.getId())) {
            ModCompatInformation info = ModCompatInformation.TOOLTIP.get(cow.getId());
            if (info != null && info.getToolTip() != null) {
                list.addAll(info.getToolTip());
            }
        }
    }

}
