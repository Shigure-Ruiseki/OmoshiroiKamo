package ruiseki.omoshiroikamo.module.cable.common.conduit.packet;

import ruiseki.omoshiroikamo.module.cable.common.conduit.IConduit;

public enum ConTypeEnum {
    ;

    final Class<? extends IConduit> baseType;

    private ConTypeEnum(Class<? extends IConduit> baseType) {
        this.baseType = baseType;
    }

    public Class<? extends IConduit> getBaseType() {
        return baseType;
    }

    public static ConTypeEnum get(IConduit con) {
        Class<? extends IConduit> from = con.getBaseConduitType();
        for (ConTypeEnum ct : ConTypeEnum.values()) {
            if (ct.getBaseType() == from) {
                return ct;
            }
        }
        return null;
    }
}
