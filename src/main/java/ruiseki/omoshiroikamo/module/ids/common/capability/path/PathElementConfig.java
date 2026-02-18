package ruiseki.omoshiroikamo.module.ids.common.capability.path;

import ruiseki.omoshiroikamo.api.capabilities.Capability;
import ruiseki.omoshiroikamo.api.capabilities.CapabilityInject;
import ruiseki.omoshiroikamo.api.ids.path.IPathElement;

public class PathElementConfig {

    @CapabilityInject(IPathElement.class)
    public static Capability<IPathElement> CAPABILITY = null;
}
