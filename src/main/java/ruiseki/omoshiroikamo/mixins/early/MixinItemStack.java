package ruiseki.omoshiroikamo.mixins.early;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ruiseki.omoshiroikamo.api.capabilities.Capability;
import ruiseki.omoshiroikamo.api.capabilities.CapabilityDispatcher;
import ruiseki.omoshiroikamo.api.capabilities.ICapabilityProvider;
import ruiseki.omoshiroikamo.api.capabilities.ICapabilitySerializable;
import ruiseki.omoshiroikamo.api.capabilities.IItemCapability;
import ruiseki.omoshiroikamo.core.common.event.OKEventFactory;

@Mixin(ItemStack.class)
@SuppressWarnings("unused")
public abstract class MixinItemStack implements ICapabilitySerializable<NBTTagCompound> {

    @Shadow
    private Item field_151002_e; // item
    @Shadow
    public NBTTagCompound stackTagCompound; // tag

    @Unique
    private CapabilityDispatcher capabilities;

    @Shadow
    public abstract void readFromNBT(NBTTagCompound nbt);

    @Shadow
    public abstract NBTTagCompound writeToNBT(NBTTagCompound nbt);

    @Unique
    private void ok$forgeInit() {
        ItemStack stack = (ItemStack) (Object) this;

        if (field_151002_e != null && field_151002_e instanceof IItemCapability capability) {
            ICapabilityProvider provider = capability.initCapabilities(stack, stack.stackTagCompound);

            this.capabilities = OKEventFactory.gatherCapabilities(stack, provider);

            if (stackTagCompound != null && stackTagCompound.hasKey("ForgeCaps") && this.capabilities != null) {
                this.capabilities.deserializeNBT(stackTagCompound.getCompoundTag("ForgeCaps"));
            }
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/item/Item;II)V", at = @At("RETURN"))
    private void ok$onConstruct(Item item, int size, int damage, CallbackInfo ci) {
        ok$forgeInit();
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable ForgeDirection facing) {
        ItemStack stack = (ItemStack) (Object) this;

        if (stack.stackSize <= 0 || this.capabilities == null) return false;

        return this.capabilities.hasCapability(capability, facing);
    }

    @Override
    public @Nullable <T> T getCapability(Capability<T> capability, ForgeDirection facing) {
        ItemStack stack = (ItemStack) (Object) this;

        if (stack.stackSize <= 0 || this.capabilities == null) return null;

        return this.capabilities.getCapability(capability, facing);
    }

    @Unique
    public void deserializeNBT(NBTTagCompound nbt) {
        final ItemStack itemStack = ItemStack.loadItemStackFromNBT(nbt);
        this.stackTagCompound = itemStack.stackTagCompound;
    }

    @Unique
    public NBTTagCompound serializeNBT() {
        NBTTagCompound ret = new NBTTagCompound();
        this.writeToNBT(ret);
        return ret;
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void ok$writeCaps(NBTTagCompound nbt, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (this.capabilities != null) {
            NBTTagCompound cnbt = this.capabilities.serializeNBT();
            if (!cnbt.hasNoTags()) {
                cir.getReturnValue()
                    .setTag("OKCaps", cnbt);
            }
        }
    }

    @Inject(method = "readFromNBT", at = @At("RETURN"))
    private void ok$readCaps(NBTTagCompound nbt, CallbackInfo ci) {

        if (this.capabilities != null && nbt.hasKey("OKCaps")) {
            this.capabilities.deserializeNBT(nbt.getCompoundTag("OKCaps"));
        }
    }
}
