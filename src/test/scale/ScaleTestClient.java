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

package galileo.test.scale;

import java.io.IOException;

import galileo.net.ClientMessageRouter;
import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;
import galileo.test.util.PerformanceTimer;

public class ScaleTestClient implements MessageListener, Runnable {

    private static boolean verbose = false;

    private ClientMessageRouter messageRouter;
    private NetworkDestination netDest;
    private PerformanceTimer pt = new PerformanceTimer("response");

    public ScaleTestClient(NetworkDestination netDest) throws Exception {
        this.netDest = netDest;
        messageRouter = new ClientMessageRouter();
        messageRouter.addListener(this);
    }

    @Override
    public void onConnect(NetworkDestination endpoint) { }

    @Override
    public void onDisconnect(NetworkDestination endpoint) {
        System.out.println("Shutting down");
        System.exit(1);
    }

    @Override
    public void run() {
        try {
            while (true) {
                send();
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void send()
    throws IOException {
        byte[] payload = new byte[ScaleTestServer.QUERY_SIZE];
        if (verbose) {
            pt.start();
        }
        messageRouter.sendMessage(netDest, new GalileoMessage(payload));
    }

    @Override
    public void onMessage(GalileoMessage message) {
        if (verbose) {
            pt.stopAndPrint();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("galileo.test.net.ScaleTestClient "
                    + "host num-threads");
            System.out.println("Add a 3rd parameter to turn on verbose mode.");
            return;
        }

        String hostname = args[0];
        int threads = Integer.parseInt(args[1]);
        if (args.length >= 3) {
            ScaleTestClient.verbose = true;
        }

        PerformanceTimer tt = new PerformanceTimer("StartThreads");
        tt.start();
        for (int i = 0; i < threads; ++i) {
            NetworkDestination netDest = new NetworkDestination(
                    hostname, ScaleTestServer.PORT);

            ScaleTestClient stc = new ScaleTestClient(netDest);
            new Thread(stc).start();
        }
        tt.stopAndPrint();
    }
}
