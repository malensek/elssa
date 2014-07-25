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

import galileo.client.EventPublisher;
import galileo.net.ClientMessageRouter;
import galileo.net.NetworkDestination;

/**
 * Sends events to a HashTestServer instance that will verify the events
 * reached their destination without corruption.
 *
 * @author malensek
 */
public class HashTestClient {

    private ClientMessageRouter messageRouter;
    private EventPublisher publisher;
    private NetworkDestination netDest;

    public HashTestClient(NetworkDestination netDest) throws Exception {
        this.netDest = netDest;
        messageRouter = new ClientMessageRouter();
        publisher = new EventPublisher(messageRouter);
    }

    public void disconnect() {
        messageRouter.shutdown();
    }

    public void test(int size, int messages)
    throws Exception {
        test(size, messages, false);
    }

    public void test(int size, int messages, boolean corrupt)
    throws Exception {
        for (int i = 0; i < messages; ++i) {
            HashTestEvent hte = new HashTestEvent(size);
            if (hte.verify() == false) {
                System.out.println("Local event corrupted!");
            }
            if (corrupt) {
                hte.corrupt();
            }
            publisher.publish(netDest, hte);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println(
                    "Usage: HashTestClient <server> <msg_size> <num_msgs>");
            System.out.println(
                    "Give a fourth argument to send corrupted messages");
            return;
        }

        String hostname = args[0];
        int size = Integer.parseInt(args[1]);
        int messages = Integer.parseInt(args[2]);
        boolean corrupt = (args.length == 4);

        NetworkDestination netDest = new NetworkDestination(
                hostname, BufferTestServer.PORT);
        HashTestClient htc = new HashTestClient(netDest);

        if (corrupt == false) {
            htc.test(size, messages);
        } else {
            System.out.println("Corrupt message test");
            htc.test(size, messages, true);
        }

        System.out.println("Test complete");
        htc.disconnect();
    }
}
