/*
Copyright (c) 2014, Colorado State University
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

package galileo.test.net;

import java.io.IOException;

import galileo.event.EventContainer;
import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;
import galileo.net.ServerMessageRouter;
import galileo.serialization.Serializer;

/**
 * Receives messages with random binary payloads (HashTestEvents), and verifies
 * their checksums.
 *
 * @author malensek
 */
public class HashTestServer implements MessageListener {

    protected static final int PORT = 5050;

    private long counter;
    private long bad;
    private ServerMessageRouter messageRouter;

    public void listen()
    throws IOException {
        messageRouter = new ServerMessageRouter();
        messageRouter.addListener(this);
        messageRouter.listen(PORT);
        System.out.println("Listening...");
    }

    @Override
    public void onConnect(NetworkDestination endpoint) {
        System.out.println("Accepting connection from " + endpoint);
    }

    @Override
    public void onDisconnect(NetworkDestination endpoint) {
        System.out.println("Client disconnected: " + endpoint);
        printStats();
    }

    private void printStats() {
        System.out.println("Processed " + counter + " messages");
        System.out.println(counter - bad + " good events");
        System.out.println(bad + " bad events");
    }

    @Override
    public void onMessage(GalileoMessage message) {
        counter++;
        try {
            EventContainer container = Serializer.deserialize(
                    EventContainer.class, message.getPayload());
            HashTestEvent event = Serializer.deserialize(
                    HashTestEvent.class, container.getEventPayload());

            if (event.verify() == false) {
                bad++;
                System.out.println("Corrupted event detected!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (counter % 1000 == 0) {
            printStats();
        }
    }

    public static void main(String[] args) throws Exception {
        HashTestServer hts = new HashTestServer();
        hts.listen();
    }
}
