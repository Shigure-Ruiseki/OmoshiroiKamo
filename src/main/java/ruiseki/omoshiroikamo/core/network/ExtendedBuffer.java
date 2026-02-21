package ruiseki.omoshiroikamo.core.network;

import net.minecraft.network.PacketBuffer;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

/**
 * An extended packet buffer.
 * 
 * @author rubensworks
 */
public class ExtendedBuffer extends PacketBuffer {

    public ExtendedBuffer(ByteBuf wrapped) {
        super(wrapped);
    }

    public String readString() {
        return ByteBufUtils.readUTF8String(this);
    }

    public ExtendedBuffer writeString(String string) {
        ByteBufUtils.writeUTF8String(this, string);
        return this;
    }

}
