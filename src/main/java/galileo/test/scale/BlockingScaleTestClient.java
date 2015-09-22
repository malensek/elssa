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

import java.net.Socket;

import java.nio.ByteBuffer;

import galileo.net.MessageRouter;
import galileo.net.NetworkDestination;
import galileo.test.util.PerformanceTimer;

public class BlockingScaleTestClient implements Runnable {

    private static boolean verbose = false;

    private PerformanceTimer pt = new PerformanceTimer("response");
    private Socket socket;

    public BlockingScaleTestClient(NetworkDestination netDest) throws Exception {
        socket = new Socket(netDest.getHostname(), netDest.getPort());
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] payload = new byte[ScaleTestServer.QUERY_SIZE];

                ByteBuffer buffer = ByteBuffer.allocate(
                        payload.length + MessageRouter.PREFIX_SZ);
                buffer.putInt(ScaleTestServer.QUERY_SIZE);
                buffer.put(payload);
                buffer.flip();
                byte[] data = buffer.array();

                if (verbose) {
                    pt.start();
                }
                socket.getOutputStream().write(data);
                byte[] reply = new byte[ScaleTestServer.REPLY_SIZE];
                socket.getInputStream().read(reply);
                if (verbose) {
                    pt.stopAndPrint();
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
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
            BlockingScaleTestClient.verbose = true;
        }

        for (int i = 0; i < threads; ++i) {
            NetworkDestination netDest = new NetworkDestination(
                    hostname, ScaleTestServer.PORT);

            BlockingScaleTestClient stc = new BlockingScaleTestClient(netDest);
            new Thread(stc).start();
        }
    }
}
