package ruiseki.omoshiroikamo.module.machinery.common.tile.mana;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType.Direction;
import ruiseki.omoshiroikamo.api.modular.IPortType.Type;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.tileentity.AbstractTE;
import ruiseki.omoshiroikamo.core.util.ManaStorage;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.ManaNetworkEvent;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.core.handler.ManaNetworkHandler;

public abstract class AbstractManaPortTE extends AbstractTE implements IModularPort, ISparkAttachable, IManaPool {

    @NBTPersist
    protected int tier = 0; // 0-15 (display: 1-16)

    @NBTPersist
    protected ManaStorage manaStorage;

    public AbstractManaPortTE() {
        // Capacity and transfer will be set by setTier() in onBlockPlacedBy or readCommon
        manaStorage = new ManaStorage(10000, 1000) {

            @Override
            protected void onManaChanged() {
                super.onManaChanged();
                markDirty();
            }
        };
    }

    @Override
    public int getCurrentMana() {
        return manaStorage.getManaStored();
    }

    @Override
    public boolean isFull() {
        return manaStorage.isFull();
    }

    @Override
    public int getAvailableSpaceForMana() {
        return manaStorage.getMaxManaStored() - manaStorage.getManaStored();
    }

    @Override
    public void recieveMana(int amount) {
        if (amount >= 0) {
            manaStorage.receiveMana(amount, false);
        } else {
            manaStorage.extractMana(-amount, false);
        }
    }

    @Override
    public boolean canAttachSpark(ItemStack stack) {
        return true;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {}

    @Override
    public void attachSpark(ISparkEntity entity) {}

    @Override
    public ISparkEntity getAttachedSpark() {
        List<ISparkEntity> sparks = worldObj.getEntitiesWithinAABB(
            ISparkEntity.class,
            AxisAlignedBB.getBoundingBox(xCoord, yCoord + 1, zCoord, xCoord + 1, yCoord + 2, zCoord + 1));

        return sparks.size() == 1 ? sparks.get(0) : null;
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return false;
    }

    @Override
    public abstract boolean canRecieveManaFromBursts();

    public void renderHUD(Minecraft mc, ScaledResolution res) {
        String name = getLocalizedName();
        int color = 0x4444FF;
        HUDHandler.drawSimpleManaHUD(color, manaStorage.getManaStored(), manaStorage.getMaxManaStored(), name, res);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        mc.renderEngine.bindTexture(HUDHandler.manaBar);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public boolean isOutputtingPower() {
        return false;
    }

    @Override
    public EnumIO getSideIO(ForgeDirection side) {
        return EnumIO.BOTH;
    }

    @Override
    public void setSideIO(ForgeDirection side, EnumIO state) {}

    @Override
    public boolean isActive() {
        return false;
    }

    /**
     * Extract energy for machine processing.
     *
     * @param amount requested amount
     * @return amount actually extracted
     */
    public int extractMana(int amount) {
        int extracted = Math.min(manaStorage.getManaStored(), amount);
        manaStorage.voidMana(extracted);
        return extracted;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (!worldObj.isRemote) {
            if (!ManaNetworkHandler.instance.isPoolIn(this) && !isInvalid()) {
                ManaNetworkEvent.addPool(this);
                return true;
            }
        }

        return false;
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        // tier field is loaded by @NBTPersist before this method
        updateManaCapacity();
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        ManaNetworkEvent.removePool(this);
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        ManaNetworkEvent.removePool(this);
    }

    @Override
    public String getLocalizedName() {
        // Use format string from lang file: tile.modularManaInput.name=Mana Input Port Tier %d
        String unlocalizedName = getUnlocalizedName() + ".name";
        String format = StatCollector.translateToLocal(unlocalizedName);
        return String.format(format, getTier() + 1);
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void setTier(int tier) {
        if (this.tier != tier) {
            this.tier = tier;
            updateManaCapacity();
            markDirty();
        }
    }

    /**
     * Update mana storage capacity and transfer rate based on current tier.
     * Called when tier changes or after NBT load.
     */
    protected void updateManaCapacity() {
        int newCapacity = MachineryConfig.getManaPortCapacity(tier + 1);
        int newTransfer = MachineryConfig.getManaPortTransfer(tier + 1);
        manaStorage.setCapacity(newCapacity);
        manaStorage.setMaxTransfer(newTransfer);
    }

    @Override
    public Type getPortType() {
        return Type.MANA;
    }

    @Override
    public abstract Direction getPortDirection();

}
