package ruiseki.omoshiroikamo.core.init;

import lombok.Data;

/**
 * A direct object reference
 * 
 * @author rubensworks
 */
@Data
public class DirectObjectReference<O> implements IObjectReference<O> {

    private final O object;

}
