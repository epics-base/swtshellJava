/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.swtshell;
import org.epics.pvdata.pv.Structure;

/**
 * Create a Structure
 * @author mrk
 *
 */
public interface CreateStructure {
    /**
     * Create the request
     */
    Structure create(String prompt);
}
