/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.swtshell;

/**
 * Get number of something.
 * @author mrk
 *
 */
public interface GetInteger {
    /**
     * Create the request
     */
    int getInteger(String prompt);
}
