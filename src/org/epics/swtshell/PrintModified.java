/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.swtshell;

import org.epics.pvdata.misc.BitSet;
import org.epics.pvdata.pv.PVStructure;

/**
 * Print the fields of a PVStructure that have been modified.
 * @author mrk
 *
 */
public interface PrintModified {
    /**
     * Print all modified fields.
     * @param pvStructure The data
     * @param changedBitSet The change bit set.
     * @param overrunBitSet The bit set showing which fields were changed more than once
     */
    void print(PVStructure pvStructure,BitSet changeBitSet,BitSet overrunBitSet);
    /**
     * Print all modified fields.
     * @param pvStructure The data
     * @param changedBitSet The change bit set.
     * @param overrunBitSet The bit set showing which fields were changed more than once
     */
    void setArgs(PVStructure pvStructure,BitSet changeBitSet,BitSet overrunBitSet);
    /**
     * Can be called after setArgs.
     */
    void print();
}
