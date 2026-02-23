package ruiseki.omoshiroikamo.module.ids.common.tileentity;

import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import lombok.Data;
import lombok.Setter;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.datastructure.DimPos;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.tileentity.AbstractTickingTE;
import ruiseki.omoshiroikamo.module.ids.common.block.ICableConnectable;
import ruiseki.omoshiroikamo.module.ids.common.part.EnumPartType;
import ruiseki.omoshiroikamo.module.ids.common.part.IPart;
import ruiseki.omoshiroikamo.module.ids.common.part.IPartContainer;
import ruiseki.omoshiroikamo.module.ids.common.part.IPartState;

public class TileMultipartTicking extends AbstractTickingTE implements IPartContainer {

    @Setter
    private BlockState connectionState;

    private final Map<ForgeDirection, PartStateHolder<?, ?>> partData = Maps.newHashMap();

    public TileMultipartTicking() {}

    @Override
    public void writeCommon(NBTTagCompound tag) {
        super.writeCommon(tag);
        NBTTagList partList = new NBTTagList();
        for (Map.Entry<ForgeDirection, PartStateHolder<?, ?>> entry : partData.entrySet()) {
            NBTTagCompound partTag = new NBTTagCompound();
            IPart part = entry.getValue()
                .getPart();
            IPartState partState = entry.getValue()
                .getState();
            partTag.setString(
                "__partType",
                part.getType()
                    .getName());
            partTag.setString(
                "__side",
                entry.getKey()
                    .name());
            part.toNBT(partTag, partState);
            partList.appendTag(partTag);
        }
        tag.setTag("parts", partList);
    }

    @Override
    public void readCommon(NBTTagCompound tag) {
        super.readCommon(tag);
        NBTTagList partList = tag.getTagList("parts", MinecraftHelpers.NBTTag_Types.NBTTagCompound.getId());
        for (int i = 0; i < partList.tagCount(); i++) {
            NBTTagCompound partTag = partList.getCompoundTagAt(i);
            EnumPartType type = EnumPartType.getInstance(partTag.getString("__partType"));
            if (type != null) {
                ForgeDirection side = ForgeDirection.valueOf(partTag.getString("__side"));
                if (side != null) {
                    IPart part = type.getPart();
                    IPartState partState = part.fromNBT(partTag);
                    partData.put(side, PartStateHolder.of(part, partState));
                } else {
                    OmoshiroiKamo.okLog(
                        Level.WARN,
                        String.format(
                            "The part %s at position %s was at an invalid " + "side and removed.",
                            type,
                            getPosition()));
                }
            } else {
                OmoshiroiKamo.okLog(
                    Level.WARN,
                    String.format(
                        "The part %s at position %s was unknown and removed.",
                        partTag.getString("__partType"),
                        getPosition()));
            }
        }
    }

    @Override
    public DimPos getPosition() {
        return DimPos.of(getWorldObj().provider.dimensionId, getPos());
    }

    @Override
    public Map<ForgeDirection, IPart<?, ?>> getParts() {
        return Maps.transformValues(partData, new Function<PartStateHolder<?, ?>, IPart<?, ?>>() {

            @Nullable
            @Override
            public IPart<?, ?> apply(@Nullable PartStateHolder<?, ?> input) {
                return input.getPart();
            }
        });
    }

    @Override
    public void setPart(ForgeDirection side, IPart part) {
        partData.put(side, PartStateHolder.of(part, part.getDefaultState()));
    }

    @Override
    public IPart getPart(ForgeDirection side) {
        return partData.get(side)
            .getPart();
    }

    @Override
    public boolean hasPart(ForgeDirection side) {
        return partData.containsKey(side);
    }

    @Override
    public IPart removePart(ForgeDirection side) {
        return partData.remove(side)
            .getPart();
    }

    @Override
    public void setPartState(ForgeDirection side, IPartState partState) {
        PartStateHolder<?, ?> partStateHolder = partData.get(side);
        if (partStateHolder == null) {
            throw new IllegalArgumentException(
                String.format("No part at position %s was found to update the state " + "for.", getPosition()));
        }
        partData.put(side, PartStateHolder.of(partStateHolder.getPart(), partState));
    }

    @Override
    public IPartState getPartState(ForgeDirection side) {
        PartStateHolder<?, ?> partStateHolder = partData.get(side);
        if (partStateHolder == null) {
            throw new IllegalArgumentException(
                String.format("No part at position %s was found to get the state from.", getPosition()));
        }
        return partStateHolder.getState();
    }

    public BlockState getConnectionState() {
        if (connectionState == null) {
            connectionState = ((ICableConnectable) getBlock()).updateConnections(getWorldObj(), getPos());
        }
        return connectionState;
    }

    @Data
    private static class PartStateHolder<P extends IPart<P, S>, S extends IPartState<P>> {

        private final IPart<P, S> part;
        private final S state;

        public static PartStateHolder<?, ?> of(IPart part, IPartState partState) {
            return new PartStateHolder(part, partState);
        }

    }
}
