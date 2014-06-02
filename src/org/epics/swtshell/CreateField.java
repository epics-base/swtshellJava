/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.swtshell;
import org.epics.pvdata.pv.Field;

/**
 * Create a Field
 * @author mrk
 *
 */
public interface CreateField {
    /**
     * Create the field.
     */
    Field create(String prompt);
}
