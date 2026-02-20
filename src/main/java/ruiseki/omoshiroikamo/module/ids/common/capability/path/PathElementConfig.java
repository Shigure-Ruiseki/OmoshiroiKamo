package ruiseki.omoshiroikamo.module.ids.common.capability.path;

import ruiseki.omoshiroikamo.api.ids.path.IPathElement;
import ruiseki.omoshiroikamo.core.capabilities.Capability;
import ruiseki.omoshiroikamo.core.capabilities.CapabilityInject;

public class PathElementConfig {

    @CapabilityInject(IPathElement.class)
    public static Capability<IPathElement> CAPABILITY = null;
}
