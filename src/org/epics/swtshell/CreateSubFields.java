/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.swtshell;
import org.epics.pvdata.pv.Field;

/**
 * Create a set of sub fields for a structure or a union.
 * @author mrk
 *
 */
public interface CreateSubFields {
    /**
     * Create the request
     * @param prompt A prompt string.
     * @return (false,true) means (failure,success)
     */
    boolean create(String prompt);
    /**
     * @return the field names.
     */
    String[] getFieldNames();
    /**
     * @return the fields.
     */
    Field[] getFields();
}
