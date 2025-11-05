package ruiseki.omoshiroikamo.common.item.chicken;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.enderio.core.common.util.BlockCoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.entity.chicken.EntityChickensChicken;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.ItemNBTHelper;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.chicken.ChickenInformation;

public class ItemChickenSpawnEgg extends ItemOK {

    @SideOnly(Side.CLIENT)
    protected IIcon baseIcon, overlayIcon;

    public ItemChickenSpawnEgg() {
        super(ModObject.itemChickenSpawnEgg.unlocalisedName);
        setHasSubtypes(true);
    }

    public static ItemChickenSpawnEgg create() {
        return new ItemChickenSpawnEgg();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            list.add(new ItemStack(item, 1, chicken.getId()));
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

    @Override
    public void registerIcons(IIconRegister register) {
        baseIcon = register.registerIcon(LibResources.PREFIX_MOD + "spawn_egg");
        overlayIcon = register.registerIcon(LibResources.PREFIX_MOD + "spawn_egg_overlay");
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        ChickensRegistryItem chickenDescription = ChickensRegistry.getByType(stack.getItemDamage());
        return LibMisc.LANG.localize("entity." + chickenDescription.getEntityName() + ".name");
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        ChickensRegistryItem chickenDescription = ChickensRegistry.getByType(stack.getItemDamage());
        return renderPass == 0 ? chickenDescription.getBgColor() : chickenDescription.getFgColor();
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
        String entityName = LibMisc.MOD_ID + "." + "chicken";
        EntityChickensChicken entity = (EntityChickensChicken) EntityList.createEntityByName(entityName, worldIn);
        if (entity == null) {
            return;
        }

        entity.setPosition(pos.x + 0.5, pos.y, pos.z + 0.5);
        entity.onSpawnWithEgg(null);
        entity.setChickenType(metadata);

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
        ChickensRegistryItem chickenDescription = ChickensRegistry.getByType(itemstack.getItemDamage());

        if (chickenDescription == null) {
            return;
        }

        list.add(
            new ChatComponentTranslation(LibResources.TOOLTIP + "chicken_spawn_egg.tier", chickenDescription.getTier())
                .getFormattedText());
        ItemStack layitem = chickenDescription.createLayItem();

        if (layitem != null && layitem.getItem() != null) {
            list.add(
                new ChatComponentTranslation(
                    LibResources.TOOLTIP + "chicken_spawn_egg.layitem",
                    layitem.getDisplayName()).getFormattedText());
        } else {
            list.add(
                new ChatComponentTranslation(
                    LibResources.TOOLTIP + "chicken_spawn_egg.nolayitem",
                    layitem.getDisplayName()).getFormattedText());
        }

        if (chickenDescription.getSpawnType() != SpawnType.NONE) {
            EnumChatFormatting format = chickenDescription.getSpawnType() == SpawnType.NORMAL ? EnumChatFormatting.GREEN
                : chickenDescription.getSpawnType() == SpawnType.HELL ? EnumChatFormatting.RED
                    : chickenDescription.getSpawnType() == SpawnType.SNOW ? EnumChatFormatting.AQUA
                        : EnumChatFormatting.WHITE;
            list.add(
                new ChatComponentTranslation(LibResources.TOOLTIP + "chicken_spawn_egg.spawnType").getFormattedText()
                    + ": "
                    + EnumChatFormatting.RESET
                    + format
                    + chickenDescription.getSpawnType()
                        .toString());
        }

        if (!chickenDescription.isBreedable()) {
            list.add(
                EnumChatFormatting.RED
                    + new ChatComponentTranslation(LibResources.TOOLTIP + "chicken_spawn_egg.notbreedable")
                        .getFormattedText());
        } else {
            if (chickenDescription.getParent1() != null && chickenDescription.getParent2() != null) {
                String parent1 = new ChatComponentTranslation(
                    "entity." + chickenDescription.getParent1()
                        .getEntityName() + ".name").getFormattedText();

                String parent2 = new ChatComponentTranslation(
                    "entity." + chickenDescription.getParent2()
                        .getEntityName() + ".name").getFormattedText();

                list.add(
                    EnumChatFormatting.YELLOW
                        + new ChatComponentTranslation(LibResources.TOOLTIP + "chicken_spawn_egg.breedable")
                            .getFormattedText()
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

        if (ChickenInformation.TOOLTIPCHICKENS.containsKey(itemstack.getItemDamage())) {
            ChickenInformation info = ChickenInformation.TOOLTIPCHICKENS.get(itemstack.getItemDamage());
            list.addAll(info.getToolTip());
        }
    }
}
