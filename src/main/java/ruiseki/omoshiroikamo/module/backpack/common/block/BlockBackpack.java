package ruiseki.omoshiroikamo.module.backpack.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.properties.DirectionBlockProperty;
import com.gtnewhorizon.gtnhlib.client.model.color.BlockColor;
import com.gtnewhorizon.gtnhlib.client.model.color.IBlockColor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Getter;
import ruiseki.omoshiroikamo.api.client.render.IJsonModelBlock;
import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.block.property.BlockPropertyReg;
import ruiseki.omoshiroikamo.core.client.render.JsonModelISBRH;
import ruiseki.omoshiroikamo.core.client.render.player.IArmorRender;
import ruiseki.omoshiroikamo.core.client.render.player.IBaubleRender;
import ruiseki.omoshiroikamo.core.client.render.player.PlayerRenderContext;
import ruiseki.omoshiroikamo.core.helper.DirectionHelpers;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.helper.RenderHelpers;
import ruiseki.omoshiroikamo.core.item.ItemBlockBauble;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.backpack.common.entity.EntityBackpack;
import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackWrapper;

public class BlockBackpack extends BlockOK implements IBlockColor, IJsonModelBlock {

    @Getter
    private final int backpackSlots;
    @Getter
    private final int upgradeSlots;

