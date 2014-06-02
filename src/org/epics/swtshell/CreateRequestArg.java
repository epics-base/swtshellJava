/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.swtshell;

/**
 * Create an argument to pass to createRequust.
 * createRequest has a string argument and returns a pvStructure that is passed to pvAccess create methods.
 * @author mrk
 *
 */
public interface CreateRequestArg {
    /**
     * Create the request
     */
    void create();
}
