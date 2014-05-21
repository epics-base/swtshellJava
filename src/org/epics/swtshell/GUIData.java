/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.swtshell;

import org.eclipse.swt.widgets.Shell;
import org.epics.pvdata.misc.BitSet;
import org.epics.pvdata.pv.PVScalar;
import org.epics.pvdata.pv.PVScalarArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVStructureArray;
import org.epics.pvdata.pv.PVUnion;
import org.epics.pvdata.pv.PVUnionArray;


/**
 * @author mrk
 *
 */
public interface GUIData {
    void getStructure(Shell parent,PVStructure pvStructure,BitSet bitSet);
    boolean getScalarValue(Shell parent,PVScalar pvScalar);
    boolean getUnionValue(Shell parent,PVUnion pvUnion);
    boolean getScalarArray(Shell parent,PVScalarArray pvScalarArray);
    boolean getStructureArray(Shell parent,PVStructureArray pvStructureArray);
    boolean getUnionArray(Shell parent,PVUnionArray pvUnionArray);
}
