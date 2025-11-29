package ruiseki.omoshiroikamo.common.block.backpack;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;
import static ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler.ACCENT_COLOR;
import static ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler.MAIN_COLOR;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizon.gtnhlib.client.model.color.BlockColor;
import com.gtnewhorizon.gtnhlib.client.model.color.IBlockColor;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Getter;
import ruiseki.omoshiroikamo.api.client.IBaubleRender;
import ruiseki.omoshiroikamo.api.client.IItemJSONRender;
import ruiseki.omoshiroikamo.api.client.JsonModelISBRH;
import ruiseki.omoshiroikamo.api.client.RenderUtils;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.common.block.ItemBlockBauble;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractBlock;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.common.entity.EntityBackpack;
import ruiseki.omoshiroikamo.common.util.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockBackpack extends AbstractBlock<TEBackpack> implements IBlockColor {

    @Getter
    private final int backpackSlots;
    @Getter
    private final int upgradeSlots;
    IIcon cloth, border, leatherClips, copperClips, ironClips, goldClips, diamondClips, obsidianClips;

    protected BlockBackpack(String name, int backpackSlots, int upgradeSlots) {
        super(name, TEBackpack.class, Material.cloth);
        setStepSound(soundTypeCloth);
        setHardness(1f);
        this.backpackSlots = backpackSlots;
        this.upgradeSlots = upgradeSlots;
    }

    public static BlockBackpack create(String name, int slots, int upgradeSlots) {
        return new BlockBackpack(name, slots, upgradeSlots);
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        cloth = reg.registerIcon(LibResources.PREFIX_MOD + "backpack_cloth");
        border = reg.registerIcon(LibResources.PREFIX_MOD + "backpack_border");
        leatherClips = reg.registerIcon(LibResources.PREFIX_MOD + "leather_clips");
        copperClips = reg.registerIcon(LibResources.PREFIX_MOD + "copper_clips");
        ironClips = reg.registerIcon(LibResources.PREFIX_MOD + "iron_clips");
        goldClips = reg.registerIcon(LibResources.PREFIX_MOD + "gold_clips");
        diamondClips = reg.registerIcon(LibResources.PREFIX_MOD + "diamond_clips");
        obsidianClips = reg.registerIcon(LibResources.PREFIX_MOD + "obsidian_clips");
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, player, stack);
        AbstractTE te = (AbstractTE) world.getTileEntity(x, y, z);
        world.setBlockMetadataWithNotify(x, y, z, te.getFacing(), 2);
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBackpack.class, name);
        GameRegistry.registerTileEntity(teClass, name + "TileEntity");
        BlockColor.registerBlockColors(new IBlockColor() {

            @Override
            public int colorMultiplier(IBlockAccess world, int x, int y, int z, int tintIndex) {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof TEBackpack backpack) {
                    if (tintIndex == 0) {
                        return EnumDye.rgbToAbgr(backpack.getMainColor());
                    }
                    if (tintIndex == 1) {
                        return EnumDye.rgbToAbgr(backpack.getAccentColor());
                    }
                }
                return -1;
            }

            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                NBTTagCompound tag = ItemNBTUtils.getNBT(stack);
                int main = tag.hasKey(MAIN_COLOR) ? tag.getInteger(MAIN_COLOR) : 0xFFCC613A;
                int accent = tag.hasKey(ACCENT_COLOR) ? tag.getInteger(ACCENT_COLOR) : 0xFF622E1A;

                if (tintIndex == 0) {
                    return EnumDye.rgbToAbgr(main);
                }
                if (tintIndex == 1) {
                    return EnumDye.rgbToAbgr(accent);
                }
                return -1;
            }
        }, this);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TEBackpack(backpackSlots, upgradeSlots);
    }

    public static class ItemBackpack extends ItemBlockBauble
        implements IGuiHolder<PlayerInventoryGuiData>, IBaubleRender, IItemJSONRender {

        @Getter
        private int backpackSlots = 27;
        @Getter
        private int upgradeSlots = 1;

        public ItemBackpack(Block block) {
            super(block, block);
            if (block instanceof BlockBackpack backpack) {
                this.backpackSlots = backpack.getBackpackSlots();
                this.upgradeSlots = backpack.getUpgradeSlots();
            }
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
            BackpackHandler handler = new BackpackHandler(stack, null, this);
            return new EntityBackpack(world, location, stack, handler);
        }

        @Override
        public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {
            super.onUpdate(stack, world, entity, slot, isHeld);
            if (!stack.hasTagCompound()) {
                BackpackHandler cap = new BackpackHandler(stack, null, this);
                cap.writeToItem();
            }
        }

        @Override
        public void onCreated(ItemStack stack, World world, EntityPlayer player) {
            super.onCreated(stack, world, player);
            if (!stack.hasTagCompound()) {
                BackpackHandler cap = new BackpackHandler(stack, null, this);
                cap.writeToItem();
            }
        }

        @Override
        public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (player.isSneaking() && te == null) {
                return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
            }
            if (!world.isRemote) {
                GuiFactories.playerInventory()
                    .openFromMainHand(player);
                return true;
            }

            return false;
        }

        @Override
        public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
            if (!world.isRemote) {
                GuiFactories.playerInventory()
                    .openFromMainHand(player);
            }
            return super.onItemRightClick(stack, world, player);
        }

        @Override
        public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
            ItemStack stack = data.getUsedItemStack();
            BackpackHandler cap = new BackpackHandler(stack, null, this);
            return new BackpackGuiHolder.ItemStackGuiHolder(cap).buildUI(data, syncManager, settings);
        }

        @Override
        public void onPlayerBaubleRender(ItemStack stack, RenderPlayerEvent event, RenderUtils.RenderType type) {
            if (stack == null || type != RenderUtils.RenderType.BODY) {
                return;
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(0f, 0.3f, 0.3f);
            ruiseki.omoshiroikamo.api.client.RenderUtils.rotateIfSneaking(event.entityPlayer);
            JsonModelISBRH.renderToEntity(stack);
            GL11.glPopMatrix();

        }

        @Override
        public void onArmorRender(ItemStack stack, RenderPlayerEvent event, RenderUtils.RenderType type) {
            if (stack == null || type != RenderUtils.RenderType.BODY) {
                return;
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(0f, 0.3f, 0.3f);
            ruiseki.omoshiroikamo.api.client.RenderUtils.rotateIfSneaking(event.entityPlayer);
            JsonModelISBRH.renderToEntity(stack);
            GL11.glPopMatrix();

        }
    }
}
