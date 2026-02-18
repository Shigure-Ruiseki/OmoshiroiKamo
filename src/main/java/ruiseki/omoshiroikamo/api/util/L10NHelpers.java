package ruiseki.omoshiroikamo.api.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import ruiseki.omoshiroikamo.api.persist.nbt.INBTSerializable;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * A set of localization helpers.
 *
 * @author rubensworks
 */
public final class L10NHelpers {

    /**
     * Holder class that acts as a parameterized unlocalized string.
     * This can also take other unlocalized strings in the parameter list, and they will recursively
     * be localized when calling {@link UnlocalizedString#localize()}.
     */
    public static class UnlocalizedString implements INBTSerializable {

        private String parameterizedString;
        private Object[] parameters;

        public UnlocalizedString(String parameterizedString, Object... parameters) {
            this.parameterizedString = parameterizedString;
            this.parameters = parameters;
            for (int i = 0; i < parameters.length; i++) {
                if (!(parameters[i] instanceof UnlocalizedString || parameters[i] instanceof String)) {
                    parameters[i] = String.valueOf(parameters[i]);
                }
            }
        }

        public UnlocalizedString() {
            this.parameterizedString = null;
            this.parameters = null;
        }

        public String localize() {
            Object[] realParameters = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Object param = parameters[i];
                if (param instanceof UnlocalizedString) {
                    realParameters[i] = ((UnlocalizedString) param).localize();
                } else {
                    realParameters[i] = param;
                }
            }
            return LibMisc.LANG.localize(parameterizedString, realParameters);
        }

        @Override
        public NBTTagCompound toNBT() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("parameterizedString", parameterizedString);
            NBTTagList list = new NBTTagList();
            for (Object parameter : parameters) {
                if (parameter instanceof UnlocalizedString) {
                    NBTTagCompound objectTag = ((UnlocalizedString) parameter).toNBT();
                    objectTag.setString("type", "object");
                    list.appendTag(objectTag);
                } else {
                    NBTTagCompound stringTag = new NBTTagCompound();
                    stringTag.setTag("value", new NBTTagString((String) parameter));
                    stringTag.setString("type", "string");
                    list.appendTag(stringTag);
                }
            }
            tag.setTag("parameters", list);
            return tag;
        }

        @Override
        public void fromNBT(NBTTagCompound tag) {
            this.parameterizedString = tag.getString("parameterizedString");
            NBTTagList list = tag.getTagList("parameters", MinecraftHelpers.NBTTag_Types.NBTTagCompound.ordinal());
            this.parameters = new Object[list.tagCount()];
            for (int i = 0; i < this.parameters.length; i++) {
                NBTTagCompound elementTag = list.getCompoundTagAt(i);
                if ("object".equals(elementTag.getString("type"))) {
                    UnlocalizedString object = new UnlocalizedString();
                    object.fromNBT(elementTag);
                    this.parameters[i] = object;
                } else {
                    this.parameters[i] = elementTag.getString("value");
                }
            }
        }

    }

}
