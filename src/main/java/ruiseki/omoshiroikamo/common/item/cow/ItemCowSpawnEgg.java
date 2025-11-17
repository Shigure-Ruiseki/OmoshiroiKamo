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

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.entity.cow.EntityCowsCow;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.TooltipUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.config.backport.CowConfig;
import ruiseki.omoshiroikamo.plugin.ModCompatInformation;
import vazkii.botania.common.core.helper.ItemNBTHelper;

public class ItemCowSpawnEgg extends ItemOK {

    @SideOnly(Side.CLIENT)
    protected IIcon baseIcon;

    public ItemCowSpawnEgg() {
        super(ModObject.itemCowSpawnEgg);
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
        baseIcon = register.registerIcon(LibResources.PREFIX_MOD + "cow_displayer");
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        CowsRegistryItem cowsRegistryItem = CowsRegistry.INSTANCE.getByType(stack.getItemDamage());
        return LibMisc.LANG.localize("entity." + cowsRegistryItem.getEntityName() + ".name");
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
    public void addInformation(ItemStack itemstack, EntityPlayer player, List<String> list, boolean flag) {
        CowsRegistryItem cow = CowsRegistry.INSTANCE.getByType(itemstack.getItemDamage());
        if (cow == null) {
            return;
        }

        FluidStack milk = cow.createMilkFluid();
        SpawnType spawnType = cow.getSpawnType();
        TooltipUtils builder = TooltipUtils.builder();

        // Tier
        builder.addLang(LibResources.TOOLTIP + "spawn_egg.tier", cow.getTier());

        // Milk fluid
        builder.addLangIf(
            milk != null && milk.getFluid() != null,
            LibResources.TOOLTIP + "spawn_egg.milkfluid",
            milk.getLocalizedName());
        builder.addLangIf(milk.getFluid() == null, LibResources.TOOLTIP + "spawn_egg.nomilkfluid");

        EnumChatFormatting labelColor = EnumChatFormatting.GRAY;
        EnumChatFormatting valueColor;

        if (spawnType == SpawnType.NORMAL) {
            valueColor = EnumChatFormatting.GREEN;
        } else if (spawnType == SpawnType.HELL) {
            valueColor = EnumChatFormatting.RED;
        } else if (spawnType == SpawnType.SNOW) {
            valueColor = EnumChatFormatting.AQUA;
        } else {
            valueColor = EnumChatFormatting.WHITE;
        }

        builder.addLabelWithLangValue(
            LibResources.TOOLTIP + "spawn_egg.spawnType",
            labelColor,
            spawnType.toString(),
            valueColor);

        // Breedable
        builder.addColoredLangIf(
            !cow.isBreedable(),
            EnumChatFormatting.RED,
            LibResources.TOOLTIP + "spawn_egg.notbreedable");

        // Breedable with parents
        if (cow.isBreedable() && cow.getParent1() != null && cow.getParent2() != null) {
            String parent1 = new ChatComponentTranslation(
                cow.getParent1()
                    .getDisplayName()).getFormattedText();
            String parent2 = new ChatComponentTranslation(
                cow.getParent2()
                    .getDisplayName()).getFormattedText();

            builder.addLabelWithValue(
                new ChatComponentTranslation(LibResources.TOOLTIP + "spawn_egg.breedable").getFormattedText(),
                EnumChatFormatting.YELLOW,
                parent1 + " & " + parent2,
                EnumChatFormatting.GOLD);
        }

        // ModCompat tooltip
        if (ModCompatInformation.TOOLTIP.containsKey(cow.getId())) {
            ModCompatInformation info = ModCompatInformation.TOOLTIP.get(cow.getId());
            builder.addAll(info.getToolTip());
        }

        list.addAll(builder.build());
    }

}
