package ruiseki.omoshiroikamo.module.machinery.common.tile.mana;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import ruiseki.omoshiroikamo.api.mana.ManaStorage;
import ruiseki.omoshiroikamo.api.modular.IManaPort;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.ManaNetworkEvent;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.core.handler.ManaNetworkHandler;

// Mana Port doesn't have Side Config because mana will transfer directly
public abstract class AbstractManaPortTE extends AbstractTE implements IManaPort, ISparkAttachable, IManaPool {

    protected ManaStorage manaStorage;

    public AbstractManaPortTE(int manaCapacity, int manaMaxReceive) {
        manaStorage = new ManaStorage(manaCapacity, manaMaxReceive) {

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
    public IO getSideIO(ForgeDirection side) {
        return IO.BOTH;
    }

    @Override
    public void setSideIO(ForgeDirection side, IO state) {}

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
        return LibMisc.LANG.localize(getUnlocalizedName() + ".tier_" + getTier() + ".name");
    }

    public abstract int getTier();

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        manaStorage.writeToNBT(root);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        manaStorage.readFromNBT(root);
    }
}
