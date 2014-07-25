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
import java.util.Random;

import galileo.client.EventPublisher;
import galileo.comm.StorageRequest;
import galileo.dataset.Block;
import galileo.net.ClientMessageRouter;
import galileo.net.NetworkDestination;

/**
 * Tests client non-blocking send operations.
 *
 * @author malensek
 */
public class BufferTestClient {

    private static final int MESSAGE_SIZE = 1024;

    private ClientMessageRouter messageRouter;
    private EventPublisher publisher;
    private NetworkDestination netDest;
    private Random random = new Random();

    public BufferTestClient(NetworkDestination netDest) throws Exception {
        this.netDest = netDest;
        messageRouter = new ClientMessageRouter();
        publisher = new EventPublisher(messageRouter);
    }

    public void disconnect() {
        messageRouter.shutdown();
    }

    public void test(int messages)
    throws Exception {
        for (int i = 0; i < messages; ++i) {
            byte[] randomBytes = new byte[MESSAGE_SIZE];
            random.nextBytes(randomBytes);
            Block block = new Block(randomBytes);
            StorageRequest store = new StorageRequest(block);
            publisher.publish(netDest, store);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println(
                    "Usage: BufferTestClient <server> <num_messages>");
            return;
        }

        String hostname = args[0];
        int messages = Integer.parseInt(args[1]);

        NetworkDestination netDest = new NetworkDestination(
                hostname, BufferTestServer.PORT);
        BufferTestClient btc = new BufferTestClient(netDest);

        try {
            btc.test(messages);
        } catch (IOException e) {
            System.out.println("Failed to send all messages!");
        }
        System.out.println("Test complete");
        btc.disconnect();
    }
}
