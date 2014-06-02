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
     * get an integer.
     * @param prompt A prompt.
     * @return and integer.
     */
    int getInteger(String prompt);
}
