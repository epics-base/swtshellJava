/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS pvData is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.swtshell;

import org.epics.pvaccess.client.ChannelRequester;

/**
 * Requester for a CreateRequestArg.
 * @author mrk
 *
 */
public interface CreateRequestArgRequester extends ChannelRequester
{
	/**
	 * Get the default field request string.
	 * @return The string.
	 */
	String getDefault();
    /**
     * The request has been created.
     * @param request The request.
     */
    void request(String request);
}
