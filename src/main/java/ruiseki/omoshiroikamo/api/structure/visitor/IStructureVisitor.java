package ruiseki.omoshiroikamo.api.structure.visitor;

import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.io.IStructureRequirement;

/**
 * Visitor interface for navigating structure definitions.
 */
public interface IStructureVisitor {

    void visit(IStructureEntry entry);

    void visit(IStructureRequirement requirement);
}
