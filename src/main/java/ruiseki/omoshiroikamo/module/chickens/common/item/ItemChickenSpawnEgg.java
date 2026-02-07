package ruiseki.omoshiroikamo.module.chickens.common.item;

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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.config.backport.ChickenConfig;
import ruiseki.omoshiroikamo.core.common.item.ItemOK;
import ruiseki.omoshiroikamo.core.common.util.TooltipUtils;
import ruiseki.omoshiroikamo.core.integration.ModCompatInformation;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.chickens.common.entity.EntityChickensChicken;

public class ItemChickenSpawnEgg extends ItemOK {

    @SideOnly(Side.CLIENT)
    protected IIcon baseIcon, overlayIcon;

    public ItemChickenSpawnEgg() {
        super(ModObject.itemChickenSpawnEgg);
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        for (ChickensRegistryItem chicken : ChickensRegistry.INSTANCE.getItems()) {
            list.add(new ItemStack(this, 1, chicken.getId()));
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
        baseIcon = register.registerIcon(LibResources.PREFIX_MOD + "chicken/spawn_egg");
        overlayIcon = register.registerIcon(LibResources.PREFIX_MOD + "chicken/spawn_egg_overlay");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        ChickensRegistryItem chickenDescription = ChickensRegistry.INSTANCE.getByType(stack.getItemDamage());
        if (chickenDescription == null) {
            return super.getItemStackDisplayName(stack);
        }
        return LibMisc.LANG.localize(chickenDescription.getDisplayName());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        ChickensRegistryItem chickenDescription = ChickensRegistry.INSTANCE.getByType(stack.getItemDamage());
        if (chickenDescription == null) {
            return renderPass;
        }
        return renderPass == 0 ? chickenDescription.getBgColor() : chickenDescription.getFgColor();
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            BlockPos pos = correctPosition(new BlockPos(x, y, z, world), side);
            activate(stack, pos);
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

        return new BlockPos(posX, posY, posZ, pos.world);
    }

    private void activate(ItemStack stack, BlockPos pos) {
        EntityChickensChicken entity = new EntityChickensChicken(pos.getWorld());

        entity.setPosition(pos.x + 0.5, pos.y, pos.z + 0.5);
        entity.onSpawnWithEgg(null);

        if (ChickenConfig.useTrait) {
            entity.addRandomTraits();
        }

        entity.setType(stack.getItemDamage());

        if (stack.hasTagCompound()) {
            NBTTagCompound entityNBT = new NBTTagCompound();
            entity.writeEntityToNBT(entityNBT);

            NBTTagCompound stackNBT = ItemNBTUtils.getNBT(stack);
            for (String key : stackNBT.func_150296_c()) {
                NBTBase value = stackNBT.getTag(key);
                entityNBT.setTag(key, value.copy());
            }

            entity.readEntityFromNBT(entityNBT);
        }

        pos.getWorld()
            .spawnEntityInWorld(entity);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        ChickensRegistryItem chicken = ChickensRegistry.INSTANCE.getByType(stack.getItemDamage());
        if (chicken == null) {
            return;
        }

        ItemStack layItem = chicken.createLayItem();
        SpawnType spawnType = chicken.getSpawnType();
        TooltipUtils builder = TooltipUtils.builder();

        // Tier
        builder.addLang(LibResources.TOOLTIP + "spawn_egg.tier", chicken.getTier());

        // Lay item
        builder.addLangIf(
            layItem != null && layItem.getItem() != null,
            LibResources.TOOLTIP + "spawn_egg.layitem",
            layItem.getDisplayName());
        builder.addLangIf(layItem == null || layItem.getItem() == null, LibResources.TOOLTIP + "spawn_egg.nolayitem");

        // Spawn type (chỉ hiển thị nếu khác NONE)
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

        // Not breedable
        builder.addColoredLangIf(
            !chicken.isBreedable(),
            EnumChatFormatting.RED,
            LibResources.TOOLTIP + "spawn_egg.notbreedable");

        // Breedable with parents
        if (chicken.isBreedable() && chicken.getParent1() != null && chicken.getParent2() != null) {
            String parent1 = new ChatComponentTranslation(
                chicken.getParent1()
                    .getDisplayName()).getFormattedText();
            String parent2 = new ChatComponentTranslation(
                chicken.getParent2()
                    .getDisplayName()).getFormattedText();

            builder.addLabelWithValue(
                new ChatComponentTranslation(LibResources.TOOLTIP + "spawn_egg.breedable").getFormattedText(),
                EnumChatFormatting.YELLOW,
                parent1 + " & " + parent2,
                EnumChatFormatting.GOLD);
        }

        // Mod compat tooltips
        if (ModCompatInformation.TOOLTIP.containsKey(chicken.getId())) {
            ModCompatInformation info = ModCompatInformation.TOOLTIP.get(chicken.getId());
            builder.addAll(info.getToolTip());
        }

        list.addAll(builder.build());
    }

}
