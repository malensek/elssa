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

package galileo.test.xfer;

import java.io.IOException;
import java.util.Random;

import galileo.net.ClientMessageRouter;
import galileo.net.GalileoMessage;
import galileo.net.NetworkDestination;
import galileo.net.Transmission;
import galileo.test.util.PerformanceTimer;
import galileo.test.util.PerformanceTimer.PerformanceSample;

/**
 * Tests client non-blocking send operations.
 *
 * @author malensek
 */
public class XferTestClient {

    private ClientMessageRouter messageRouter;
    private NetworkDestination server;
    private Random random = new Random();
    private PerformanceTimer pt;

    public XferTestClient(NetworkDestination server)
    throws Exception {
        this.server = server;
        messageRouter = new ClientMessageRouter();
    }

    public void disconnect() {
        messageRouter.shutdown();
    }

    public void test(int size, int numMessages)
    throws Exception {
        byte[] randomBytes = new byte[size];
        pt = new PerformanceTimer("send");
        pt.start();
        for (int i = 0; i < numMessages; ++i) {
            GalileoMessage msg = new GalileoMessage(randomBytes);
            Transmission t = messageRouter.sendMessage(server, msg);
            t.finish();
        }
        pt.stop();

        double totalTime = pt.getLastResult();
        long totalSize = (size * numMessages);
        double totalMB = totalSize / (1000.0 * 1000.0);

        System.out.format("Sent %.2f MB in %.2fs (%.2f MB/s).\t\t"
                + "[%d-byte messages]%n",
                totalMB, totalTime * 1E-3, totalMB / (totalTime * 1E-3), size);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println(
                    "Usage: XferTestClient <server>");
            return;
        }

        String hostname = args[0];
        int messages = Integer.parseInt(args[1]);

        NetworkDestination server = new NetworkDestination(
                hostname, XferTestServer.PORT);
        XferTestClient xc = new XferTestClient(server);

        try {
            System.out.println("Sending 10 MB using different message sizes");
            xc.test(10, 1000000);
            xc.test(100, 100000);
            xc.test(1000, 10000);
            xc.test(10000, 1000);
            xc.test(100000, 100);

            System.out.println("Sending a fixed amount of messages: "
                    + messages);
            xc.test(10, messages);
            xc.test(100, messages);
            xc.test(1000, messages);
            xc.test(10000, messages);
            xc.test(100000, messages);
            xc.test(1000000, messages);
        } catch (IOException e) {
            System.out.println("Failed to send all messages!");
        }

        System.out.println("Test complete");
        xc.disconnect();
    }
}
