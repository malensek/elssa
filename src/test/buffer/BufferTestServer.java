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

package galileo.test.buffer;

import java.io.IOException;

import java.util.Random;

import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;
import galileo.net.ServerMessageRouter;

/**
 * Receives incoming messages from clients and increments a message counter.
 * Each time a client disconnects, the total number of messages received is
 * reported.
 *
 * @author malensek
 */
public class BufferTestServer implements MessageListener {

    protected static final int PORT = 5050;

    private int counter;
    private ServerMessageRouter messageRouter;
    private Random random = new Random();

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
        System.out.println("Client disconnect: " + endpoint);
        System.out.println("Number of messages received so far: " + counter);
    }

    @Override
    public void onMessage(GalileoMessage message) {
        counter++;
        try {
            /* We sleep here to help force the client-side buffer to fill up.
             * It's worth noting that doing something like this in production is
             * a very, very bad idea because it blocks the MessageRouter thread.
             * onMessage should do as little (non-blocking) work as possible. */
            Thread.sleep(random.nextInt(5));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted!");
        }
    }

    public static void main(String[] args) throws Exception {
        BufferTestServer bts = new BufferTestServer();
        bts.listen();
    }
}
