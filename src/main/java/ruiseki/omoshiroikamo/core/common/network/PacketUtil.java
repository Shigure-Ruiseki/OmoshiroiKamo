package ruiseki.omoshiroikamo.core.common.network;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.ModularUI;

import cpw.mods.fml.common.FMLCommonHandler;
import io.netty.buffer.ByteBuf;

public class PacketUtil {

    public static NBTTagCompound readNBTTagCompound(ByteBuf buf) {
        try {
            short size = buf.readShort();
            if (size < 0) return null;

            byte[] data = new byte[size];
            buf.readBytes(data);
            return CompressedStreamTools.func_152457_a(data, NBTSizeTracker.field_152451_a);
        } catch (IOException e) {
            FMLCommonHandler.instance()
                .raiseException(e, "PacketUtil.readNBTTagCompound", true);
            return null;
        }
    }

    public static void writeNBTTagCompound(@Nullable NBTTagCompound nbt, ByteBuf buf) {
        try {
            if (nbt == null) {
                buf.writeShort(-1);
                return;
            }
            byte[] data = CompressedStreamTools.compress(nbt);
            buf.writeShort(data.length);
            buf.writeBytes(data);
        } catch (IOException e) {
            FMLCommonHandler.instance()
                .raiseException(e, "PacketUtil.writeNBTTagCompound", true);
        }
    }

    public static byte[] readByteArray(ByteBuf buf) {
        int size = buf.readMedium();
        byte[] res = new byte[size];
        buf.readBytes(res);
        return res;
    }

    public static void writeByteArray(ByteBuf buf, byte[] arr) {
        buf.writeMedium(arr.length);
        buf.writeBytes(arr);
    }

    public static void writeStringSafe(ByteBuf buf, @Nullable String str) {
        writeStringSafe(buf, str, Short.MAX_VALUE, false);
    }

    public static void writeStringSafe(ByteBuf buf, @Nullable String str, int maxBytes, boolean crash) {
        maxBytes = Math.min(maxBytes, Short.MAX_VALUE);
        if (str == null) {
            writeVarInt(buf, Short.MAX_VALUE + 1);
            return;
        }

        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > maxBytes) {
            if (crash) {
                throw new IllegalArgumentException("String too long: " + bytes.length + " > max " + maxBytes);
            }
            byte[] tmp = new byte[maxBytes];
            System.arraycopy(bytes, 0, tmp, 0, maxBytes);
            bytes = tmp;
            ModularUI.LOGGER.warn("Warning! Synced string exceeds max length: {}", str);
        }

        writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }

    @Nullable
    public static String readStringSafe(ByteBuf buf) {
        int length = readVarInt(buf);
        if (length > Short.MAX_VALUE) return null;

        String s = buf.toString(buf.readerIndex(), length, StandardCharsets.UTF_8);
        buf.readerIndex(buf.readerIndex() + length);
        return s;
    }

    public static void writeVarInt(ByteBuf buf, int value) {
        while ((value & 0xFFFFFF80) != 0L) {
            buf.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        buf.writeByte(value & 0x7F);
    }

    public static int readVarInt(ByteBuf buf) {
        int numRead = 0;
        int result = 0;
        byte read;

        do {
            read = buf.readByte();
            result |= (read & 0x7F) << (7 * numRead);
            numRead++;
            if (numRead > 5) throw new RuntimeException("VarInt too big");
        } while ((read & 0x80) != 0);

        return result;
    }
}
