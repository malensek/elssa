/*
Copyright (c) 2013, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

package io.elssa.net;

/**
 * Interface for classes that will listen for incoming {@link ElssaMessage}
 * instances produced by a {@link MessageRouter}.
 *
 * @author malensek
 */
public interface MessageListener {

    /**
     * Called when a message is ready to be processed.  This method is invoked
     * by a worker thread group, and should generally be lightweight to avoid
     * consuming network processing resources with business logic.
     */
    public void onMessage(ElssaMessage message);

    /**
     * Called when a connection is established with a remote endpoint.
     */
    public void onConnect(NetworkEndpoint endpoint);

    /**
     * Called when the MessageListener has been disconnected from a remote
     * endpoint.
     */
    public void onDisconnect(NetworkEndpoint endpoint);
}