    @BlockPropertyReg
    private final static DirectionBlockProperty property = new DirectionBlockProperty() {

        @Override
        public String getName() {
            return "facing";
        }

        @Override
        public boolean hasTrait(BlockPropertyTrait trait) {
            return switch (trait) {
                case SupportsWorld, WorldMutable, StackMutable, SupportsStacks -> true;
                default -> false;
            };
        }

        @Override
        public ForgeDirection getValue(IBlockAccess world, int x, int y, int z) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TEBackpack tile) {
                return tile.getForward()
                    .getOpposite();
            }
            return ForgeDirection.SOUTH;
        }

        @Override
        public void setValue(World world, int x, int y, int z, ForgeDirection value) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TEBackpack tile) {
                tile.setOrientation(value, ForgeDirection.UP);
            }
        }

        @Override
        public ForgeDirection getValue(ItemStack stack) {
            return ForgeDirection.SOUTH;
        }
    };

    public BlockBackpack(String name, int backpackSlots, int upgradeSlots) {
        super(name, TEBackpack.class, Material.cloth);
        setStepSound(soundTypeCloth);
        setHardness(1f);
        this.backpackSlots = backpackSlots;
        this.upgradeSlots = upgradeSlots;
        isFullSize = isOpaque = false;
        rotatable = true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TEBackpack barrel) {
            barrel.setOrientation(
                DirectionHelpers.yawToDirection4(entity)
                    .getOpposite(),
                ForgeDirection.UP);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TEBackpack barrel) {
            return barrel.onBlockActivated(world, player, ForgeDirection.getOrientation(side), subX, subY, subZ);
        }
        return true;
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBackpack.class;
    }

    @Override
    protected void registerBlockColor() {
        BlockColor.registerBlockColors(new IBlockColor() {

            @Override
            public int colorMultiplier(@Nullable ItemStack stack, int tintIndex) {
                if (stack == null) return -1;

                BackpackWrapper wrapper = new BackpackWrapper(stack, backpackSlots, upgradeSlots);
                return switch (tintIndex) {
                    case 0 -> wrapper.getMainColor();
                    case 1 -> wrapper.getAccentColor();
                    default -> -1;
                };
            }

            @Override
            public int colorMultiplier(@Nullable IBlockAccess world, int x, int y, int z, int tintIndex) {
                if (world == null) return -1;

                TileEntity te = world.getTileEntity(x, y, z);
                if (!(te instanceof TEBackpack backpack)) return -1;

                return switch (tintIndex) {
                    case 0 -> backpack.getMainColor();
                    case 1 -> backpack.getAccentColor();
                    default -> -1;
                };
            }

        }, this);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        TEBackpack tile = (TEBackpack) super.createTileEntity(world, metadata);
        BackpackWrapper wrapper = new BackpackWrapper(null, backpackSlots, upgradeSlots);
        tile.setWrapper(wrapper);
        return tile;
    }

    @Override
    public boolean shouldDropInventory(World world, int x, int y, int z) {
        return false;
    }

    public static class ItemBackpack extends ItemBlockBauble
        implements IGuiHolder<PlayerInventoryGuiData>, IBaubleRender, IArmorRender {

        @Getter
        private int backpackSlots = 27;
        @Getter
        private int upgradeSlots = 1;

        public ItemBackpack(Block block) {
            super(block);
            if (block instanceof BlockBackpack backpack) {
                this.backpackSlots = backpack.getBackpackSlots();
                this.upgradeSlots = backpack.getUpgradeSlots();
            }
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack) {
            if (stack.hasTagCompound() && stack.getTagCompound()
                .hasKey("display", 10)) {
                return stack.getTagCompound()
                    .getCompoundTag("display")
                    .getString("Name");
            }
            return super.getItemStackDisplayName(stack);
        }

        @Override
        public String[] getBaubleTypes(ItemStack itemstack) {
            return new String[] { "body" };
        }

        @Override
        public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
            return armorType == 1;
        }

        @Override
        public boolean hasCustomEntity(ItemStack stack) {
            return true;
        }

        @Override
        public Entity createEntity(World world, Entity location, ItemStack stack) {
            BackpackWrapper wrapper = new BackpackWrapper(stack, this);
            return new EntityBackpack(world, location, stack, wrapper);
        }

        @Override
        public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {
            super.onUpdate(stack, world, entity, slot, isHeld);
            if (!world.isRemote && stack != null) {
                if (!stack.hasTagCompound()) {
                    BackpackWrapper wrapper = new BackpackWrapper(stack, this);
                    wrapper.writeToItem();
                    stack.setTagCompound(wrapper.getTagCompound());
                }
            }
        }

        @Override
        public void onCreated(ItemStack stack, World world, EntityPlayer player) {
            super.onCreated(stack, world, player);
            if (!world.isRemote && stack != null) {
                if (!stack.hasTagCompound()) {
                    BackpackWrapper wrapper = new BackpackWrapper(stack, this);
                    wrapper.writeToItem();
                    stack.setTagCompound(wrapper.getTagCompound());
                }
            }
        }

        @Override
        public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ) {

            if (player.isSneaking() && stack != null) {
                if (stack.getTagCompound() == null) {
                    new BackpackWrapper(stack, this).writeToItem();
                }
                return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
            }

            return false;
        }

        @Override
        public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
            if (!world.isRemote && stack != null && stack.getTagCompound() != null) {
                BackpackWrapper wrapper = new BackpackWrapper(stack, this);
                if (wrapper.canPlayerAccess(player.getUniqueID())) {
                    GuiFactories.playerInventory()
                        .openFromMainHand(player);
                }
            }
            return stack;
        }

        @Override
        public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
            ItemStack stack = data.getUsedItemStack();
            BackpackWrapper cap = new BackpackWrapper(stack, this);
            return new BackpackGuiHolder.ItemStackGuiHolder(cap).buildUI(data, syncManager, settings);
        }

        @Override
        public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel mainPanel) {
            return new ModularScreen(LibMisc.MOD_ID, mainPanel);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            list.add(LangHelpers.localize("tooltip.backpack.inventory_size", backpackSlots));
            list.add(LangHelpers.localize("tooltip.backpack.upgrade_slots_size", upgradeSlots));
            if (GuiScreen.isShiftKeyDown()) {
                BackpackWrapper cap = new BackpackWrapper(stack, this);
                list.add(LangHelpers.localize("tooltip.backpack.stack_multiplier", cap.getTotalStackMultiplier(), "x"));
            }
            super.addInformation(stack, player, list, flag);
        }

        @Override
        public void collectContext(ItemStack stack, EntityPlayer player, PlayerRenderContext context) {
            context.setRenderCape(false);
        }

        @Override
        public void render(ItemStack stack, EntityPlayer player, RenderPlayerEvent event,
            RenderHelpers.RenderType type) {
            if (stack == null || type != RenderHelpers.RenderType.BODY) return;
            GL11.glPushMatrix();
            GL11.glTranslatef(0f, 0.3f, 0.3f);
            JsonModelISBRH.INSTANCE.renderToEntity(stack);
            GL11.glPopMatrix();
        }
    }
}
