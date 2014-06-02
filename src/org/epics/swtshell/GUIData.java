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
 * Get data values from the client.
 * @author mrk
 *
 */
public interface GUIData {
    /**
     * Get data for a pvStructure.
     * @param parent The parent shell.
     * @param pvStructure The structure into which data should be written.
     * @param bitSet The bitSet which shows what fields are changed.
     */
    void getStructure(Shell parent,PVStructure pvStructure,BitSet bitSet);
    /**
     * Get data for a scalar field.
     * @param parent The parent shell.
     * @param pvScalar The field.
     * @return
     */
    boolean getScalarValue(Shell parent,PVScalar pvScalar);
    /**
     * Get data for a union field.
     * @param parent The parent shell.
     * @param pvUnion The field.
     * @return
     */
    boolean getUnionValue(Shell parent,PVUnion pvUnion);
    /**
     * Get data for a scalar array field.
     * @param parent The parent shell.
     * @param pvScalarArray The field.
     * @return
     */
    boolean getScalarArray(Shell parent,PVScalarArray pvScalarArray);
    /**
     * Get data for a structure array field.
     * @param parent The parent shell.
     * @param pvStructureArray The field.
     * @return
     */
    boolean getStructureArray(Shell parent,PVStructureArray pvStructureArray);
    /**
     * Get data for a union array field.
     * @param parent The parent shell.
     * @param pvUnionArray The field.
     * @return
     */
    boolean getUnionArray(Shell parent,PVUnionArray pvUnionArray);
}
