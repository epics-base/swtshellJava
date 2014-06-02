/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.swtshell;
import org.epics.pvdata.pv.Union;

/**
 * Create a Union
 * @author mrk
 *
 */
public interface CreateUnion {
    /**
     * Create a union
     * @param prompt A prompt string
     * @return The Union.
     */
    Union create(String prompt);
    /**
     * create a variant union.
     * @return The Union
     */
    Union createVariant();
}
